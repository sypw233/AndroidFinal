package ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DataExploration
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ovo.sypw.wmx420.androidfinal.data.model.BilibiliRankingItem
import ovo.sypw.wmx420.androidfinal.ui.components.ErrorView
import ovo.sypw.wmx420.androidfinal.ui.components.LoadingIndicator
import ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank.components.BilibiliRankChart
import ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank.components.RankingVideoCard

enum class ChartType(val title: String, val icon: ImageVector) {
    LINE("折线图", Icons.AutoMirrored.Filled.ShowChart),
    PIE("饼图", Icons.Default.PieChart),
    BAR("柱状图", Icons.Default.BarChart),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BilibiliRankScreen(
    viewModel: BilibiliRankViewModel = koinInject(),
    onVideoClick: (BilibiliRankingItem) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val rankingList by viewModel.rankingList.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (isExpanded) 45f else 0f, label = "rotation")
    var currentChart: ChartType? by remember { mutableStateOf(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "B站全站排行榜",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 展开的三个 ExtendedFloatingActionButton
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ChartType.entries.reversed().forEach { chartType ->
                            ExtendedFloatingActionButton(
                                modifier = Modifier.width(120.dp),
                                onClick = {
                                    isExpanded = false
                                    currentChart = chartType
                                },
                                icon = { Icon(chartType.icon, contentDescription = null) },
                                text = { Text(chartType.title) },
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { isExpanded = !isExpanded },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.DataExploration,
                        contentDescription = if (isExpanded) "关闭" else "图表分析",
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is BilibiliRankUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.loadData(forceRefresh = true) },
                    )
                }

                is BilibiliRankUiState.Loading -> {
                    LoadingIndicator()
                }

                is BilibiliRankUiState.Success -> {
                    val chart = currentChart
                    if (chart is ChartType) {
                        BilibiliRankChart(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            chartType = chart,
                            chartData = viewModel.getChartData(
                                currentChart = chart,
                                rankingList = rankingList
                            ),
                            rankingList = rankingList,
                            onVideoClick = onVideoClick,
                            onBack = {
                                currentChart = null
                            }
                        )
                    } else {
                        RankList(
                            rankingList = rankingList,
                            onVideoClick = onVideoClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RankList(
    rankingList: List<BilibiliRankingItem>,
    onVideoClick: (BilibiliRankingItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(rankingList.take(100)) { index, item ->
            RankingVideoCard(
                rank = index + 1,
                item = item,
                onClick = { onVideoClick(item) }
            )
        }
    }
}
