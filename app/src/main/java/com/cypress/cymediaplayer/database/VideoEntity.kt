package com.cypress.cymediaplayer.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cypress.cymediaplayer.repositories.VideoItem

@Entity
data class VideoEntity(
    val title: String,
    val uri: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)

fun VideoEntity.toVideoItem(): VideoItem {
    return VideoItem(
        id = id,
        title = title,
        uri = uri,
        null
    )
}