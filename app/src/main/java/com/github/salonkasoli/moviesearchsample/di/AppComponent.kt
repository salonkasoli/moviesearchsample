package com.github.salonkasoli.moviesearchsample.di

import com.github.salonkasoli.moviesearchsample.di.module.AppModule
import com.github.salonkasoli.moviesearchsample.di.module.IOModule
import com.github.salonkasoli.moviesearchsample.di.module.SubcomponentsModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        IOModule::class,
        AppModule::class,
        SubcomponentsModule::class
    ]
)
interface AppComponent {

    fun authComponent(): AuthComponent.Factory
    fun movieDetailComponent(): MovieDetailComponent.Factory
    fun movieSearchComponent(): MovieSearchComponent.Factory
    fun rateComponent(): RateComponent.Factory
}