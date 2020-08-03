package com.github.salonkasoli.moviesearchsample.di

import com.github.salonkasoli.moviesearchsample.auth.AuthController
import com.github.salonkasoli.moviesearchsample.di.module.AuthUiModule
import dagger.Subcomponent

@Subcomponent(modules = [AuthUiModule::class])
interface AuthComponent {

    fun controller(): AuthController

    @Subcomponent.Factory
    interface Factory {
        fun create(module: AuthUiModule): AuthComponent
    }
}