package com.cypress.cymediaplayer.composables

import android.Manifest
import android.os.Build
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.cypress.cymediaplayer.repositories.VideoItem
import com.cypress.cymediaplayer.viewModels.VideoListViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun VideoListScreen(onNavigation : (VideoItem) -> Unit) {



    var isRefreshing by remember { mutableStateOf(false) }
//    val videoListViewModel : VideoListViewModel = koinViewModel()
//    val videoList by videoListViewModel.videoList.collectAsState()
//    val videos : LazyPagingItems<VideoEntity> = videoListViewModel.videoPagingFlow.collectAsLazyPagingItems()
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

//    LaunchedEffect(isRefreshing) {
//        if (isRefreshing) {
//            videoListViewModel.getAllList()
//            isRefreshing = false
//        }
//    }
//
//
//    Column(modifier = Modifier
//        .safeContentPadding()
//        .fillMaxSize(),) {
//
//        Text("Items: ${videoList.size}")
//
//        PullToRefreshLazyColumn(videoList , isRefreshing , {
//            isRefreshing = true
//        } , {
//            LazyColumn(modifier = Modifier.fillMaxSize()) {
//                items(videoList){ videoItem ->
//
//                    Row(modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp)
//                        .clickable{
//                            onNavigation(videoItem)
//                        } ,
//                        verticalAlignment = Alignment.CenterVertically) {
//                        videoItem.thumbnail?.let {
//                            Image(
//                                bitmap = it.asImageBitmap(),
//                                contentDescription = null,
//                                modifier = Modifier.size(52.dp)
//                                    .clip(RoundedCornerShape(16.dp)), // Rounded corners
//                                contentScale = ContentScale.Crop
//                            )
//                        }
//                        Spacer(modifier = Modifier.size(4.dp))
//                        Text(
//                            text = videoItem.title,
//                            modifier = Modifier.weight(1f),
//                            fontSize = 12.sp
//                        )
//                    }
//                }
//            }
//        })
//
//    }

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
