package com.github.salonkasoli.moviesearchsample.di.module

import android.content.Context
import com.github.salonkasoli.moviesearchsample.auth.SessionIdCache
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailCache
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(
    private val context: Context
) {

    @Provides
    @Singleton
    fun sessionIdCache(): SessionIdCache {
        return SessionIdCache(context)
    }

    @Provides
    @Singleton
    fun movieDetailCache(): MovieDetailCache {
        return MovieDetailCache()
    }
}