package ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank

import ovo.sypw.wmx420.androidfinal.data.model.BilibiliRankingItem

sealed interface BilibiliRankUiState {
    data object Loading : BilibiliRankUiState
    data class Success(
        val rankList: List<BilibiliRankingItem> = emptyList()
    ) : BilibiliRankUiState

    data class Error(val message: String) : BilibiliRankUiState
}
