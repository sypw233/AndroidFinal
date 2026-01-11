package ovo.sypw.wmx420.androidfinal.ui.screens.settings.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ovo.sypw.wmx420.androidfinal.ui.screens.settings.SettingsClickItem
import ovo.sypw.wmx420.androidfinal.ui.screens.settings.SettingsSectionHeader
import ovo.sypw.wmx420.androidfinal.utils.PreferenceUtils

@Composable
fun BilibiliSettings(
    context: Context
) {
    SettingsSectionHeader(title = "B站设置")
    // B站 Cookies 状态
    var bilibiliCookies by remember {
        mutableStateOf(PreferenceUtils.getBilibiliCookies(context))
    }
    var showCookiesDialog by remember { mutableStateOf(false) }
    var tempCookies by remember { mutableStateOf("") }

    // B站 Cookies 对话框
    if (showCookiesDialog) {
        AlertDialog(
            onDismissRequest = { showCookiesDialog = false },
            title = { Text("设置 B站 Cookies") },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempCookies,
                        onValueChange = { tempCookies = it },
                        label = { Text("Cookies") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 5,
                        placeholder = { Text("") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    bilibiliCookies = tempCookies
                    PreferenceUtils.setBilibiliCookies(context, tempCookies)
                    showCookiesDialog = false
                }) {
                    Text("保存")
                }
            },
            dismissButton = {
                Row {
                    if (bilibiliCookies.isNotBlank()) {
                        TextButton(onClick = {
                            tempCookies = ""
                            bilibiliCookies = ""
                            PreferenceUtils.setBilibiliCookies(context, "")
                            showCookiesDialog = false
                        }) {
                            Text("清除", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(onClick = { showCookiesDialog = false }) {
                        Text("取消")
                    }
                }
            }
        )
    }

    SettingsClickItem(
        icon = Icons.Default.Cookie,
        title = "B站 Cookies",
        subtitle = if (bilibiliCookies.isBlank()) "未设置" else "已设置",
        onClick = {
            tempCookies = bilibiliCookies
            showCookiesDialog = true
        }
    )

    Spacer(modifier = Modifier.height(24.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(16.dp))
}