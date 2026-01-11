package ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ovo.sypw.wmx420.androidfinal.data.model.BilibiliRankingItem
import ovo.sypw.wmx420.androidfinal.utils.formatCount

@Composable
fun RankingVideoCard(
    rank: Int,
    item: BilibiliRankingItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 排名徽章
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        brush = when (rank) {
                            1 -> Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500)))
                            2 -> Brush.linearGradient(listOf(Color(0xFFC0C0C0), Color(0xFF9E9E9E)))
                            3 -> Brush.linearGradient(listOf(Color(0xFFCD7F32), Color(0xFF8B5A2B)))
                            else -> Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (rank <= 3) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 封面图
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(16f / 10f)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = item.pic.replace("http://", "https://"),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // 分区标签
                item.tname.let { tname ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                RoundedCornerShape(topEnd = 6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tname,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 视频信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.owner.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 数据统计
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatItem(
                        icon = Icons.Default.PlayArrow,
                        value = item.stat.view.formatCount()
                    )
                    StatItem(
                        icon = Icons.Default.ThumbUp,
                        value = item.stat.like.formatCount()
                    )
                }
            }

            // 打开图标
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "打开",
                modifier = Modifier
                    .size(20.dp)
                    .padding(start = 4.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
