package com.cypress.cymediaplayer.composables

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.ui.PlayerView
import com.cypress.cymediaplayer.viewModels.VideoViewModel
import com.cypress.cymediaplayer.repositories.VideoItem
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun VideoPlayerScreen(videoItem: VideoItem , onBackPressed: (String) -> Unit) {

    val context = LocalContext.current
    val videoPlayerViewModel: VideoViewModel = koinViewModel()
    BackHandler(enabled = true) {
        videoPlayerViewModel.stop()
        onBackPressed("home")
    }
    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = videoPlayerViewModel.exoPlayer
                useController = true
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
    videoPlayerViewModel.play(videoItem.uri.toUri())

}