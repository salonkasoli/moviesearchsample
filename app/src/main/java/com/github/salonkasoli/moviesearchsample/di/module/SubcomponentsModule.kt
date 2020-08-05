package com.github.salonkasoli.moviesearchsample.di.module

import com.github.salonkasoli.moviesearchsample.di.AuthComponent
import com.github.salonkasoli.moviesearchsample.di.MovieDetailComponent
import dagger.Module

@Module(subcomponents = [AuthComponent::class, MovieDetailComponent::class])
class SubcomponentsModule