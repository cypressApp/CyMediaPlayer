package com.cypress.cymediaplayer.viewModels

data class RemoteControlState(
    var isConnected: Boolean = false,
    var isConnecting: Boolean = false,
    var isDisconnecting: Boolean = false,
    var onDataReceived: Boolean = false,
    var onDataSent: Boolean = false,
    var receivedMessage: String = ""
)