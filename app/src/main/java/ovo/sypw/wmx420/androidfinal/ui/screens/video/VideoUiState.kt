package ovo.sypw.wmx420.androidfinal.ui.screens.video

import ovo.sypw.wmx420.androidfinal.data.model.Video


sealed interface VideoUiState {
    data object Loading : VideoUiState
    data class Success(val videos: List<Video>) : VideoUiState
    data class Error(val message: String) : VideoUiState
}