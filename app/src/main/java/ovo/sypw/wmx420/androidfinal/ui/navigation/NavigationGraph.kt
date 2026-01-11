package ovo.sypw.wmx420.androidfinal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ovo.sypw.wmx420.androidfinal.ui.screens.home.HomeScreen
import ovo.sypw.wmx420.androidfinal.ui.screens.main.MainScreen
import ovo.sypw.wmx420.androidfinal.ui.screens.me.MeScreen
import ovo.sypw.wmx420.androidfinal.ui.screens.video.VideoScreen
import ovo.sypw.wmx420.androidfinal.ui.screens.webview.WebViewScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main, // 设置起始页
        modifier = modifier
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
                on何意味Click = { 何意味 ->
                    when (何意味) {
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
        composable<Screen.Video> {
            VideoScreen()
        }
        composable<Screen.Me> {
            MeScreen()
        }
        composable<Screen.WebView> {
            WebViewScreen(
                url = it.arguments?.getString("url") ?: "",
                title = it.arguments?.getString("title") ?: "",
                onBack = { navController.popBackStack() }
            )
        }
        composable<Screen.Main> {
            MainScreen(
                navController = navController
            )
        }
    }
}
