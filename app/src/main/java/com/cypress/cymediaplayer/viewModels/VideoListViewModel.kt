package com.cypress.cymediaplayer.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.cypress.cymediaplayer.database.VideoEntity
import com.cypress.cymediaplayer.database.toVideoItem
import com.cypress.cymediaplayer.repositories.VideoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VideoListViewModel(
    pager: Pager<Int, VideoEntity>
) : ViewModel(){

    val videoPagingFlow: Flow<PagingData<VideoItem>> = pager
        .flow
        .map { pagingData ->
            pagingData.map { entity ->
                entity.toVideoItem()
            }
        }
        .cachedIn(viewModelScope)

}