package com.cypress.cymediaplayer.repositories

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import com.cypress.cymediaplayer.database.VideoEntity

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
    fun getVideoList(pageOffset: Long , pageCount : Int) : List<VideoItem>
}

class VideoListRepositoryImp(val context: Context) : VideoListRepository{

    override fun getVideoList(pageOffset: Long , pageCount : Int) : List<VideoItem> {

        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE
        )

        val tempList: MutableList<VideoItem> = mutableListOf()

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

                val tempItem = VideoItem(id, title, uri.toString(), null)
                tempList.add(tempItem)
                index++
            }
        }

        return tempList
    }

}
