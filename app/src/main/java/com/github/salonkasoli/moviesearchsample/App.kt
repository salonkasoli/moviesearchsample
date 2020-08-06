package com.github.salonkasoli.moviesearchsample

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.github.salonkasoli.moviesearchsample.di.AppComponent
import com.github.salonkasoli.moviesearchsample.di.DaggerAppComponent
import com.github.salonkasoli.moviesearchsample.di.module.AppModule
import com.github.salonkasoli.moviesearchsample.di.module.IOModule

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fresco.initialize(this)
        appComponent = DaggerAppComponent.builder()
            .iOModule(IOModule())
            .appModule(AppModule(this))
            .build()
    }

    companion object {
        lateinit var instance: App
    }
}