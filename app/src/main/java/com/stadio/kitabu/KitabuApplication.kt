package com.stadio.kitabu

import android.app.Application
import com.stadio.kitabu.di.AppContainer

class KitabuApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
