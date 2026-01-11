package ovo.sypw.wmx420.androidfinal.ui.screens.me

import ovo.sypw.wmx420.androidfinal.data.model.User

interface MeUiState {
    data object Loading : MeUiState
    data class LoggedIn(val user: User) : MeUiState
    data object LoggedOut : MeUiState
}