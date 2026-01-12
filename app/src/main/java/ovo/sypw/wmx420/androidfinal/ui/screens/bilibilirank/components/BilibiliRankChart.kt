package ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ovo.sypw.wmx420.androidfinal.data.model.BilibiliRankingItem
import ovo.sypw.wmx420.androidfinal.data.model.ChartData
import ovo.sypw.wmx420.androidfinal.ui.components.charts.CustomBarChart
import ovo.sypw.wmx420.androidfinal.ui.components.charts.CustomLineChart
import ovo.sypw.wmx420.androidfinal.ui.components.charts.CustomPieChart
import ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank.ChartType

@Composable
fun BilibiliRankChart(
    chartType: ChartType,
    chartData: ChartData,
    rankingList: List<BilibiliRankingItem>,
    onVideoClick: (BilibiliRankingItem) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item{
            when (chartType) {
                ChartType.LINE -> CustomLineChart(
                    data = chartData as ChartData.LineChartData,
                    rankingList = rankingList.take(10),
                    onVideoClick = onVideoClick
                )
                ChartType.PIE -> CustomPieChart(chartData as ChartData.PieChartData)
                ChartType.BAR -> CustomBarChart(
                    data = chartData as ChartData.BarChartData,
                    rankingList = rankingList.take(10),
                    onVideoClick = onVideoClick
                )
            }
        }
    }
}

