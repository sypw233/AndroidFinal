package ovo.sypw.wmx420.androidfinal.ui.screens.settings.components

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ovo.sypw.wmx420.androidfinal.ui.screens.settings.SettingsSectionHeader
import ovo.sypw.wmx420.androidfinal.ui.screens.settings.SettingsToggleItem
import ovo.sypw.wmx420.androidfinal.utils.PreferenceUtils

@Composable
fun AdsSettings(
    context: Context
) {
    var adEnable by remember {
        mutableStateOf(PreferenceUtils.isAdEnabled(context))
    }
    var googleAdEnable by remember {
        mutableStateOf(PreferenceUtils.enableGoogleAd(context))
    }
    SettingsSectionHeader("广告设置")

    SettingsToggleItem(
        icon = Icons.Default.AdsClick,
        title = "开屏广告",
        subtitle = if (adEnable) "已启用" else "已禁用",
        checked = adEnable,
        onCheckedChange = {
            adEnable = it
            PreferenceUtils.setAdEnabled(context, it)
        }
    )
    if (adEnable) {
        SettingsToggleItem(
            icon = Icons.Default.AdsClick,
            title = "Google广告",
            subtitle = if (googleAdEnable) "已启用" else "已禁用",
            checked = googleAdEnable,
            onCheckedChange = {
                googleAdEnable = it
                PreferenceUtils.setUseGoogleAd(context, it)
            }
        )
    }
}