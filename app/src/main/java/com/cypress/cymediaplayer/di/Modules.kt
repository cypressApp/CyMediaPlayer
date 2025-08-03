package com.cypress.cymediaplayer.di

import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.ExperimentalPagingApi
import androidx.room.Room
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import com.cypress.cymediaplayer.database.CyDatabase
import com.cypress.cymediaplayer.data.repositories.RemoteControlRepository
import com.cypress.cymediaplayer.data.repositories.RemoteControlRepositoryImp
import com.cypress.cymediaplayer.data.repositories.RemoteReceiverRepository
import com.cypress.cymediaplayer.data.repositories.RemoteReceiverRepositoryImp
import com.cypress.cymediaplayer.data.local.TcpClientApi
import com.cypress.cymediaplayer.data.local.TcpServerApi
import com.cypress.cymediaplayer.data.repositories.VideoListRepository
import com.cypress.cymediaplayer.data.repositories.VideoListRepositoryImp
import com.cypress.cymediaplayer.data.repositories.VideoPlayerRepository
import com.cypress.cymediaplayer.data.repositories.VideoPlayerRepositoryImp
import com.cypress.cymediaplayer.data.repositories.amplify.AuthRepository
import com.cypress.cymediaplayer.data.repositories.amplify.AuthRepositoryImp
import com.cypress.cymediaplayer.viewModels.RemoteControlViewModel
import com.cypress.cymediaplayer.viewModels.RemoteReceiverViewModel
import com.cypress.cymediaplayer.viewModels.VideoListViewModel
import com.cypress.cymediaplayer.viewModels.VideoViewModel
import com.cypress.cymediaplayer.viewModels.amplify.AuthViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(ExperimentalPagingApi::class)
val platformModule = module{

    singleOf(::VideoListRepositoryImp).bind<VideoListRepository>()
    factoryOf(::VideoPlayerRepositoryImp).bind<VideoPlayerRepository>() // new instance every time it's injected
    factory { ExoPlayer.Builder(androidContext()).build() } // new instance every time it's injected
    singleOf(::AuthRepositoryImp).bind<AuthRepository>()

    viewModelOf(::VideoViewModel)

    single<CyDatabase> {
        Room.databaseBuilder(
            androidContext(),
            CyDatabase::class.java,
            "cyDatabase.db"
        ).build()
    }

    viewModelOf (::VideoListViewModel)

    single<ImageLoader>{
        ImageLoader.Builder(get())
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }

    viewModelOf(::AuthViewModel)

    viewModelOf(::RemoteControlViewModel)
    singleOf(::RemoteControlRepositoryImp).bind<RemoteControlRepository>()

    viewModelOf(::RemoteReceiverViewModel)
    singleOf(::RemoteReceiverRepositoryImp).bind<RemoteReceiverRepository>()

    single<TcpServerApi>{
        TcpServerApi()
    }

    factory<TcpClientApi> { TcpClientApi() }

}