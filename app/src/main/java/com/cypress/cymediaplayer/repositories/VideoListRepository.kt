package com.cypress.cymediaplayer.repositories

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import com.cypress.cymediaplayer.common.Resources
import com.cypress.cymediaplayer.database.VideoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VideoItem(
    actual val id: Long,
    actual val title: String,
    actual val uri: String,
    val thumbnail: Bitmap?
)
{
    fun toVideoEntity() : VideoEntity {

        return VideoEntity(
            title = title,
            uri = uri
        )

    }
}

interface VideoListRepository{
    fun getVideoList() : Flow<Resources<VideoItem>>
}

class VideoListRepositoryImp(val context: Context) : VideoListRepository{

    override fun getVideoList() : Flow<Resources<VideoItem>> = flow {

        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE
        )

        val retriever = MediaMetadataRetriever()

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val uri = ContentUris.withAppendedId(collection, id)
                var tempItem : VideoItem
                try {
                    retriever.setDataSource(context, uri)
                    val frameBitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    tempItem = VideoItem(id, title, uri.toString(), frameBitmap)

                } catch (e: Exception) {
                    e.printStackTrace()
                    tempItem = VideoItem(id, title, uri.toString(), null)
                }

                emit(Resources.Success<VideoItem>(tempItem))
            }
        }
        retriever.release()
    }


}
