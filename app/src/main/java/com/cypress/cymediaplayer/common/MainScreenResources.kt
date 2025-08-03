package com.cypress.cymediaplayer.common

import com.cypress.cymediaplayer.repositories.VideoItem

sealed class MainScreenResources {

    object VideoList : MainScreenResources()
    data class VideoPlayer(val videoItem: VideoItem) : MainScreenResources()
    object RemoteControl : MainScreenResources()
    object QrCode : MainScreenResources()

}