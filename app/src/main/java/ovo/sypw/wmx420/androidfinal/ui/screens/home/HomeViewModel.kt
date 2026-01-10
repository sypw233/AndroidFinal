package ovo.sypw.wmx420.androidfinal.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ovo.sypw.wmx420.androidfinal.data.model.Banner
import ovo.sypw.wmx420.androidfinal.data.model.News
import ovo.sypw.wmx420.androidfinal.data.repository.NewsRepository

class HomeViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList: StateFlow<List<News>> = _newsList.asStateFlow()

    private val _bannerList = MutableStateFlow<List<Banner>>(emptyList())
    val bannerList: StateFlow<List<Banner>> = _bannerList.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _refreshEvent = MutableSharedFlow<RefreshResult>()
    val refreshEvent: SharedFlow<RefreshResult> = _refreshEvent.asSharedFlow()

    private var currentPage = 1
    private var isLoadingMore = false

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            currentPage = 1
            _uiState.value = HomeUiState.Loading
            newsRepository.getNewsList(currentPage)
                .collect { result ->
                    result.onSuccess { data ->
                        _bannerList.value = data.banners
                        _newsList.value = data.news
                        _uiState.value = HomeUiState.Success(
                            bannerList = data.banners,
                            newsList = data.news,
                            hasMore = data.hasMore
                        )
                    }
                    result.onFailure {
                        _uiState.value = HomeUiState.Error(it.message ?: "Unknown error")
                    }
                }
        }
    }

    fun refresh() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            currentPage = 1

            try {
                newsRepository.refreshNews()
                    .catch { e ->
                        _isRefreshing.value = false
                        _refreshEvent.emit(RefreshResult.Error("刷新失败: ${e.message}"))
                    }
                    .collect { result ->
                        _isRefreshing.value = false
                        result.fold(
                            onSuccess = { refreshData ->
                                val data = refreshData.newsListData
                                val newCount = refreshData.newItemsCount

                                _bannerList.value = data.banners
                                _newsList.value = data.news
                                _uiState.value = HomeUiState.Success(
                                    bannerList = _bannerList.value,
                                    newsList = _newsList.value,
                                    hasMore = data.hasMore
                                )

                                when {
                                    newCount > 0 -> _refreshEvent.emit(RefreshResult.Success("发现 $newCount 条新内容"))
                                    data.news.isNotEmpty() -> _refreshEvent.emit(
                                        RefreshResult.Empty(
                                            "暂无新内容"
                                        )
                                    )

                                    else -> _refreshEvent.emit(RefreshResult.Empty("暂无内容"))
                                }
                            },
                            onFailure = { e ->
                                _refreshEvent.emit(RefreshResult.Error("刷新失败"))
                            }
                        )
                    }
            } catch (e: Exception) {
                _isRefreshing.value = false
                _refreshEvent.emit(RefreshResult.Error("刷新失败: ${e.message}"))
            }
        }
    }

    fun loadMore() {
        if (isLoadingMore) return
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success && !currentState.hasMore) return

        isLoadingMore = true

        viewModelScope.launch {
            currentPage++
            newsRepository.getNewsList(currentPage)
                .catch { isLoadingMore = false }
                .collect { result ->
                    result.onSuccess { data ->
                        val currentNews = _newsList.value.toMutableList()
                        currentNews.addAll(data.news)
                        _newsList.value = currentNews
                        _uiState.value = HomeUiState.Success(
                            bannerList = _bannerList.value,
                            newsList = currentNews,
                            hasMore = data.hasMore
                        )
                    }
                    isLoadingMore = false
                }
        }
    }
}

sealed interface RefreshResult {
    data class Success(val message: String) : RefreshResult
    data class Empty(val message: String) : RefreshResult
    data class Error(val message: String) : RefreshResult
}
