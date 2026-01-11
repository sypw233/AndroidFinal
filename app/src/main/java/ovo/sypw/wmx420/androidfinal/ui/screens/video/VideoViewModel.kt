package ovo.sypw.wmx420.androidfinal.ui.screens.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.wmx420.androidfinal.data.model.Video
import ovo.sypw.wmx420.androidfinal.data.repository.VideoRepository

class VideoViewModel(
    private val videoRepository: VideoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<VideoUiState>(VideoUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private val _videoList = MutableStateFlow<List<Video>>(emptyList())
    val videoList = _videoList.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            videoRepository.getVideoList()
                .collect { result ->
                    result.onSuccess { videos ->
                        _uiState.value = VideoUiState.Success(videos)
                    }
                    result.onFailure { e ->
                        _uiState.value = VideoUiState.Error(e.message ?: "加载失败")
                    }
                }
        }
    }
}