package ovo.sypw.wmx420.androidfinal.data.repository

import android.app.Activity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow
import ovo.sypw.wmx420.androidfinal.data.remote.FirebaseWrapper
import ovo.sypw.wmx420.androidfinal.data.remote.PhoneVerificationResult

class UserRepository {

    val currentUser: FirebaseUser?
        get() = FirebaseWrapper.currentUser

    fun authStateFlow(): Flow<FirebaseUser?> = FirebaseWrapper.authStateFlow()

    suspend fun signInWithEmail(email: String, password: String): Result<AuthResult> =
        FirebaseWrapper.signInWithEmail(email, password)

    suspend fun signUpWithEmail(email: String, password: String): Result<AuthResult> =
        FirebaseWrapper.signUpWithEmail(email, password)


    suspend fun signUpWithGoogle(credential: AuthCredential): Result<AuthResult> =
        FirebaseWrapper.signInWithGoogle(credential)


    fun signOut() = FirebaseWrapper.signOut()

    suspend fun sendPasswordResetEmail(email: String): Result<Void> =
        FirebaseWrapper.sendPasswordResetEmail(email)

    // ==================== 手机号认证相关方法 ====================

    /**
     * 发起手机号验证
     */
    fun startPhoneVerification(
        phoneNumber: String,
        activity: Activity,
        callback: (PhoneVerificationResult) -> Unit
    ) = FirebaseWrapper.startPhoneNumberVerification(phoneNumber, activity, callback)

    /**
     * 重新发送验证码
     */
    fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        resendToken: PhoneAuthProvider.ForceResendingToken,
        callback: (PhoneVerificationResult) -> Unit
    ) = FirebaseWrapper.resendVerificationCode(phoneNumber, activity, resendToken, callback)

    /**
     * 使用验证码登录
     */
    suspend fun signInWithPhoneCode(verificationId: String, code: String): Result<AuthResult> =
        FirebaseWrapper.signInWithPhoneCode(verificationId, code)

    /**
     * 使用手机凭证登录（自动验证时）
     */
    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<AuthResult> =
        FirebaseWrapper.signInWithPhoneCredential(credential)
}
