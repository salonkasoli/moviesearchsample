package com.github.salonkasoli.moviesearchsample.di.module

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.ui.LoadingWidget
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailCache
import com.github.salonkasoli.moviesearchsample.rate.RateActivity
import com.github.salonkasoli.moviesearchsample.rate.RateController
import com.github.salonkasoli.moviesearchsample.rate.RateWidget
import com.github.salonkasoli.moviesearchsample.rate.api.RateInteractor
import dagger.Module
import dagger.Provides

@Module
class RateUiModule(
    private val activity: RateActivity,
    private val movieId: Int
) {

    @Provides
    fun controller(
        widget: RateWidget,
        interactor: RateInteractor,
        cache: MovieDetailCache
    ): RateController {
        return RateController(
            widget,
            interactor,
            cache,
            movieId,
            activity
        )
    }

    @Provides
    fun lifecycleScope(): LifecycleCoroutineScope {
        return activity.lifecycleScope
    }

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