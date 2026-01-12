package ovo.sypw.wmx420.androidfinal.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ovo.sypw.wmx420.androidfinal.data.model.ChartData

@Composable
fun CustomPieChart(
    data: ChartData.PieChartData,
    modifier: Modifier = Modifier,
    selectedIndex: Int = -1,
    onSliceClick: ((Int) -> Unit)? = null
) {
    val items = data.items
    if (items.isEmpty()) return

    var internalSelectedIndex by remember { mutableIntStateOf(selectedIndex) }

    // 同步外部选中状态
    LaunchedEffect(selectedIndex) {
        internalSelectedIndex = selectedIndex
    }

    val total = items.sumOf { it.value.toDouble() }.toFloat()

    Column(modifier = modifier) {
        // 图表标题
        Text(
            text = data.title,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 饼图 - 放大尺寸
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .pointerInput(items) {
                    detectTapGestures { offset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val radius = minOf(centerX, centerY) * 0.85f

                        // 计算点击位置相对于中心的角度
                        val dx = offset.x - centerX
                        val dy = offset.y - centerY
                        val distance = kotlin.math.sqrt(dx * dx + dy * dy)

                        // 检查是否在饼图范围内
                        if (distance <= radius && distance >= radius * 0.3f) {
                            var angle = kotlin.math.atan2(dy, dx) * 180f / kotlin.math.PI.toFloat()
                            angle = (angle + 360f) % 360f  // 转换为正角度
                            angle = (angle + 90f) % 360f   // 从顶部开始

                            // 找到点击的扇形
                            var currentAngle = 0f
                            for (i in items.indices) {
                                val sweepAngle = items[i].value / total * 360f
                                if (angle >= currentAngle && angle < currentAngle + sweepAngle) {
                                    internalSelectedIndex =
                                        if (internalSelectedIndex == i) -1 else i
                                    onSliceClick?.invoke(i)
                                    break
                                }
                                currentAngle += sweepAngle
                            }
                        } else if (distance < radius * 0.3f) {
                            // 点击中心取消选中
                            internalSelectedIndex = -1
                            onSliceClick?.invoke(-1)
                        }
                    }
                }
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val radius = minOf(centerX, centerY) * 0.85f
            val innerRadius = radius * 0.5f

            var startAngle = -90f  // 从顶部开始

            items.forEachIndexed { index, item ->
                val sweepAngle = item.value / total * 360f
                val isSelected = index == internalSelectedIndex
                val scale = if (isSelected) 1.08f else 1f
                val actualRadius = radius * scale

                // 绘制扇形
                drawArc(
                    color = Color(item.color).let {
                        if (isSelected) it else it.copy(alpha = 0.9f)
                    },
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(centerX - actualRadius, centerY - actualRadius),
                    size = Size(actualRadius * 2, actualRadius * 2)
                )

                // 绘制边框
                drawArc(
                    color = Color.White,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(centerX - actualRadius, centerY - actualRadius),
                    size = Size(actualRadius * 2, actualRadius * 2),
                    style = Stroke(width = 3f)
                )

                startAngle += sweepAngle
            }

            // 绘制中心圆（环形效果）
            drawCircle(
                color = Color.White,
                radius = innerRadius,
                center = Offset(centerX, centerY)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 图例 - 改为可滑动列表
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            itemsIndexed(items) { index, item ->
                val isSelected = index == internalSelectedIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) Color(item.color).copy(alpha = 0.1f)
                            else Color.Transparent
                        )
                        .clickable {
                            internalSelectedIndex = if (internalSelectedIndex == index) -1 else index
                            onSliceClick?.invoke(index)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(item.color), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "%.1f%%".format(item.value),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(item.color) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 选中详情 - 移到下方
        if (internalSelectedIndex in items.indices) {
            val selectedItem = items[internalSelectedIndex]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(selectedItem.color).copy(alpha = 0.15f)
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
                            .background(Color(selectedItem.color), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "%.0f%%".format(selectedItem.value),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = selectedItem.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "占比 ${String.format("%.1f", selectedItem.value)}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}