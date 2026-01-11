package ovo.sypw.wmx420.androidfinal.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String? = null,
    val avatarUrl: String? = null,
) {
    companion object {
        fun getEmptyUser() = User(
            uid = "",
            email = "",
            displayName = null,
            avatarUrl = null,
        )
    }
}
