package com.cypress.cymediaplayer.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.cypress.cymediaplayer.database.VideoEntity

class VideoListViewModel(
//    val videoListRepository: VideoListRepository
    pager: Pager<Int, VideoEntity>
) : ViewModel(){

    val videoPagingFlow = pager
        .flow
//        .map { pagingData ->
////            pagingData.map { it.toBeer() }
//        }
        .cachedIn(viewModelScope)

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