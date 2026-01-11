package ovo.sypw.wmx420.androidfinal.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import ovo.sypw.wmx420.androidfinal.data.remote.FirebaseWrapper

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


}




