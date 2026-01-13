package ovo.sypw.androidendproject.ui.screens.login

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Success(val user: FirebaseUser) : LoginUiState
    data object VerificationRequired : LoginUiState  // 邮箱验证状态
    data object PasswordResetEmailSent : LoginUiState  // 密码重置邮件已发送
    data class Error(val message: String) : LoginUiState

    // 手机号登录状态
    /** 验证码已发送，等待用户输入 */
    data class PhoneCodeSent(
        val verificationId: String,
        val resendToken: PhoneAuthProvider.ForceResendingToken
    ) : LoginUiState

    /** 正在验证手机验证码 */
    data object PhoneVerifying : LoginUiState
}
