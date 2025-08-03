package com.cypress.cymediaplayer.viewModels

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cypress.cymediaplayer.common.Resources
import com.cypress.cymediaplayer.repositories.VideoItem
import com.cypress.cymediaplayer.repositories.VideoListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class VideoListViewModel(
    val videoListRepository: VideoListRepository
) : ViewModel(){

    private val _videoList = mutableStateOf<List<VideoItem>>(emptyList())
    val videoList: State<List<VideoItem>> = _videoList

    private val _listManager = mutableStateOf(ListManagerState())
    val listManager : State<ListManagerState> = _listManager

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

    fun deleteItem(uri : Uri){
        videoListRepository.deleteItem(uri).onEach { result ->
            when(result){
                is Resources.Deleting ->{
                    _listManager.value = ListManagerState(isDeleting = true)
                }
                is Resources.Error -> {
                    _listManager.value = ListManagerState(error = result.message.toString())
                }
                is Resources.Loading -> {

                }
                is Resources.Success -> {
                    _listManager.value = ListManagerState(isSuccess = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun isTv() : Boolean{
        return videoListRepository.isTv()
    }

}