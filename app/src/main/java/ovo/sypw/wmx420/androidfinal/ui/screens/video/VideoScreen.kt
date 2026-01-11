package ovo.sypw.wmx420.androidfinal.ui.screens.video

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ovo.sypw.wmx420.androidfinal.data.model.Video
import ovo.sypw.wmx420.androidfinal.ui.screens.components.ErrorView
import ovo.sypw.wmx420.androidfinal.ui.screens.components.LoadingIndicator
import ovo.sypw.wmx420.androidfinal.ui.screens.video.components.VideoItem

@Composable
fun VideoScreen(
    viewModel: VideoViewModel = koinInject(),
    onVideoClick: (Video) -> Unit
) {
    VideoList(
        viewModel = viewModel,
        onVideoClick = onVideoClick
    )
}

@Composable
fun VideoList(
    viewModel: VideoViewModel,
    onVideoClick: (Video) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val videoList by viewModel.videoList.collectAsState()
    when (val state = uiState) {
        is VideoUiState.Loading -> LoadingIndicator()
        is VideoUiState.Error -> {
            ErrorView(
                message = state.message,
                onRetry = { viewModel.loadData() }
            )
        }

        is VideoUiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                items(state.videos, key = { "video_${it.id}" }) { video ->
                    VideoItem(
                        video = video,
                        onClick = { onVideoClick(video) }
                    )
                }
            }
        }
    }
}