package ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ovo.sypw.wmx420.androidfinal.data.model.BilibiliRankingItem
import ovo.sypw.wmx420.androidfinal.data.model.ChartData
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
                        _uiState.value =
                            BilibiliRankUiState.Error(exception.message ?: "Unknown error")
                    }
                }
        }
    }

    fun getChartData(
        currentChart: ChartType,
        rankingList: List<BilibiliRankingItem>
    ): ChartData {
        return when (currentChart) {
            ChartType.LINE -> {
                // 折线图：展示TOP10视频播放量趋势
                val top10 = rankingList.take(10)
                ChartData.LineChartData(
                    title = "TOP 10 视频播放量",
                    labels = top10.mapIndexed { index, _ -> "第${index + 1}名" },
                    values = top10.map { it.stat.view.toFloat() }
                )
            }

            ChartType.BAR -> {
                // 柱状图：展示TOP10视频播放量对比
                val top10 = rankingList.take(10)
                ChartData.BarChartData(
                    title = "TOP 10 视频播放量对比",
                    labels = top10.mapIndexed { index, _ -> "Top${index + 1}" },
                    values = top10.map { it.stat.view.toFloat() }
                )
            }

            ChartType.PIE -> {
                // 饼图：展示分区分布
                val categoryColors = listOf(
                    0xFF2196F3.toInt(), // 蓝色
                    0xFF4CAF50.toInt(), // 绿色
                    0xFFE040FB.toInt(), // 品红
                    0xFFFF9800.toInt(), // 橙色
                    0xFF9C27B0.toInt(), // 紫色
                    0xFF00BCD4.toInt(), // 青色
                    0xFFF44336.toInt(), // 红色
                    0xFF795548.toInt(), // 棕色
                    0xFF607D8B.toInt(), // 蓝灰
                    0xFF8BC34A.toInt()  // 浅绿
                )
                val categoryCount = rankingList
                    .groupBy { it.tname }
                    .mapValues { it.value.size }
                    .toList()
                    .sortedByDescending { it.second }
                ChartData.PieChartData(
                    title = "热门视频分区分布",
                    items = categoryCount.mapIndexed { index, (category, count) ->
                        ChartData.PieChartItem(
                            label = category,
                            value = count.toFloat(),
                            color = categoryColors[index % categoryColors.size]
                        )
                    }
                )
            }
        }
    }
}

