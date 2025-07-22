package com.cypress.cymediaplayer.viewModels

import android.app.Application
import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.cypress.cymediaplayer.database.VideoEntity
import com.cypress.cymediaplayer.repositories.VideoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VideoListViewModel(
//    val videoListRepository: VideoListRepository
    context: Context,
    pager: Pager<Int, VideoEntity>
) : ViewModel(){

    val videoPagingFlow: Flow<PagingData<VideoItem>> = pager
        .flow
        .map { pagingData ->
            pagingData.map { entity ->
//                val retriever = MediaMetadataRetriever()
//                retriever.setDataSource(context, entity.uri.toUri())
//                val frameBitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                VideoItem(
                    id = entity.id,
                    title = entity.title,
                    uri = entity.uri,
                    thumbnail = null //frameBitmap
                )
            }
        }
        .cachedIn(viewModelScope)

//    val videoPagingFlow = pager
//        .flow
////        .map { pagingData ->
//////            pagingData.map { it.toBeer() }
////        }
//        .cachedIn(viewModelScope)

//    private val _videoList = MutableStateFlow<List<VideoItem>>(emptyList())
//    val videoList : StateFlow<List<VideoItem>> get() = _videoList

//    fun getAllList(){
//        viewModelScope.launch{
//            val tempList = mutableListOf<VideoItem>()
//            _videoList.value = emptyList()
//            withContext(Dispatchers.IO){
//                videoListRepository.getVideoList().collect { videoItem ->
//                    when(videoItem){
//                        is Resources.Success -> videoItem.data?.let { tempList.add(it) }
//                        is Resources.Error -> TODO()
//                        is Resources.Loading -> TODO()
//                    }
//                    if (tempList.size % 5 == 0) {
//                        withContext(Dispatchers.Main) {
//                            _videoList.value = tempList.toList()
//                        }
//                    }
//                }
//
//            }
//            _videoList.value = tempList.toList()
//        }
//    }

}