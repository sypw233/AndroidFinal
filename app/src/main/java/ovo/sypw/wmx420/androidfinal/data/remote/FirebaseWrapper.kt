package ovo.sypw.wmx420.androidfinal.data.remote

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

/**
 * 手机验证结果回调
 */
sealed interface PhoneVerificationResult {
    /** 验证码已发送 */
    data class CodeSent(
        val verificationId: String,
        val resendToken: PhoneAuthProvider.ForceResendingToken
    ) : PhoneVerificationResult

    /** 自动验证成功（无需用户输入验证码） */
    data class AutoVerified(val credential: PhoneAuthCredential) : PhoneVerificationResult

    /** 验证失败 */
    data class Failed(val exception: Exception) : PhoneVerificationResult
}

object FirebaseWrapper {

    private const val TAG = "FirebaseWrapper"
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
    }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<AuthResult> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Log.d(
                TAG,
                "signInWithEmail: login success, user:${result.user}, isVerified:${result.user?.isEmailVerified}"
            )
            Result.success(result)
        } catch (e: Exception) {
            Log.d(TAG, "signInWithEmail: login fail $e")
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(credential: AuthCredential): Result<AuthResult> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            Log.d(TAG, "signInWithGoogle: login success, user:${result.user}")
            Result.success(result)
        } catch (e: Exception) {
            Log.d(TAG, "signInWithGoogle: login fail $e")
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<AuthResult> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Log.d(TAG, "signUpWithEmail: user created,user:${result.user?.uid}")
            result.user?.let { firebaseUser ->
                firebaseUser.sendEmailVerification().await()
                Log.d(TAG, "signUpWithEmail: verification email sent")
                signOut()
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Void> {
        return try {
            val result = auth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "sendPasswordResetEmail: email sent")
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    // ==================== 手机号认证相关方法 ====================

    /**
     * 发起手机号验证，发送短信验证码
     * @param phoneNumber 手机号（需包含国家代码，如 +8613800138000）
     * @param activity 当前 Activity，用于 reCAPTCHA 验证
     * @param callback 验证结果回调
     */
    fun startPhoneNumberVerification(
        phoneNumber: String,
        activity: Activity,
        callback: (PhoneVerificationResult) -> Unit
    ) {
        Log.d(TAG, "startPhoneNumberVerification: 开始验证手机号 $phoneNumber")

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // 自动验证成功（某些设备支持自动读取短信验证码）
                Log.d(TAG, "onVerificationCompleted: 自动验证成功")
                callback(PhoneVerificationResult.AutoVerified(credential))
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e(TAG, "onVerificationFailed: 验证失败", e)
                callback(PhoneVerificationResult.Failed(e))
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent: 验证码已发送, verificationId=$verificationId")
                callback(PhoneVerificationResult.CodeSent(verificationId, token))
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /**
     * 重新发送验证码
     * @param phoneNumber 手机号
     * @param activity 当前 Activity
     * @param resendToken 上次发送时获得的重发令牌
     * @param callback 验证结果回调
     */
    fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        resendToken: PhoneAuthProvider.ForceResendingToken,
        callback: (PhoneVerificationResult) -> Unit
    ) {
        Log.d(TAG, "resendVerificationCode: 重新发送验证码到 $phoneNumber")

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "resendVerificationCode onVerificationCompleted: 自动验证成功")
                callback(PhoneVerificationResult.AutoVerified(credential))
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e(TAG, "resendVerificationCode onVerificationFailed: 验证失败", e)
                callback(PhoneVerificationResult.Failed(e))
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "resendVerificationCode onCodeSent: 验证码已重新发送")
                callback(PhoneVerificationResult.CodeSent(verificationId, token))
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /**
     * 使用验证码验证并登录
     * @param verificationId 验证 ID（从 onCodeSent 获得）
     * @param code 用户输入的验证码
     * @return 登录结果
     */
    suspend fun signInWithPhoneCode(verificationId: String, code: String): Result<AuthResult> {
        return try {
            Log.d(TAG, "signInWithPhoneCode: 开始验证, code=$code")
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val result = auth.signInWithCredential(credential).await()
            Log.d(TAG, "signInWithPhoneCode: 登录成功, user=${result.user?.uid}")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "signInWithPhoneCode: 登录失败", e)
            Result.failure(e)
        }
    }

    /**
     * 使用 PhoneAuthCredential 登录（用于自动验证的情况）
     * @param credential 手机认证凭证
     * @return 登录结果
     */
    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<AuthResult> {
        return try {
            Log.d(TAG, "signInWithPhoneCredential: 开始登录")
            val result = auth.signInWithCredential(credential).await()
            Log.d(TAG, "signInWithPhoneCredential: 登录成功, user=${result.user?.uid}")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "signInWithPhoneCredential: 登录失败", e)
            Result.failure(e)
        }
    }
}