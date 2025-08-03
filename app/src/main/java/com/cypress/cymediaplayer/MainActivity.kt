package com.cypress.cymediaplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.cypress.cymediaplayer.repositories.TcpClientRepository
import com.cypress.cymediaplayer.repositories.VideoItem
import com.cypress.cymediaplayer.viewModels.VideoListViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


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
