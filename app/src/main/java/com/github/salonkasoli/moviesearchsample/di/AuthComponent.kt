package com.github.salonkasoli.moviesearchsample.di

import com.github.salonkasoli.moviesearchsample.auth.AuthViewModel
import com.github.salonkasoli.moviesearchsample.auth.ui.AuthWidget
import com.github.salonkasoli.moviesearchsample.di.module.AuthUiModule
import dagger.Subcomponent

@Subcomponent(modules = [AuthUiModule::class])
interface AuthComponent {

    fun widget(): AuthWidget

    fun vmFactory(): AuthViewModel.Factory

    @Subcomponent.Factory
    interface Factory {
        fun create(module: AuthUiModule): AuthComponent
    }
}