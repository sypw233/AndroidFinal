package ovo.sypw.wmx420.androidfinal.ui.screens.intro

import androidx.compose.animation.AnimatedVisibility
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

@Composable
fun IntroScreen(
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })

    val introPages = listOf(
        IntroPage(
            imageRes = R.drawable.ic_launcher_foreground,
            title = "Welcome to Our App",
            content = "111"
        ),
        IntroPage(
            imageRes = R.drawable.ic_launcher_foreground,
            title = "Welcome to Our App",
            content = "222"
        ),
        IntroPage(
            imageRes = R.drawable.ic_launcher_foreground,
            title = "Welcome to Our App",
            content = "333"
        ),
    )
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
                .padding(bottom = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) { index ->
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
            visible = pagerState.currentPage == 2,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
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
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.content,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}