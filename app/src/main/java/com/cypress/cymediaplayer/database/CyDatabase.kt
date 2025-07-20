package com.cypress.cymediaplayer.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [VideoEntity::class],
    version = 1
)
abstract class CyDatabase: RoomDatabase() {

    abstract fun videoListDao(): VideoListDao

}