package ovo.sypw.wmx420.androidfinal.ui.screens.video

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import ovo.sypw.wmx420.androidfinal.data.model.Video
import ovo.sypw.wmx420.androidfinal.data.model.VideoDetail

private val tabs = listOf("视频简介", "视频列表")

@OptIn(UnstableApi::class)
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(videoId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    val isFullscreen = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val video = remember(videoId) { Video.mock().find { it.id == videoId } ?: Video.mock().first() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var currentPlayingIndex by remember { mutableIntStateOf(0) }
    var hasStartedPlaying by remember { mutableStateOf(false) }
    var isManualMode by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItems(video.videoDetailList.map { MediaItem.fromUri(Uri.parse(it.videoUrl)) })
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            prepare()
        }
    }

    val playerView = remember {
        PlayerView(context).apply {
            player = exoPlayer
            useController = true
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.BLACK)
            setShutterBackgroundColor(android.graphics.Color.BLACK)
            setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
        }
    }

    // 系统UI控制
    LaunchedEffect(isFullscreen) { toggleSystemUI(activity, !isFullscreen) }

    // 播放列表切换
    LaunchedEffect(currentPlayingIndex) {
        if (currentPlayingIndex in 0 until exoPlayer.mediaItemCount && currentPlayingIndex != exoPlayer.currentMediaItemIndex) {
            exoPlayer.seekTo(currentPlayingIndex, 0)
            if (hasStartedPlaying) exoPlayer.play()
        }
    }

    // 全屏返回处理
    BackHandler(enabled = isFullscreen) {
        isManualMode = false
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    // 自动恢复屏幕方向
    LaunchedEffect(isManualMode, isFullscreen) {
        if (!isManualMode && !isFullscreen) {
            delay(500)
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // 全屏按钮监听
    LaunchedEffect(isFullscreen) {
        playerView.setFullscreenButtonClickListener {
            isManualMode = !isFullscreen
            activity?.requestedOrientation = if (isFullscreen)
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
    }

    // 资源释放
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            toggleSystemUI(activity, true)
        }
    }

    val startPlaying = { hasStartedPlaying = true; exoPlayer.play() }

    if (isFullscreen) {
        Box(Modifier
            .fillMaxSize()
            .background(Color.Black)) {
            VideoPlayerContent(hasStartedPlaying, playerView, video.coverUrl, startPlaying)
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(video.name) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                "返回"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(Modifier
                .fillMaxSize()
                .padding(padding)) {
                Box(Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)) {
                    VideoPlayerContent(hasStartedPlaying, playerView, video.coverUrl, startPlaying)
                }
                PrimaryTabRow(selectedTabIndex) {
                    tabs.forEachIndexed { i, title ->
                        Tab(
                            selectedTabIndex == i,
                            { selectedTabIndex = i },
                            text = { Text(title) })
                    }
                }
                when (selectedTabIndex) {
                    0 -> VideoIntroContent(video.intro)
                    1 -> VideoListContent(
                        video.videoDetailList,
                        currentPlayingIndex
                    ) { currentPlayingIndex = it }
                }
            }
        }
    }
}

@Composable
private fun VideoPlayerContent(
    isPlaying: Boolean,
    playerView: PlayerView,
    coverUrl: String,
    onPlay: () -> Unit
) {
    if (isPlaying) {
        AndroidView(factory = { playerView }, modifier = Modifier.fillMaxSize())
    } else {
        Box(
            Modifier
                .fillMaxSize()
                .clickable(onClick = onPlay),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                coverUrl,
                "视频封面",
                Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Icon(
                Icons.Default.PlayCircleOutline,
                "播放",
                Modifier.size(72.dp),
                Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

private fun toggleSystemUI(activity: Activity?, show: Boolean) {
    activity?.window?.let { window ->
        WindowCompat.setDecorFitsSystemWindows(window, show)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            if (show) show(WindowInsetsCompat.Type.systemBars())
            else {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}

@Composable
private fun VideoIntroContent(intro: String) {
    Column(Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("课程简介", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(
            intro,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun VideoListContent(
    videoList: List<VideoDetail>,
    currentIndex: Int,
    onVideoClick: (Int) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        itemsIndexed(videoList) { index, detail ->
            VideoListItem(detail, index == currentIndex) { onVideoClick(index) }
            if (index < videoList.lastIndex) HorizontalDivider()
        }
    }
}

@Composable
private fun VideoListItem(videoDetail: VideoDetail, isPlaying: Boolean, onClick: () -> Unit) {
    val containerColor =
        if (isPlaying) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val contentColor =
        if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val iconTint =
        if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PlayCircleOutline, null, tint = iconTint)
            Spacer(Modifier.width(12.dp))
            Text(
                videoDetail.videoName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}
