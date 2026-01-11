package ovo.sypw.wmx420.androidfinal.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay

enum class AdType {
    IMAGE, VIDEO
}

data class SplashAd(
    val type: AdType,
    val url: String,
    val duration: Int = 5,
)

@Composable
fun SplashScreen(
    navigateToMain: () -> Unit
) {
    val context = LocalContext.current
    var countDown by remember { mutableStateOf(5) }
    var adFinished by remember { mutableStateOf(false) }
    val splashAd = remember {
        val index = (0..10000).random()
        SplashAd(
            type = AdType.IMAGE,
            url = "https://picsum.photos/id/$index/200/200",
            duration = 5
        )
    }

    LaunchedEffect(Unit) {
        while (countDown > 0 && !adFinished) {
            delay(1000L)
            countDown--
        }
        if (!adFinished) {
            adFinished = true
            navigateToMain()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (splashAd.type) {
            AdType.IMAGE -> {
                AsyncImage(
                    model = splashAd.url,
                    contentDescription = "ad",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            AdType.VIDEO -> {}
        }
    }
}