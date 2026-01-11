package ovo.sypw.androidendproject.ui.screens.login

import com.google.firebase.auth.FirebaseUser

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Success(val user: FirebaseUser) : LoginUiState
    data object VerificationRequired : LoginUiState  // 邮箱验证状态
    data object PasswordResetEmailSent : LoginUiState  // 密码重置邮件已发送
    data class Error(val message: String) : LoginUiState
}
