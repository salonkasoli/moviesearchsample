package com.github.salonkasoli.moviesearchsample.di.module

import android.content.Context
import com.github.salonkasoli.moviesearchsample.Const
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class IOModule(
    context: Context
) {

    @Provides
    @Singleton
    fun gson(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun retrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Const.MOVIE_DB_URL)
            .build()
    }

}