package com.cypress.cymediaplayer.viewModels

data class RemoteReceiverState (
    val isClientVerified: Boolean = false,
    val isClientConnected: Boolean = false,
    val isDataReceived: Boolean = false,
    val isServerStopped: Boolean = false,
    val receivedMessage: String = ""
)