package ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ovo.sypw.wmx420.androidfinal.data.model.BilibiliRankingItem
import ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank.components.RankingVideoCard
import ovo.sypw.wmx420.androidfinal.ui.screens.components.ErrorView
import ovo.sypw.wmx420.androidfinal.ui.screens.components.LoadingIndicator

@Composable
fun BilibiliRankScreen(
    viewModel: BilibiliRankViewModel = koinInject(),
    onVideoClick: (BilibiliRankingItem) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val rankingList by viewModel.rankingList.collectAsState()

    Scaffold() {
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
        }

    }
}
