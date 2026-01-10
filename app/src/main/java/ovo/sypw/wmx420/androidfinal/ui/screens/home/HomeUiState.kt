package ovo.sypw.wmx420.androidfinal.ui.screens.home

import ovo.sypw.wmx420.androidfinal.data.model.Banner
import ovo.sypw.wmx420.androidfinal.data.model.News

interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val bannerList: List<Banner>,
        val newsList: List<News>,
        val hasMore: Boolean,
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState

}