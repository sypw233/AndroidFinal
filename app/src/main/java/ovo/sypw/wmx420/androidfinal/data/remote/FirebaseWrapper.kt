package ovo.sypw.wmx420.androidfinal.data.remote

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

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


}