package ovo.sypw.wmx420.androidfinal.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ovo.sypw.wmx420.androidfinal.ui.components.DoubleBackExit
import ovo.sypw.wmx420.androidfinal.ui.navigation.Screen
import ovo.sypw.wmx420.androidfinal.ui.screens.bilibilirank.BilibiliRankScreen
import ovo.sypw.wmx420.androidfinal.ui.screens.home.HomeScreen
import ovo.sypw.wmx420.androidfinal.ui.screens.me.MeScreen
import ovo.sypw.wmx420.androidfinal.ui.screens.video.VideoScreen

@Composable
fun MainScreen(navController: NavHostController) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current

    DoubleBackExit(context)

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavItem.entries.forEach { item ->
                    // 使用 endsWith 精确匹配路由后缀，避免 contains 导致多个项被选中
                    val screenName = item.screen::class.simpleName ?: ""
                    val isSelected = currentDestination?.route?.endsWith(screenName) == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = isSelected,
                        onClick = {
                            bottomNavController.navigate(item.screen) {
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Screen.Home> {
                HomeScreen(
                    onBannerClick = { banner ->
                        navController.navigate(
                            Screen.WebView(
                                banner.linkUrl,
                                banner.title
                            )
                        )
                    },
                    onNewsClick = { news ->
                        navController.navigate(
                            Screen.WebView(
                                news.url,
                                news.title
                            )
                        )
                    },
                    onCenterButtonClick = { route ->
                        when (route) {
                            "android" -> navController.navigate(
                                Screen.WebView(
                                    url = "https://www.runoob.com/w3cnote/android-tutorial-end.html",
                                    title = "Android"
                                )
                            )

                            "jsp" -> navController.navigate(
                                Screen.WebView(
                                    url = "https://www.runoob.com/jsp/jsp-tutorial.html",
                                    title = "JSP"
                                )
                            )

                            "jquery" -> navController.navigate(
                                Screen.WebView(
                                    url = "https://www.runoob.com/jquery/jquery-tutorial.html",
                                    title = "jQuery"
                                )
                            )

                            "servlet" -> navController.navigate(
                                Screen.WebView(
                                    url = "https://www.runoob.com/servlet/servlet-tutorial.html",
                                    title = "Servlet"
                                )
                            )
                        }
                    }
                )
            }
            composable<Screen.BilibiliRank> {
                BilibiliRankScreen(
                    onVideoClick = { item ->
                        navController.navigate(
                            Screen.WebView(
                                url = "https://m.bilibili.com/video/${item.bvid}",
                                title = item.title
                            )
                        )
                    }
                )
            }
            composable<Screen.Video> {
                VideoScreen(
                    onVideoClick = { video ->
                        navController.navigate(Screen.VideoDetail(video.id))
                    }
                )
            }
            composable<Screen.Me> {
                MeScreen(
                    onLoginClick = {
                        navController.navigate(Screen.Login)
                    },
                    onMapClick = {
                        navController.navigate(Screen.Map)
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Setting)
                    }
                )
            }
        }
    }
}
