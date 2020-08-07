package com.github.salonkasoli.moviesearchsample.di

import com.github.salonkasoli.moviesearchsample.detail.MovieDetailViewModel
import com.github.salonkasoli.moviesearchsample.detail.ui.MovieDetailWidget
import com.github.salonkasoli.moviesearchsample.di.module.MovieDetailUiModule
import dagger.Subcomponent

@Subcomponent(modules = [MovieDetailUiModule::class])
interface MovieDetailComponent {

    fun widget(): MovieDetailWidget

    fun vmFactory(): MovieDetailViewModel.Factory

    @Subcomponent.Factory
    interface Factory {
        fun create(module: MovieDetailUiModule): MovieDetailComponent
    }
}