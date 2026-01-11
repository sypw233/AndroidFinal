package ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ovo.sypw.wmx420.androidfinal.data.model.BilibiliRankingItem
import ovo.sypw.wmx420.androidfinal.data.repository.BilibiliRepository

class BilibiliRankViewModel(
    private val bilibiliRepository: BilibiliRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<BilibiliRankUiState>(BilibiliRankUiState.Loading)
    val uiState: StateFlow<BilibiliRankUiState> = _uiState

    private val _rankingList = MutableStateFlow<List<BilibiliRankingItem>>(emptyList())
    val rankingList: StateFlow<List<BilibiliRankingItem>> = _rankingList

    private var isDataLoaded = false

    init {
        loadDataIfNeeded()
    }

    private fun loadDataIfNeeded() {
        if (!isDataLoaded) {
            loadData()
        }
    }

    fun loadData(forceRefresh: Boolean = false) {
        if (isDataLoaded && !forceRefresh) return
        
        viewModelScope.launch {
            _uiState.value = BilibiliRankUiState.Loading
            bilibiliRepository.getBilibiliRankingData()
                .collect { result ->
                    result.onSuccess { rankList ->
                        _rankingList.value = rankList
                        _uiState.value = BilibiliRankUiState.Success(rankList)
                        isDataLoaded = true
                    }
                    result.onFailure { exception ->
                        _uiState.value = BilibiliRankUiState.Error(exception.message ?: "Unknown error")
                    }
                }
        }
    }
}

