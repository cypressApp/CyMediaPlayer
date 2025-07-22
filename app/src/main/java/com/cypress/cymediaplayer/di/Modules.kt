package com.cypress.cymediaplayer.di

import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.room.Room
import com.cypress.cymediaplayer.database.CyDatabase
import com.cypress.cymediaplayer.database.LocalVideoMediator
import com.cypress.cymediaplayer.database.VideoEntity
import com.cypress.cymediaplayer.database.VideoItemPagingSource
import com.cypress.cymediaplayer.repositories.VideoItem
import com.cypress.cymediaplayer.repositories.VideoListRepository
import com.cypress.cymediaplayer.repositories.VideoListRepositoryImp
import com.cypress.cymediaplayer.repositories.VideoPlayerRepository
import com.cypress.cymediaplayer.repositories.VideoPlayerRepositoryImp
import com.cypress.cymediaplayer.viewModels.VideoListViewModel
import com.cypress.cymediaplayer.viewModels.VideoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.FileOutputStream

@OptIn(ExperimentalPagingApi::class)
val platformModule = module{

    singleOf(::VideoListRepositoryImp).bind<VideoListRepository>()
    factoryOf(::VideoPlayerRepositoryImp).bind<VideoPlayerRepository>() // new instance every time it's injected
    factory { ExoPlayer.Builder(androidContext()).build() } // new instance every time it's injected
    viewModel { VideoViewModel(get()) }


    single<CyDatabase> {
        Room.databaseBuilder(
            androidContext(),
            CyDatabase::class.java,
            "cyDatabase.db"
        ).build()
    }

    single<Pager<Int, VideoEntity>> {
        val cyDb: CyDatabase = get()
        Pager(
            config = PagingConfig(
                pageSize = 20,
//                prefetchDistance = 0,     // no prefetch
//                initialLoadSize = 20
            ),
            remoteMediator = LocalVideoMediator(
                get(), cyDb = cyDb
            ),
            pagingSourceFactory = {
                cyDb.videoListDao().pagingSource()
//                val tempContext : Context = get()
//                val videoEntityPagingSource:PagingSource<Int, VideoEntity> =
//                VideoItemPagingSource(videoEntityPagingSource) { entity ->
////                    val retriever = MediaMetadataRetriever()
////                    retriever.setDataSource(tempContext, entity.uri.toUri())
////                    val frameBitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
////                    retriever.release()
//                    VideoItem(entity.id,
//                        entity.title,
//                        entity.uri,
//                        null)
//                }
            }
        )
    }

    viewModel { VideoListViewModel(get() , get()) }

}