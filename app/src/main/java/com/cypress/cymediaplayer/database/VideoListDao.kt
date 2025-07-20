package com.cypress.cymediaplayer.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface VideoListDao {

    @Upsert
    suspend fun upsert(videoEntity: List<VideoEntity>)

    @Query("SELECT * FROM VideoEntity")
    fun pagingSource(): PagingSource<Int, VideoEntity>

    @Query("DELETE FROM VideoEntity")
    suspend fun clearAll()

}