package ovo.sypw.wmx420.androidfinal.ui.components

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun DoubleBackExit(
    context: Context
){
    // 双击返回退出逻辑
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < 2000) {
            // 两次返回间隔小于2秒，退出应用
            (context as? Activity)?.finish()
        } else {
            // 第一次返回，显示提示
            lastBackPressTime = currentTime
            Toast.makeText(context, "再按一次退出应用", Toast.LENGTH_SHORT).show()
        }
    }
}