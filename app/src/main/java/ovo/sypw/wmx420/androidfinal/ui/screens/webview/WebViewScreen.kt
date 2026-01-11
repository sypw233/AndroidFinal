package ovo.sypw.wmx420.androidfinal.ui.screens.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.kevinnzou.web.AccompanistWebViewClient
import com.kevinnzou.web.LoadingState
import com.kevinnzou.web.WebView
import com.kevinnzou.web.rememberWebViewNavigator
import com.kevinnzou.web.rememberWebViewState
import ovo.sypw.wmx420.androidfinal.utils.UrlUtils

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    title: String = "",
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val state = rememberWebViewState(url = url)
    val navigator = rememberWebViewNavigator()

    val displayTitle = title.ifEmpty { state.pageTitle ?: "加载中..." }

    var pageLoadCount by remember { mutableIntStateOf(0) }
    var showExternalAppDialog by remember { mutableStateOf(false) }
    var pendingExternalUrl by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = navigator.canGoBack) {
        navigator.navigateBack()
    }

    val webViewClient = remember {
        object : AccompanistWebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val requestUrl = request?.url?.toString() ?: return false
                // Log 建议保留关键信息即可，生产环境可移除
                Log.d("WebViewScreen", "Url: $requestUrl, Count: $pageLoadCount")

                if (!UrlUtils.isHttpUrl(requestUrl)) {
                    // 首次加载阻止跳转（防自动唤起），后续点击需确认
                    if (pageLoadCount <= 1) return true

                    pendingExternalUrl = requestUrl
                    showExternalAppDialog = true
                    return true
                }
                return false
            }

            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
            }
        }
    }

    if (showExternalAppDialog && pendingExternalUrl != null) {
        val externalUrl = pendingExternalUrl!!
        val appName = UrlUtils.getAppNameFromScheme(externalUrl)

        AlertDialog(
            onDismissRequest = {
                showExternalAppDialog = false
                pendingExternalUrl = null
            },
            title = { Text("打开外部应用") },
            text = {
                // 优化2：简化字符串处理
                Text("是否在 $appName 中打开链接？")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExternalAppDialog = false
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(externalUrl)).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("WebViewScreen", "无法打开: $externalUrl")
                        }
                        pendingExternalUrl = null
                    }
                ) { Text("打开") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showExternalAppDialog = false
                    pendingExternalUrl = null
                }) { Text("取消") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = displayTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, "关闭")
                    }
                },
                actions = {
                    NavIcon(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        desc = "后退",
                        enabled = navigator.canGoBack,
                        onClick = { navigator.navigateBack() }
                    )
                    NavIcon(
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        desc = "前进",
                        enabled = navigator.canGoForward,
                        onClick = { navigator.navigateForward() }
                    )
                    IconButton(onClick = { navigator.reload() }) {
                        Icon(Icons.Default.Refresh, "刷新")
                    }
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(state.lastLoadedUrl ?: url))
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.OpenInBrowser, "浏览器打开")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            WebView(
                state = state,
                navigator = navigator,
                client = webViewClient,
                modifier = Modifier.fillMaxSize(),
                onCreated = { webView ->
                    webView.settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        setSupportZoom(true)
                        mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                    }
                }
            )

            val loadingState = state.loadingState
            if (loadingState is LoadingState.Loading) {
                LinearProgressIndicator(
                    progress = { loadingState.progress },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun NavIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    desc: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, enabled = enabled) {
        Icon(
            imageVector = icon,
            contentDescription = desc,
            tint = if (enabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}