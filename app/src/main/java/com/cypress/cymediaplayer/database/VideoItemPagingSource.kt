package com.cypress.cymediaplayer.database

import android.graphics.Bitmap
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.*
import androidx.paging.PagingState
import com.cypress.cymediaplayer.repositories.VideoItem

class VideoItemPagingSource(
    private val originalSource: PagingSource<Int, VideoEntity>,
    private val mapper: suspend (VideoEntity) -> VideoItem
) : PagingSource<Int, VideoItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoItem> {
        return when (val result = originalSource.load(params)) {
            is Page -> {
                val mappedData = result.data.map { mapper(it) }
                Page(
                    data = mappedData,
                    prevKey = result.prevKey,
                    nextKey = result.nextKey
                )
            }

            is Error -> Error(result.throwable)
            is Invalid<*, *> -> TODO()
        }
    }

    override fun getRefreshKey(state: PagingState<Int, VideoItem>): Int? {
        // You can delegate to the original source's refresh key logic
        // But PagingSource doesn't expose it, so implement your own if needed
        return null
    }
}

