package ovo.sypw.wmx420.androidfinal.ui.screens.me

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import ovo.sypw.wmx420.androidfinal.data.model.User
import ovo.sypw.wmx420.androidfinal.ui.components.LoadingIndicator

@Composable
fun MeScreen(
    viewModel: MeViewModel = koinInject(),
    onLoginClick: () -> Unit,
    onMapClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (uiState) {
            is MeUiState.Loading -> LoadingIndicator()
            is MeUiState.LoggedIn -> {
                ProfileCard(
                    user = (uiState as MeUiState.LoggedIn).user,
                    onLoginClick = { },
                )
            }

            is MeUiState.LoggedOut -> {
                ProfileCard(
                    user = null,
                    onLoginClick = onLoginClick,
                )
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        MeManuItem(
            icon = Icons.Default.Map,
            title = "地图",
            onClick = onMapClick
        )
        MeManuItem(
            icon = Icons.Default.Settings,
            title = "设置",
            onClick = onSettingsClick
        )

        Spacer(modifier = Modifier.weight(1f))

        if (uiState is MeUiState.LoggedIn) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                LoggingOutCard(viewModel::logout)
            }
        }

    }
}

@Composable
fun LoggingOutCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "退出登录",
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("退出登录")
        }
    }
}

@Composable
fun ProfileCard(
    user: User?,
    onLoginClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { if (user == null) onLoginClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = CenterVertically
        ) {
            // 只有当用户存在且有头像 URL 时才加载图片，否则显示默认图标
            if (user?.avatarUrl != null) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = "用户头像",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f) // 关键：让文本区域占据剩余空间，把按钮挤到右边
            ) {
                // 动态计算标题和副标题
                val displayName = if (user != null) {
                    user.displayName?.takeIf { it.isNotBlank() }
                        ?: user.email.substringBefore("@")
                } else {
                    "未登录"
                }

                val subText = user?.email ?: "登录后也没有更多内容"

                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }
}


@Composable
fun MeManuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = title,
            )

        }
    }

}