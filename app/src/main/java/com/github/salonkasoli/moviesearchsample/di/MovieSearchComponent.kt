package com.github.salonkasoli.moviesearchsample.di

import com.github.salonkasoli.moviesearchsample.di.module.MovieSearchUiModule
import com.github.salonkasoli.moviesearchsample.search.SearchViewModel
import com.github.salonkasoli.moviesearchsample.search.ui.MoviesListWidget
import dagger.Subcomponent

@Subcomponent(modules = [MovieSearchUiModule::class])
interface MovieSearchComponent {

    fun widget(): MoviesListWidget

    fun vmFactory(): SearchViewModel.Factory

    @Subcomponent.Factory
    interface Factory {
        fun create(module: MovieSearchUiModule): MovieSearchComponent
    }
}