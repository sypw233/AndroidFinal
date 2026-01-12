package ovo.sypw.wmx420.androidfinal.ui.components.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ovo.sypw.wmx420.androidfinal.data.model.BilibiliRankingItem
import ovo.sypw.wmx420.androidfinal.data.model.ChartData
import ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank.components.RankingVideoCard
import ovo.sypw.wmx420.androidfinal.utils.formatLargeNumber

@Composable
fun CustomBarChart(
    data: ChartData.BarChartData,
    modifier: Modifier = Modifier,
    rankingList: List<BilibiliRankingItem> = emptyList(),
    onVideoClick: ((BilibiliRankingItem) -> Unit)? = null,
    onBarClick: ((Int) -> Unit)? = null
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary
    val outlineColor = MaterialTheme.colorScheme.outline

    var selectedIndex by remember { mutableIntStateOf(-1) }
    var animationProgress by remember { mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 600),
        label = "barAnimation"
    )

    LaunchedEffect(data) {
        animationProgress = 1f
    }

    val values1 = data.values
    val values2 = data.values2
    val labels = data.labels
    val hasTwoGroups = values2 != null

    if (values1.isEmpty()) return

    val maxValue = if (hasTwoGroups) {
        maxOf(values1.maxOrNull() ?: 0f, values2?.maxOrNull() ?: 0f) * 1.1f
    } else {
        (values1.maxOrNull() ?: 0f) * 1.1f
    }

    Column(modifier = modifier) {
        // 图表标题
        Text(
            text = data.title,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 图例
        if (hasTwoGroups) {
            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(primaryColor, RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("点赞", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(secondaryColor, RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("投币", style = MaterialTheme.typography.labelMedium)
            }
        }

        // 图表
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .pointerInput(values1) {
                    detectTapGestures { offset ->
                        val chartWidth = size.width.toFloat()
                        val padding = 50f
                        val availableWidth = chartWidth - padding * 2
                        val barGroupWidth = availableWidth / values1.size
                        val clickedIndex = ((offset.x - padding) / barGroupWidth).toInt()
                            .coerceIn(0, values1.size - 1)
                        selectedIndex = if (selectedIndex == clickedIndex) -1 else clickedIndex
                        onBarClick?.invoke(clickedIndex)
                    }
                }
        ) {
            val chartWidth = size.width
            val chartHeight = size.height
            val padding = 50f
            val topPadding = 10f
            val bottomPadding = 35f
            val availableWidth = chartWidth - padding * 2
            val availableHeight = chartHeight - topPadding - bottomPadding
            val barGroupWidth = availableWidth / values1.size
            val barWidth = if (hasTwoGroups) barGroupWidth * 0.35f else barGroupWidth * 0.6f
            val gap = barGroupWidth * 0.1f

            // Y轴刻度
            for (i in 0..4) {
                val y = topPadding + availableHeight * (1 - i / 4f)
                val value = maxValue * i / 4f
                drawLine(
                    color = outlineColor.copy(alpha = 0.15f),
                    start = Offset(padding, y),
                    end = Offset(chartWidth - padding / 2, y),
                    strokeWidth = 1f
                )
                drawContext.canvas.nativeCanvas.drawText(
                    value.formatLargeNumber(),
                    padding - 8f, y + 5f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                )
            }

            // 柱状图
            values1.forEachIndexed { index, value1 ->
                val isSelected = index == selectedIndex
                val x = padding + index * barGroupWidth + gap

                // 第一组柱子
                val height1 = (value1 / maxValue) * availableHeight * animatedProgress
                val barColor1 = if (isSelected) primaryColor else primaryColor.copy(alpha = 0.85f)
                drawRoundRect(
                    color = barColor1,
                    topLeft = Offset(x, topPadding + availableHeight - height1),
                    size = Size(barWidth, height1),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // 第二组柱子
                if (hasTwoGroups) {
                    val value2 = values2.getOrElse(index) { 0f }
                    val height2 = (value2 / maxValue) * availableHeight * animatedProgress
                    val barColor2 =
                        if (isSelected) secondaryColor else secondaryColor.copy(alpha = 0.85f)
                    drawRoundRect(
                        color = barColor2,
                        topLeft = Offset(x + barWidth + 4f, topPadding + availableHeight - height2),
                        size = Size(barWidth, height2),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                }

                // 选中高亮
                if (isSelected) {
                    val totalBarWidth = if (hasTwoGroups) barWidth * 2 + 4f else barWidth
                    drawRoundRect(
                        color = primaryColor.copy(alpha = 0.1f),
                        topLeft = Offset(x - 4f, topPadding),
                        size = Size(totalBarWidth + 8f, availableHeight),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                }
            }

            // X轴标签
            values1.forEachIndexed { index, _ ->
                val x = padding + index * barGroupWidth + barGroupWidth / 2
                if (index % 2 == 0 || values1.size <= 6) {
                    drawContext.canvas.nativeCanvas.drawText(
                        labels.getOrElse(index) { "" },
                        x, chartHeight - 8f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 26f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }

        // 选中详情 - 显示视频卡片
        if (selectedIndex in values1.indices && selectedIndex in rankingList.indices) {
            val selectedItem = rankingList[selectedIndex]
            Spacer(modifier = Modifier.height(16.dp))
            RankingVideoCard(
                rank = selectedIndex + 1,
                item = selectedItem,
                onClick = { onVideoClick?.invoke(selectedItem) }
            )
        } else if (selectedIndex in values1.indices) {
            // 没有视频数据时显示基本信息
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = labels.getOrElse(selectedIndex) { "第${selectedIndex + 1}名" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "播放量: ${values1[selectedIndex].formatLargeNumber()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}