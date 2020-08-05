package com.github.salonkasoli.moviesearchsample.di

import com.github.salonkasoli.moviesearchsample.detail.MovieDetailController
import com.github.salonkasoli.moviesearchsample.di.module.MovieDetailUiModule
import dagger.Subcomponent

@Subcomponent(modules = [MovieDetailUiModule::class])
interface MovieDetailComponent {

    fun controller(): MovieDetailController

    @Subcomponent.Factory
    interface Factory {
        fun create(module: MovieDetailUiModule): MovieDetailComponent
    }
}