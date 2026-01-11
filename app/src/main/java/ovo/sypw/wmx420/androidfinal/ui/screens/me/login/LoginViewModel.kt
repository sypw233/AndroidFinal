package ovo.sypw.androidendproject.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.wmx420.androidfinal.data.repository.UserRepository

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Log.w(TAG, "signInWithEmail: 邮箱或密码为空")
            _uiState.value = LoginUiState.Error("邮箱或密码不能为空")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = userRepository.signInWithEmail(email, password)
            result.fold(
                onSuccess = { authResult ->
                    val user = authResult.user
                    Log.d(
                        TAG,
                        "signInWithEmail: onSuccess, user=${user?.uid}, isEmailVerified=${user?.isEmailVerified}"
                    )
                    if (user != null) {
                        // 检查邮箱是否已验证
                        if (user.isEmailVerified) {
                            _uiState.value = LoginUiState.Success(user)
                        } else {
                            userRepository.signOut()
                            _uiState.value = LoginUiState.Error("请先验证您的邮箱后再登录")
                        }
                    } else {
                        Log.w(TAG, "signInWithEmail: 用户信息为空")
                        _uiState.value = LoginUiState.Error("登录失败：用户信息为空")
                    }
                },
                onFailure = { e ->
                    Log.e(TAG, "signInWithEmail: onFailure", e)
                    _uiState.value = LoginUiState.Error(e.message ?: "登录失败")
                }
            )
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Log.w(TAG, "signUpWithEmail: 邮箱或密码为空")
            _uiState.value = LoginUiState.Error("邮箱或密码不能为空")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = userRepository.signUpWithEmail(email, password)
            result.fold(
                onSuccess = { _ ->
                    _uiState.value = LoginUiState.VerificationRequired
                },
                onFailure = { e ->
                    Log.e(TAG, "signUpWithEmail: onFailure", e)
                    _uiState.value = LoginUiState.Error(e.message ?: "注册失败")
                }
            )
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val idToken = account.idToken
                Log.d(TAG, "signInWithGoogle: idToken=${if (idToken != null) "存在" else "null"}")
                if (idToken != null) {
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    val result = userRepository.signUpWithGoogle(credential)
                    result.fold(
                        onSuccess = { authResult ->
                            val user = authResult.user
                            Log.d(TAG, "signInWithGoogle: onSuccess, user=${user?.uid}")
                            if (user != null) {
                                _uiState.value = LoginUiState.Success(user)
                            } else {
                                Log.w(TAG, "signInWithGoogle: 用户信息为空")
                                _uiState.value = LoginUiState.Error("Google 登录失败：用户信息为空")
                            }
                        },
                        onFailure = { e ->
                            Log.e(TAG, "signInWithGoogle: onFailure", e)
                            _uiState.value = LoginUiState.Error(e.message ?: "Google 登录失败")
                        }
                    )
                } else {
                    Log.w(TAG, "signInWithGoogle: idToken 为 null")
                    _uiState.value = LoginUiState.Error("Google 登录失败：无法获取 ID Token")
                }
            } catch (e: Exception) {
                Log.e(TAG, "signInWithGoogle: 异常", e)
                _uiState.value = LoginUiState.Error(e.message ?: "Google 登录处理出错")
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        Log.d(TAG, "sendPasswordResetEmail: 开始, email=$email")
        if (email.isBlank()) {
            Log.w(TAG, "sendPasswordResetEmail: 邮箱为空")
            _uiState.value = LoginUiState.Error("请输入邮箱地址")
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "sendPasswordResetEmail: 设置状态为 Loading")
            _uiState.value = LoginUiState.Loading
            val result = userRepository.sendPasswordResetEmail(email)
            result.fold(
                onSuccess = {
                    Log.d(TAG, "sendPasswordResetEmail: 发送成功")
                    _uiState.value = LoginUiState.PasswordResetEmailSent
                },
                onFailure = { e ->
                    Log.e(TAG, "sendPasswordResetEmail: 失败", e)
                    _uiState.value = LoginUiState.Error(e.message ?: "发送重置邮件失败")
                }
            )
        }
    }

    fun resetState() {
        Log.d(TAG, "resetState: 重置状态为 Idle")
        _uiState.value = LoginUiState.Idle
    }
}
