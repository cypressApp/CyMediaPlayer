package com.cypress.cymediaplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.cypress.cymediaplayer.common.MainScreenResources
import com.cypress.cymediaplayer.composables.remoteControlComposables.QrCodeComposable
import com.cypress.cymediaplayer.composables.remoteControlComposables.QrCodeScannerComposable
import com.cypress.cymediaplayer.composables.remoteControlComposables.RemoteControlComposable
import com.cypress.cymediaplayer.composables.videoComposables.VideoListComposable
import com.cypress.cymediaplayer.composables.videoComposables.VideoPlayerScreen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf<MainScreenResources>(MainScreenResources.VideoList) }

            when (val screen = currentScreen) {
                is MainScreenResources.VideoList -> VideoListComposable(onNavigation = { videoItem ->
                    currentScreen = MainScreenResources.VideoPlayer(videoItem)
                }, onNavRemoteControl = {
                    currentScreen = MainScreenResources.RemoteControl
                })
                is MainScreenResources.VideoPlayer -> {
                    VideoPlayerScreen(videoItem = screen.videoItem , {
                        currentScreen = MainScreenResources.VideoList
                    })
                }
                is MainScreenResources.RemoteControl -> {
                    RemoteControlComposable(onNavigation = {
                        currentScreen = MainScreenResources.QrCode
                    } , onBackPressed = {
                        currentScreen = MainScreenResources.VideoList
                    })
                }
                is MainScreenResources.QrCode -> {
                    QrCodeScannerComposable(onNavigation = {
                        currentScreen = MainScreenResources.RemoteControl
                    } , onBackPressed = {
                        currentScreen = MainScreenResources.RemoteControl
                    })
//                    QrCodeComposable(onNavigation = {
//                        currentScreen = MainScreenResources.VideoList
//                    } , onBackPressed = {
//                        currentScreen = MainScreenResources.VideoList
//                    })
                }
            }

        }
    }
}
