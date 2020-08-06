package com.github.salonkasoli.moviesearchsample.di.module

import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.ui.LoadingWidget
import com.github.salonkasoli.moviesearchsample.rate.RateActivity
import com.github.salonkasoli.moviesearchsample.rate.RateWidget
import dagger.Module
import dagger.Provides

@Module
class RateUiModule(
    private val activity: RateActivity
) {

    @Provides
    fun widget(): RateWidget {
        return RateWidget(
            activity.findViewById(R.id.seek_bar_container),
            activity.findViewById(R.id.rate_text),
            activity.findViewById(R.id.seek_bar),
            activity.findViewById(R.id.save_button),
            LoadingWidget(activity.findViewById(R.id.loading_container)),
            activity.savedStateRegistry
        )
    }
}