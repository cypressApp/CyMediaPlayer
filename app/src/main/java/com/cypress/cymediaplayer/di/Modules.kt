package com.cypress.cymediaplayer.di

import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import com.cypress.cymediaplayer.database.CyDatabase
import com.cypress.cymediaplayer.database.LocalVideoMediator
import com.cypress.cymediaplayer.database.VideoEntity
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
import org.koin.core.module.dsl.viewModelOf

@OptIn(ExperimentalPagingApi::class)
val platformModule = module{

    singleOf(::VideoListRepositoryImp).bind<VideoListRepository>()
    factoryOf(::VideoPlayerRepositoryImp).bind<VideoPlayerRepository>() // new instance every time it's injected
    factory { ExoPlayer.Builder(androidContext()).build() } // new instance every time it's injected
//    viewModel { VideoViewModel(get()) }
    viewModelOf(::VideoViewModel)

    single<CyDatabase> {
        Room.databaseBuilder(
            androidContext(),
            CyDatabase::class.java,
            "cyDatabase.db"
        ).build()
    }

    single<Pager<Int, VideoEntity>> {

        Pager(
            config = PagingConfig(
                pageSize = 20,
//                prefetchDistance = 0,     // no prefetch
//                initialLoadSize = 20
            ),
            remoteMediator = LocalVideoMediator(
                get(), get()
            ),
            pagingSourceFactory = {
                (get() as CyDatabase).videoListDao().pagingSource()
            }
        )
    }

    viewModelOf (::VideoListViewModel)

    single<ImageLoader>{
        ImageLoader.Builder(get())
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }

}