package com.cypress.cymediaplayer

import android.app.Application
import com.cypress.cymediaplayer.di.initKoin
import org.koin.android.ext.koin.androidContext

class CyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@CyApplication)
        }
    }
}