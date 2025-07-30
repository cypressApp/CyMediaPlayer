package com.cypress.cymediaplayer.viewModels.amplify

data class AuthState(
    var isLoading: Boolean = false,
    var isSuccess: Boolean = false,
    var errorMessage: String = ""
)
