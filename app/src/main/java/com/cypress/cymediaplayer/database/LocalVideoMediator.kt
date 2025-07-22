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
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class LocalVideoMediator(
    val context: Context,
    private val cyDb: CyDatabase
): RemoteMediator<Int, VideoEntity>()  {

    var offset = 0L

    fun getVideoList(pageOffset: Long , pageCount : Int) : List<VideoItem> {

        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE
        )

        val tempList: MutableList<VideoItem> = mutableListOf()
//        val retriever = MediaMetadataRetriever()

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)

            var index = 0
            while (cursor.moveToNext()) {
                if (index < pageOffset) {
                    index++
                    continue
                }
                if (tempList.size >= pageCount) break

                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val uri = ContentUris.withAppendedId(collection, id)

//                val tempItem: VideoItem = try {
//                    retriever.setDataSource(context, uri)
//                    val frameBitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
//                    VideoItem(id, title, uri.toString(), frameBitmap)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    VideoItem(id, title, uri.toString(), null)
//                }
                val tempItem = VideoItem(id, title, uri.toString(), null)
                tempList.add(tempItem)
                index++
            }
        }

//        retriever.release()
        return tempList
    }

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
//                        (lastItem.id / state.config.pageSize) + 1
                    }
                }
            }

            val videoList = getVideoList(
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