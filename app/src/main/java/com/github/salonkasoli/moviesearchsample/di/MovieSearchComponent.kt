package com.github.salonkasoli.moviesearchsample.di

import com.github.salonkasoli.moviesearchsample.di.module.MovieSearchUiModule
import com.github.salonkasoli.moviesearchsample.search.SearchMovieController
import dagger.Subcomponent

@Subcomponent(modules = [MovieSearchUiModule::class])
interface MovieSearchComponent {

    fun controller(): SearchMovieController

    @Subcomponent.Factory
    interface Factory {
        fun create(module: MovieSearchUiModule): MovieSearchComponent
    }
}