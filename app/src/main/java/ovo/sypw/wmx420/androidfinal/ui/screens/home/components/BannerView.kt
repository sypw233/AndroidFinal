package ovo.sypw.wmx420.androidfinal.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.delay
import ovo.sypw.wmx420.androidfinal.data.model.Banner

@Composable
fun BannerView(
    bannerList: List<Banner>,
    onClick: (Banner) -> Unit,
    modifier: Modifier = Modifier,
    autoScrollDelay: Long = 3000L
) {
    if (bannerList.isEmpty()) return

    // 1. 计算初始页码：为了能向左滑动，我们将起始页设在 Int.MAX_VALUE 的中间位置
    // 同时确保中间位置对应的实际上是 bannerList 的第 0 个元素
    val initialPage = remember(bannerList.size) {
        val center = Int.MAX_VALUE / 2
        center - (center % bannerList.size)
    }

    // 2. 设置 PagerState
    // 如果只有一张图，不需要无限轮播，count 设为 1
    val pageCount = if (bannerList.size > 1) Int.MAX_VALUE else 1
    val pagerState = rememberPagerState(
        initialPage = if (bannerList.size > 1) initialPage else 0,
        pageCount = { pageCount }
    )

    // 3. 处理自动轮播逻辑
    // 监听是否正在被用户拖拽
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    // 当非拖拽状态且列表大于1时，启动定时器
    LaunchedEffect(key1 = isDragged) {
        if (!isDragged && bannerList.size > 1) {
            while (true) {
                delay(autoScrollDelay)
                // 确保协程还在活跃状态且未被拖拽
                runCatching {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
            .clip(RoundedCornerShape(12.dp))
    ) {
        // --- 轮播主体 ---
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // 取余算法获取真实索引
            val actualIndex = page % bannerList.size
            val banner = bannerList[actualIndex]

            BannerItem(
                banner = banner,
                onClick = onClick
            )
        }

        // --- 底部指示器 & 标题 ---
        // 这里计算当前真实的 index 用于指示器高亮
        val currentActualIndex = pagerState.currentPage % bannerList.size

        BannerIndicatorAndTitle(
            banner = bannerList[currentActualIndex],
            totalCount = bannerList.size,
            currentIndex = currentActualIndex,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun BannerItem(
    banner: Banner,
    onClick: (Banner) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick(banner) }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(banner.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = banner.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun BannerIndicatorAndTitle(
    banner: Banner,
    totalCount: Int,
    currentIndex: Int,
    modifier: Modifier = Modifier
) {
    // 使用渐变背景让文字和指示器更清晰
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 左侧：标题
            Text(
                text = banner.title,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )

            // 右侧：指示器圆点
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(totalCount) { index ->
                    val isSelected = index == currentIndex
                    // 选中的时候宽一点，变成圆角矩形，未选中是小圆点
                    val width = if (isSelected) 12.dp else 6.dp
                    val color =
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(
                            alpha = 0.5f
                        )

                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(width)
                            .clip(RoundedCornerShape(3.dp))
                            .background(color)
                    )
                }
            }
        }
    }
}