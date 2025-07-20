package com.cypress.cymediaplayer.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VideoEntity(
    val title: String,
    val uri: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
