package com.cypress.cymediaplayer.database

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.cypress.cymediaplayer.common.Resources
import com.cypress.cymediaplayer.database.CyDatabase
import com.cypress.cymediaplayer.database.VideoEntity
import com.cypress.cymediaplayer.repositories.VideoItem
import com.cypress.cymediaplayer.repositories.VideoListRepository
import org.koin.compose.getKoin
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class LocalVideoMediator(
    private val cyDb: CyDatabase,
    val videoListRepository: VideoListRepository
): RemoteMediator<Int, VideoEntity>()  {

    var offset = 0L

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, VideoEntity>
    ): MediatorResult {

        return try {
            Log.e("pageOffset" , "$offset")
             when(loadType) {
                LoadType.REFRESH -> offset = 0
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if(lastItem == null) {
                        offset = 0
                    } else {
                        offset += state.config.pageSize
                    }
                }
            }

            val videoList = videoListRepository.getVideoList(
                pageOffset = offset,
                pageCount = state.config.pageSize
            )

            cyDb.withTransaction {
                if(loadType == LoadType.REFRESH) {
                    cyDb.videoListDao().clearAll()
                }
                val videoEntities = videoList.map { it.toVideoEntity() }
                cyDb.videoListDao().upsert(videoEntity = videoEntities)
            }

            MediatorResult.Success(
                endOfPaginationReached = videoList.isEmpty()
            )
        } catch(e: IOException) {
            MediatorResult.Error(e)
        }

    }

}