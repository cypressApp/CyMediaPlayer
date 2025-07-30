package com.cypress.cymediaplayer.viewModels

data class ListManagerState (
    var isDeleting: Boolean = false,
    var isLoading: Boolean = false,
    var isSuccess: Boolean = false,
    var error: String = ""
)