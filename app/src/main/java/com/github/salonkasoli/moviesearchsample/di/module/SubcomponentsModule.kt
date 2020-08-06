package com.github.salonkasoli.moviesearchsample.di.module

import com.github.salonkasoli.moviesearchsample.di.AuthComponent
import com.github.salonkasoli.moviesearchsample.di.MovieDetailComponent
import com.github.salonkasoli.moviesearchsample.di.MovieSearchComponent
import dagger.Module

@Module(
    subcomponents = [
        AuthComponent::class,
        MovieDetailComponent::class,
        MovieSearchComponent::class
    ]
)
class SubcomponentsModule