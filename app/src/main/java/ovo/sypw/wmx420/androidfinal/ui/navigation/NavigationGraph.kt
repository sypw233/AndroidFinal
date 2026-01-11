package ovo.sypw.wmx420.androidfinal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ovo.sypw.wmx420.androidfinal.ui.screens.intro.IntroScreen
import ovo.sypw.wmx420.androidfinal.ui.screens.main.MainScreen
import ovo.sypw.wmx420.androidfinal.ui.screens.me.login.LoginScreen
import ovo.sypw.wmx420.androidfinal.ui.screens.splash.SplashScreen
import ovo.sypw.wmx420.androidfinal.ui.screens.webview.WebViewScreen
import ovo.sypw.wmx420.androidfinal.utils.PreferenceUtils

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val adEnable = PreferenceUtils.isAdEnabled(context)
    val firstLaunch = PreferenceUtils.isFirstLaunch(context)
    val googleAdEnable = PreferenceUtils.useGoogleAd(context)
    val startDestination = if (firstLaunch) {
        Screen.Intro
    } else if (adEnable) {
        if (googleAdEnable) Screen.Main else Screen.Splash
    } else {
        Screen.Main
    }

    NavHost(
        navController = navController,
        startDestination = startDestination, // 设置起始页
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
        composable<Screen.Login> {
            LoginScreen(
                onLoginSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable<Screen.Setting> { }
        composable<Screen.Intro> {
            IntroScreen(
                onFinish = { navController.navigate(Screen.Main) }
            )
        }
        composable<Screen.Splash> {
            SplashScreen(
                navigateToMain = { navController.navigate(Screen.Main) }
            )
        }

    }
}