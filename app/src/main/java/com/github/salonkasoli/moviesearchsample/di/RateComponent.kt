package com.github.salonkasoli.moviesearchsample.di

import com.github.salonkasoli.moviesearchsample.di.module.RateUiModule
import com.github.salonkasoli.moviesearchsample.rate.RateViewModel
import com.github.salonkasoli.moviesearchsample.rate.RateWidget
import dagger.Subcomponent


@Subcomponent(modules = [RateUiModule::class])
interface RateComponent {

    fun widget(): RateWidget

    fun vmFactory(): RateViewModel.Factory

    @Subcomponent.Factory
    interface Factory {
        fun create(module: RateUiModule): RateComponent
    }
}