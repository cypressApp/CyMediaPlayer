package com.cypress.cymediaplayer.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cypress.cymediaplayer.repositories.VideoItem
import com.cypress.cymediaplayer.repositories.VideoListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class VideoListViewModel(
    val videoListRepository: VideoListRepository
) : ViewModel(){

    private val _videoList = MutableStateFlow<List<VideoItem>>(emptyList())
    val videoList: StateFlow<List<VideoItem>> = _videoList

    fun loadVideos(pageOffset: Long, pageCount: Int) {
        viewModelScope.launch {
            videoListRepository.getVideoList(pageOffset, pageCount)
                .catch { e ->
                    _videoList.value = emptyList()
                }
                .collect { newVideos ->
                    val currentList = _videoList.value
                    _videoList.value = currentList + newVideos
                }
        }
    }

    fun clearList(){
        _videoList.value = emptyList()
    }

}