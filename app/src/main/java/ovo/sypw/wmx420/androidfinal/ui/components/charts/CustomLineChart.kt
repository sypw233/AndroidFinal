package ovo.sypw.wmx420.androidfinal.ui.components.charts

import android.graphics.Paint
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ovo.sypw.wmx420.androidfinal.data.model.BilibiliRankingItem
import ovo.sypw.wmx420.androidfinal.data.model.ChartData
import ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank.components.RankingVideoCard
import ovo.sypw.wmx420.androidfinal.utils.formatLargeNumber
import kotlin.math.roundToInt

@Composable
fun CustomLineChart(
    data: ChartData.LineChartData,
    modifier: Modifier = Modifier,
    rankingList: List<BilibiliRankingItem> = emptyList(),
    onVideoClick: ((BilibiliRankingItem) -> Unit)? = null,
    onPointClick: ((Int, Float) -> Unit)? = null
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline

    var selectedIndex by remember { mutableIntStateOf(-1) }
    var animationProgress by remember { mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 800),
        label = "lineAnimation"
    )

    LaunchedEffect(data) {
        animationProgress = 1f
    }

    val values = data.values
    val labels = data.labels

    if (values.isEmpty()) return

    val maxValue = values.maxOrNull() ?: 0f
    val range = if (maxValue > 0) maxValue * 1.1f else 1f
    val avgValue = values.average().toFloat()

    Column(modifier = modifier) {
        // 图表标题
        Text(
            text = data.title,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 图例
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(3.dp)
                    .background(outlineColor.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "平均值: ${avgValue.formatLargeNumber()}",
                style = MaterialTheme.typography.labelMedium,
                color = outlineColor
            )
        }

        // 图表
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .pointerInput(values) {
                    detectTapGestures { offset ->
                        val chartWidth = size.width.toFloat()
                        val padding = 50f
                        val availableWidth = chartWidth - padding * 2
                        val pointSpacing =
                            if (values.size > 1) availableWidth / (values.size - 1) else availableWidth
                        val clickedIndex = ((offset.x - padding) / pointSpacing).roundToInt()
                            .coerceIn(0, values.size - 1)
                        selectedIndex = if (selectedIndex == clickedIndex) -1 else clickedIndex
                        onPointClick?.invoke(clickedIndex, values[clickedIndex])
                    }
                }
        ) {
            val chartWidth = size.width
            val chartHeight = size.height
            val padding = 50f
            val topPadding = 20f
            val bottomPadding = 35f
            val availableWidth = chartWidth - padding * 2
            val availableHeight = chartHeight - topPadding - bottomPadding
            val pointSpacing =
                if (values.size > 1) availableWidth / (values.size - 1) else availableWidth

            // Y轴刻度
            for (i in 0..4) {
                val y = topPadding + availableHeight * (1 - i / 4f)
                val value = range * i / 4f
                drawLine(
                    color = outlineColor.copy(alpha = 0.15f),
                    start = Offset(padding, y),
                    end = Offset(chartWidth - padding / 2, y),
                    strokeWidth = 1f
                )
                drawContext.canvas.nativeCanvas.drawText(
                    value.formatLargeNumber(),
                    padding - 8f, y + 5f,
                    Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 28f
                        textAlign = Paint.Align.RIGHT
                    }
                )
            }

            // 平均线
            val avgY = topPadding + availableHeight * (1 - avgValue / range)
            drawLine(
                color = outlineColor.copy(alpha = 0.6f),
                start = Offset(padding, avgY),
                end = Offset(chartWidth - padding / 2, avgY),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
            )

            // 计算点位置
            val points = values.mapIndexed { index, value ->
                val x = padding + index * pointSpacing
                val normalizedValue = (value / range).coerceIn(0f, 1f)
                val y = topPadding + availableHeight * (1 - normalizedValue * animatedProgress)
                Offset(x, y)
            }

            // 填充区域
            if (points.size > 1) {
                val fillPath = Path().apply {
                    moveTo(points[0].x, chartHeight - bottomPadding)
                    lineTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                    lineTo(points.last().x, chartHeight - bottomPadding)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.3f),
                            primaryColor.copy(alpha = 0.05f)
                        )
                    )
                )
            }

            // 折线
            if (points.size > 1) {
                val linePath = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }
                drawPath(linePath, primaryColor, style = Stroke(width = 4f))
            }

            // 数据点
            points.forEachIndexed { index, point ->
                val isSelected = index == selectedIndex
                drawCircle(
                    color = if (isSelected) primaryColor else primaryColor.copy(alpha = 0.8f),
                    radius = if (isSelected) 14f else 10f,
                    center = point
                )
                drawCircle(
                    color = surfaceColor,
                    radius = if (isSelected) 8f else 6f,
                    center = point
                )
                drawCircle(
                    color = primaryColor,
                    radius = if (isSelected) 5f else 3f,
                    center = point
                )
            }

            // X轴标签
            points.forEachIndexed { index, point ->
                if (index % 2 == 0 || points.size <= 6) {
                    drawContext.canvas.nativeCanvas.drawText(
                        labels.getOrElse(index) { "" },
                        point.x, chartHeight - 8f,
                        Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 26f
                            textAlign = Paint.Align.CENTER
                        }
                    )
                }
            }
        }

        // 选中点详情 - 显示视频卡片
        if (selectedIndex in values.indices && selectedIndex in rankingList.indices) {
            val selectedItem = rankingList[selectedIndex]
            Spacer(modifier = Modifier.height(16.dp))
            RankingVideoCard(
                rank = selectedIndex + 1,
                item = selectedItem,
                onClick = { onVideoClick?.invoke(selectedItem) }
            )
        } else if (selectedIndex in values.indices) {
            // 没有视频数据时显示基本信息
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = primaryColor.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(primaryColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${selectedIndex + 1}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = labels.getOrElse(selectedIndex) { "第${selectedIndex + 1}名" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "播放量: ${values[selectedIndex].formatLargeNumber()}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = primaryColor
                        )
                    }
                }
            }
        }
    }
}