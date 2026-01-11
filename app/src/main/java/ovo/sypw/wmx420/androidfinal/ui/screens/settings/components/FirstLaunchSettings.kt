package ovo.sypw.wmx420.androidfinal.ui.screens.settings.components

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ovo.sypw.wmx420.androidfinal.ui.screens.settings.SettingsSectionHeader
import ovo.sypw.wmx420.androidfinal.ui.screens.settings.SettingsToggleItem
import ovo.sypw.wmx420.androidfinal.utils.PreferenceUtils

@Composable
fun FirstLaunchSettings(
    context: Context
) {
    SettingsSectionHeader(title = "首次启动设置")
    var firstLaunch by remember { mutableStateOf(PreferenceUtils.getFirstLaunch(context)) }

    SettingsToggleItem(
        icon = Icons.Default.SettingsBackupRestore,
        title = "首次启动",
        subtitle = "下次启动时启用Splash",
        checked = firstLaunch,
        onCheckedChange = {
            firstLaunch = it
            PreferenceUtils.setFirstLaunch(context, it)
        }
    )
}