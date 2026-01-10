package ovo.sypw.wmx420.androidfinal.ui.navigation

import kotlinx.serialization.Serializable

// 定义所有可能的路由
sealed class Screen {
    @Serializable
    data object Home : Screen()
    @Serializable
    data object Video : Screen()
    @Serializable
    data object Me : Screen()
    @Serializable
    data class WebView(val url: String) : Screen()
}