package com.cypress.cymediaplayer.composables.videoComposables

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.ImageLoader
import coil.compose.AsyncImage
import com.cypress.cymediaplayer.composables.PullToRefreshLazyColumn
import com.cypress.cymediaplayer.repositories.VideoItem
import com.cypress.cymediaplayer.viewModels.VideoListViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun VideoListComposable(onNavigation : (VideoItem) -> Unit) {

    var isRefreshing by remember { mutableStateOf(false) }
    val videoListViewModel : VideoListViewModel = koinViewModel()
    val imageLoader : ImageLoader = getKoin().get()
    val videoList by videoListViewModel.videoList.collectAsState()

//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent(),
//        onResult = { uri : Uri? ->
//            videoPlayerViewModel.play(uri)
//        }
//    )

//    DisposableEffect(Unit) {
//        onDispose {
//            viewModel.release()
//        }
//    }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(videoList) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if(lastVisibleIndex == videoList.lastIndex) {
                    videoListViewModel.loadVideos((videoList.lastIndex + 1).toLong() , 20)
                }
            }
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            videoListViewModel.clearList()
            delay(100)
            videoListViewModel.loadVideos(0, 20)
            isRefreshing = false
        }
    }

    if(videoListViewModel.isTv()){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black) // base background
        ) {

            Canvas(modifier = Modifier.fillMaxSize()) {
                // Example: diagonal gradient effect
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF1E1E1E), Color(0xFF333333), Color(0xFF000000)),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )
                )

                // Example: circles for a subtle pattern
                for (i in 1..5) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.05f),
                        radius = size.minDimension / (i * 2),
                        center = Offset(size.width / 2, size.height / 2)
                    )
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { isRefreshing = true }) {
                Text("Refresh")
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .focusGroup(),
                contentAlignment = Alignment.Center
            ) {
                LazyRow(
                    state = lazyListState,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(videoList.size) { index ->
                        val videoItem = videoList[index]
                        var isFocused by remember { mutableStateOf(false) }
                        val interactionSource = remember { MutableInteractionSource() }
                        Column(
                            modifier = Modifier.focusable()
                                .onFocusChanged { focusState ->
                                    isFocused = focusState.isFocused
                                }
                                .padding(8.dp)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) { onNavigation(videoItem) },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = videoItem.uri.toUri(),
                                imageLoader = imageLoader,
                                contentDescription = null,
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            if(isFocused){
                                Spacer(modifier = Modifier.size(4.dp))
                                Text(
                                    text = videoItem.title,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }

                        }
                    }
                }
            }
        }

    }else{
        Column(modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),) {

            Text("Items: ${videoList.size}")

            PullToRefreshLazyColumn(videoList, isRefreshing, {
                isRefreshing = true
            }, {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(videoList.size) { index ->
                        val videoItem = videoList[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    onNavigation(videoItem)
                                },
                            verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = videoItem.uri.toUri(),
                                imageLoader = imageLoader,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = videoItem.title,
                                modifier = Modifier.weight(1f),
                                fontSize = 12.sp
                            )
                        }


                    }
                }

            })

        }
    }



//    Column(modifier = Modifier
//        .safeContentPadding()
//        .fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally) {
//
////        Button(onClick = {
////            launcher.launch("video/*")
////        }) {
////            Text("Open Video")
////        }
//
//
//
//    }


//    val context = LocalContext.current
//    LaunchedEffect(key1 = videos.loadState) {
//        if(videos.loadState.refresh is LoadState.Error) {
//            Toast.makeText(
//                context,
//                "Error: " + (videos.loadState.refresh as LoadState.Error).error.message,
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        if(videos.loadState.refresh is LoadState.Loading) {
//            CircularProgressIndicator(
//                modifier = Modifier.align(Alignment.Center)
//            )
//        } else {
//            LazyColumn(
//                modifier = Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
////                items(videos){
////
////                }
////                item {
////                    if(videos.loadState.append is LoadState.Loading) {
////                        CircularProgressIndicator()
////                    }
////                }
//            }
//        }
//    }


    CheckVideoPermission()

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
