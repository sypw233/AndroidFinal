package ovo.sypw.wmx420.androidfinal.ui.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.ui.graphics.vector.ImageVector
import ovo.sypw.wmx420.androidfinal.ui.navigation.Screen

enum class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
) {
    Home(
        screen = Screen.Home,
        label = "首页",
        icon = Icons.Default.Home
    ),
    BilibiliRank(
        screen = Screen.BilibiliRank,
        label = "排行榜",
        icon = Icons.Default.StackedBarChart
    ),

    Video(
        screen = Screen.Video,
        label = "视频",
        icon = Icons.Filled.PlayCircle
    ),
    Me(
        screen = Screen.Me,
        label = "我的",
        icon = Icons.Default.Approval
    ),
}