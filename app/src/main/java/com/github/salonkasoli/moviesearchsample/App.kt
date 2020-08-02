package com.github.salonkasoli.moviesearchsample

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.github.salonkasoli.moviesearchsample.auth.SessionIdCache
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailCache
import com.github.salonkasoli.moviesearchsample.search.MovieListCache
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fresco.initialize(this)

        put(MovieListCache())
        put(MovieDetailCache())
        put(SessionIdCache(this))

        put(
            Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Const.MOVIE_DB_URL)
                .build()
        )
    }

    companion object {
        lateinit var instance: App

        // Вместо Dagger2 здесь живет небольшой контейнер зависимостей.
        private val deps = HashMap<Class<out Any>, Any>()

        fun <T : Any> put(any: T) {
            deps[any::class.java] = any
        }

        fun <T> get(clazz: Class<T>): T {
            return deps[clazz] as T
        }
    }
}