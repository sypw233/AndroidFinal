package ovo.sypw.wmx420.androidfinal.ui.screens.splash

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ovo.sypw.wmx420.androidfinal.ui.components.LoadingIndicator
import ovo.sypw.wmx420.androidfinal.utils.PreferenceUtils

// SharedPreferences 键名
private const val KEY_CACHED_SPLASH_URL = "cached_splash_url"
private const val KEY_NEXT_SPLASH_URL = "next_splash_url"

enum class AdType {
    IMAGE, VIDEO
}

data class SplashAd(
    val type: AdType,
    val url: String,
    val duration: Int = 5,
)

/**
 * 生成新的随机图片URL
 */
private fun generateNewImageUrl(): String {
    val index = (0..10000).random()
    return "https://picsum.photos/480/960?random=$index"
}

@Composable
fun SplashScreen(
    navigateToMain: () -> Unit
) {
    val context = LocalContext.current
    var countdown by remember { mutableIntStateOf(5) }
    var adFinished by remember { mutableStateOf(false) }
    var isImageLoaded by remember { mutableStateOf(false) }

    val splashAd = remember {
        val cachedUrl = PreferenceUtils.getString(context, KEY_CACHED_SPLASH_URL)
        val nextUrl = PreferenceUtils.getString(context, KEY_NEXT_SPLASH_URL)
        val imageUrl = when {
            cachedUrl.isNotEmpty() -> cachedUrl
            nextUrl.isNotEmpty() -> nextUrl
            else -> generateNewImageUrl()
        }
        SplashAd(
            type = AdType.IMAGE,
            url = imageUrl,
            duration = 5
        )
    }

    // 预加载下一次启动需要的图片
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            // 生成下一次的URL并保存
            val nextUrl = generateNewImageUrl()
            PreferenceUtils.putString(context, KEY_NEXT_SPLASH_URL, nextUrl)

            // 预加载下一次的图片到缓存
            try {
                val imageLoader = ImageLoader.Builder(context).build()
                val request = ImageRequest.Builder(context)
                    .data(nextUrl)
                    .build()
                imageLoader.execute(request)

                PreferenceUtils.putString(context, KEY_CACHED_SPLASH_URL, nextUrl)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 倒计时逻辑
    LaunchedEffect(Unit) {
        while (countdown > 0 && !adFinished) {
            delay(1000L)
            countdown--
        }
        if (!adFinished) {
            adFinished = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        if (!isImageLoaded) {
            LoadingIndicator()
        }

        when (splashAd.type) {
            AdType.IMAGE -> {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(splashAd.url)
                        .crossfade(300)
                        .build(),
                    contentDescription = "开屏广告",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onState = { state ->
                        if (state is AsyncImagePainter.State.Success) {
                            isImageLoaded = true
                        }
                    }
                )
            }

            AdType.VIDEO -> {
                SplashVideoPlayer(
                    videoUrl = splashAd.url,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // 跳过按钮
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(48.dp)
                .clickable {
                    adFinished = true
                    navigateToMain()
                },
            shape = RoundedCornerShape(64.dp),
            color = Color.Black.copy(alpha = 0.6f)
        ) {
            Text(
                text = if (countdown > 0) "跳过 $countdown" else "跳过",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 80.sp,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
            )
        }
    }
}

@Composable
fun SplashVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl.toUri()))
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
        )
    }
}
