package com.github.salonkasoli.moviesearchsample.di.module

import android.content.Context
import com.github.salonkasoli.moviesearchsample.auth.session.SessionRepository
import com.github.salonkasoli.moviesearchsample.auth.token.authed.AuthedTokenRepository
import com.github.salonkasoli.moviesearchsample.auth.token.newly.NewTokenRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class AuthDataModule(private val context: Context) {

    @Provides
    @Singleton
    fun newTokenRepo(retrofit: Retrofit): NewTokenRepository {
        return NewTokenRepository(retrofit, context)
    }

    @Provides
    @Singleton
    fun authedTokenRepo(retrofit: Retrofit): AuthedTokenRepository {
        return AuthedTokenRepository(retrofit, context)
    }

    @Provides
    @Singleton
    fun sessionRepository(retrofit: Retrofit): SessionRepository {
        return SessionRepository(retrofit, context)
    }
}