package com.cypress.cymediaplayer

import android.Manifest
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.cypress.cymediaplayer.database.VideoEntity
import com.cypress.cymediaplayer.repositories.VideoItem
import com.cypress.cymediaplayer.ui.theme.CyMediaPlayerTheme
import com.cypress.cymediaplayer.viewModels.VideoListViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckVideoPermission()
            CyMediaPlayerTheme {
                val videoListViewModel : VideoListViewModel = koinViewModel()
                val videos : LazyPagingItems<VideoEntity> = videoListViewModel.videoPagingFlow.collectAsLazyPagingItems()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current
                    LaunchedEffect(key1 = videos.loadState) {
                        if(videos.loadState.refresh is LoadState.Error) {
                            Toast.makeText(
                                context,
                                "Error: " + (videos.loadState.refresh as LoadState.Error).error.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        if (videos.loadState.refresh is LoadState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
//                                val retriever = MediaMetadataRetriever()
                                items(videos.itemCount) { index ->
                                    val item = videos[index]
                                    if (item != null) {
                                        Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable{
//                                            onNavigation(videoItem)
                                        } ,
                                        verticalAlignment = Alignment.CenterVertically) {
                                        val uri = item.uri.toUri()
                                        context.contentResolver.openFileDescriptor(uri, "r")?.use {
                                            val retriever = MediaMetadataRetriever()
                                            retriever.setDataSource(it.fileDescriptor)
                                            val bitmap = retriever.getFrameAtTime(0)
                                            retriever.release()
                                            // use bitmap
                                            if(bitmap != null){
                                                Image(
                                                    bitmap = bitmap.asImageBitmap(),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(52.dp)
                                                        .clip(RoundedCornerShape(16.dp)), // Rounded corners
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        }
//                                        retriever.setDataSource(this@MainActivity, item.uri.toUri())
//                                        val frameBitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
//
                                        Spacer(modifier = Modifier.size(4.dp))
                                        Text(
                                            text = item.title,
                                            modifier = Modifier.weight(1f),
                                            fontSize = 12.sp
                                        )
                                    }
                                    }
                                }
//                                retriever.release()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, KoinExperimentalAPI::class)
@Composable
fun CheckVideoPermission() {

    val videoListViewModel : VideoListViewModel = koinViewModel()

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_VIDEO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission = permission)

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        } else {
//            videoListViewModel.getAllList()
        }
    }

    if (permissionState.status.isGranted) {
//        videoListViewModel.getAllList()
    } else {
        Text("Permission is required to view videos")
    }
}