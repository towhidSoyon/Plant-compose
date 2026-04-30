package com.plant.compose

import android.app.Application
import com.plant.compose.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PlantApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PlantApplication)
            modules(appModule)
        }
    }
}
