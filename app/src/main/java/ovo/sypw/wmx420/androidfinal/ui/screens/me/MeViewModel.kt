package ovo.sypw.wmx420.androidfinal.ui.screens.me

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.wmx420.androidfinal.data.repository.UserRepository
import ovo.sypw.wmx420.androidfinal.utils.toUser

class MeViewModel(
    val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MeUiState>(MeUiState.Loading)
    val uiState: StateFlow<MeUiState> = _uiState.asStateFlow()

    init {
        checkLoginStatus()
    }

    fun checkLoginStatus() {
        viewModelScope.launch {
            userRepository.authStateFlow().collect { firebaseUser ->
                if (firebaseUser != null) {
                    _uiState.value = MeUiState.LoggedIn(firebaseUser.toUser())
                } else {
                    _uiState.value = MeUiState.LoggedOut
                }
            }
        }
    }
    fun logOut(){
        viewModelScope.launch {
            userRepository.signOut()
            _uiState.value = MeUiState.LoggedOut
        }
    }
}