package ovo.sypw.wmx420.androidfinal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ovo.sypw.wmx420.androidfinal.ui.screens.main.MainScreen
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
