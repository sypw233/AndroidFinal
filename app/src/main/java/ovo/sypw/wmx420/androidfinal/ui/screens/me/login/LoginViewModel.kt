package ovo.sypw.androidendproject.ui.screens.login

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.wmx420.androidfinal.data.remote.PhoneVerificationResult
import ovo.sypw.wmx420.androidfinal.data.repository.UserRepository

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // 手机号登录相关状态
    private var currentPhoneNumber: String = ""
    private var currentVerificationId: String? = null
    private var currentResendToken: PhoneAuthProvider.ForceResendingToken? = null

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

    // ==================== 手机号登录相关方法 ====================

    /**
     * 发起手机号验证，发送短信验证码
     * @param phoneNumber 手机号（需包含国家代码，如 +8613800138000）
     * @param activity 当前 Activity
     */
    fun startPhoneVerification(phoneNumber: String, activity: Activity) {
        if (phoneNumber.isBlank()) {
            Log.w(TAG, "startPhoneVerification: 手机号为空")
            _uiState.value = LoginUiState.Error("请输入手机号")
            return
        }

        // 格式化手机号（如果没有国家代码，默认添加中国代码）
        val formattedNumber = if (phoneNumber.startsWith("+")) {
            phoneNumber
        } else {
            "+86$phoneNumber"
        }

        Log.d(TAG, "startPhoneVerification: 发起验证, phoneNumber=$formattedNumber")
        currentPhoneNumber = formattedNumber
        _uiState.value = LoginUiState.Loading

        userRepository.startPhoneVerification(formattedNumber, activity) { result ->
            handlePhoneVerificationResult(result)
        }
    }

    /**
     * 使用验证码登录
     * @param code 用户输入的验证码
     */
    fun verifyPhoneCode(code: String) {
        val verificationId = currentVerificationId
        if (verificationId == null) {
            Log.w(TAG, "verifyPhoneCode: verificationId 为空")
            _uiState.value = LoginUiState.Error("验证会话已过期，请重新发送验证码")
            return
        }

        if (code.isBlank() || code.length < 6) {
            Log.w(TAG, "verifyPhoneCode: 验证码格式不正确")
            _uiState.value = LoginUiState.Error("请输入 6 位验证码")
            return
        }

        Log.d(TAG, "verifyPhoneCode: 开始验证, code=$code")
        _uiState.value = LoginUiState.PhoneVerifying

        viewModelScope.launch {
            val result = userRepository.signInWithPhoneCode(verificationId, code)
            result.fold(
                onSuccess = { authResult ->
                    val user = authResult.user
                    Log.d(TAG, "verifyPhoneCode: 登录成功, user=${user?.uid}")
                    if (user != null) {
                        _uiState.value = LoginUiState.Success(user)
                    } else {
                        _uiState.value = LoginUiState.Error("登录失败：用户信息为空")
                    }
                },
                onFailure = { e ->
                    Log.e(TAG, "verifyPhoneCode: 验证失败", e)
                    _uiState.value = LoginUiState.Error(e.message ?: "验证码验证失败")
                }
            )
        }
    }

    /**
     * 重新发送验证码
     * @param activity 当前 Activity
     */
    fun resendPhoneCode(activity: Activity) {
        val resendToken = currentResendToken
        if (resendToken == null || currentPhoneNumber.isBlank()) {
            Log.w(TAG, "resendPhoneCode: 无法重发，缺少必要信息")
            _uiState.value = LoginUiState.Error("无法重发验证码，请重新开始")
            return
        }

        Log.d(TAG, "resendPhoneCode: 重发验证码到 $currentPhoneNumber")
        _uiState.value = LoginUiState.Loading

        userRepository.resendVerificationCode(currentPhoneNumber, activity, resendToken) { result ->
            handlePhoneVerificationResult(result)
        }
    }

    /**
     * 处理手机验证结果
     */
    private fun handlePhoneVerificationResult(result: PhoneVerificationResult) {
        when (result) {
            is PhoneVerificationResult.CodeSent -> {
                Log.d(TAG, "handlePhoneVerificationResult: 验证码已发送")
                currentVerificationId = result.verificationId
                currentResendToken = result.resendToken
                _uiState.value = LoginUiState.PhoneCodeSent(
                    verificationId = result.verificationId,
                    resendToken = result.resendToken
                )
            }

            is PhoneVerificationResult.AutoVerified -> {
                Log.d(TAG, "handlePhoneVerificationResult: 自动验证成功")
                viewModelScope.launch {
                    val signInResult = userRepository.signInWithPhoneCredential(result.credential)
                    signInResult.fold(
                        onSuccess = { authResult ->
                            val user = authResult.user
                            if (user != null) {
                                _uiState.value = LoginUiState.Success(user)
                            } else {
                                _uiState.value = LoginUiState.Error("登录失败：用户信息为空")
                            }
                        },
                        onFailure = { e ->
                            Log.e(TAG, "handlePhoneVerificationResult: 自动验证登录失败", e)
                            _uiState.value = LoginUiState.Error(e.message ?: "登录失败")
                        }
                    )
                }
            }

            is PhoneVerificationResult.Failed -> {
                Log.e(TAG, "handlePhoneVerificationResult: 验证失败", result.exception)
                _uiState.value = LoginUiState.Error(result.exception.message ?: "发送验证码失败")
            }
        }
    }

    /**
     * 返回手机号输入界面
     */
    fun backToPhoneInput() {
        currentVerificationId = null
        currentResendToken = null
        _uiState.value = LoginUiState.Idle
    }

    fun resetState() {
        Log.d(TAG, "resetState: 重置状态为 Idle")
        currentPhoneNumber = ""
        currentVerificationId = null
        currentResendToken = null
        _uiState.value = LoginUiState.Idle
    }
}

