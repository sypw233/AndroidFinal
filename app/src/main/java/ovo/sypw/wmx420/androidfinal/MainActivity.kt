package ovo.sypw.wmx420.androidfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ovo.sypw.wmx420.androidfinal.ui.navigation.AppNavigation
import ovo.sypw.wmx420.androidfinal.ui.theme.AndroidFinalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidFinalTheme {
                AppNavigation()
            }
        }
    }
}
