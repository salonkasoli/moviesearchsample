package com.github.salonkasoli.moviesearchsample

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.github.salonkasoli.moviesearchsample.search.MovieListCache

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fresco.initialize(this)

        put(MovieListCache())
    }

    companion object {
        lateinit var instance: App

        private val deps = HashMap<Class<out Any>, Any>()

        fun <T : Any> put(any: T) {
            deps[any::class.java] = any
        }

        fun <T> get(clazz: Class<T>) : T {
            return deps[clazz] as T
        }
    }
}