package ovo.sypw.wmx420.androidfinal.ui.screens.intro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ovo.sypw.wmx420.androidfinal.R
import ovo.sypw.wmx420.androidfinal.utils.PreferenceUtils

data class IntroPage(
    val imageRes: Int,
    val title: String,
    val content: String
)

private val introPages = listOf(
    IntroPage(
        imageRes = R.drawable.outline_rss_feed_24,
        title = "Page 1",
        content = "聚合IT之家RSS订阅最新资讯"
    ),
    IntroPage(
        imageRes = R.drawable.outline_stacked_bar_chart_24,
        title = "Page 2",
        content = "聚合B站排行榜，通过图表展现"
    ),
    IntroPage(
        imageRes = R.drawable.outline_play_circle_24,
        title = "Page 3",
        content = "内置播放器"
    ),
    IntroPage(
        imageRes = R.drawable.outline_map_24,
        title = "Page 4",
        content = "集成高德地图核心服务"
    ),
    IntroPage(
        imageRes = R.drawable.outline_person_pin_24,
        title = "Page 5",
        content = "支持邮箱与Google一键登录"
    ),
)

@Composable
fun IntroScreen(
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { introPages.size })
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            IntroPageContent(introPages[page])
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(pagerState.pageCount) { index ->
                val color = if (pagerState.currentPage == index) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
        AnimatedVisibility(
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight }
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight }
            ) + fadeOut(),
            visible = pagerState.currentPage == pagerState.pageCount - 1,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Button(
                onClick = {
                    PreferenceUtils.setFirstLaunch(context, false)
                    onFinish()
                }
            ) {
                Text("进入")
            }
        }
    }
}

@Composable
fun IntroPageContent(page: IntroPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = page.imageRes,
            contentDescription = page.title,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.content,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}