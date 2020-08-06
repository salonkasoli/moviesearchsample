package com.github.salonkasoli.moviesearchsample.di

import com.github.salonkasoli.moviesearchsample.di.module.RateUiModule
import com.github.salonkasoli.moviesearchsample.rate.RateController
import dagger.Subcomponent


@Subcomponent(modules = [RateUiModule::class])
interface RateComponent {

    fun controller(): RateController

    @Subcomponent.Factory
    interface Factory {
        fun create(module: RateUiModule): RateComponent
    }
}