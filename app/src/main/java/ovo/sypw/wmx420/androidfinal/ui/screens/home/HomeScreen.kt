package ovo.sypw.wmx420.androidfinal.ui.screens.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ovo.sypw.wmx420.androidfinal.data.model.Banner
import ovo.sypw.wmx420.androidfinal.data.model.News
import ovo.sypw.wmx420.androidfinal.ui.screens.components.ErrorView
import ovo.sypw.wmx420.androidfinal.ui.screens.components.LoadingIndicator
import ovo.sypw.wmx420.androidfinal.ui.screens.home.components.BannerView
import ovo.sypw.wmx420.androidfinal.ui.screens.home.components.CategoryButtonRow
import ovo.sypw.wmx420.androidfinal.ui.screens.home.components.NewsItem

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinInject(),
    onBannerClick: (Banner) -> Unit,
    onNewsClick: (News) -> Unit,
    on何意味Click: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val bannerList by viewModel.bannerList.collectAsState()
    val newsList by viewModel.newsList.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val showBackToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 2
        }
    }
    // 监听刷新事件
    LaunchedEffect(Unit) {
        viewModel.refreshEvent.collect { result ->
            val message = when (result) {
                is RefreshResult.Success -> result.message
                is RefreshResult.Empty -> result.message
                is RefreshResult.Error -> result.message
            }
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            when (uiState) {
                is HomeUiState.Loading -> LoadingIndicator(message = "数据加载中...")

                is HomeUiState.Success -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (bannerList.isNotEmpty()) {
                            item(key = "banner") {
                                BannerView(
                                    bannerList = bannerList,
                                    onClick = onBannerClick
                                )
                            }
                        }
                        item(key = "何意味button") {
                            CategoryButtonRow(
                                onCategoryClick = on何意味Click,
                            )
                        }
                        items(newsList, key = { it.id }) { news ->
                            Log.d("HomeScreen", "NewsList: $news")
                            NewsItem(
                                news = news,
                                onClick = { onNewsClick(news) },
                            )
                        }
                        if ((uiState as HomeUiState.Success).hasMore) {
                            item(key = "load_more") {
                                LaunchedEffect(Unit) {
                                    viewModel.loadMore()
                                }
                                LoadingIndicator(modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                }

                is HomeUiState.Error -> {
                    ErrorView(
                        message = (uiState as HomeUiState.Error).message,
                        onRetry = { viewModel.loadData() }
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp) // 避开可能的 BottomBar
        ) {
            AnimatedVisibility(
                visible = showBackToTop,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "回到顶部")
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
