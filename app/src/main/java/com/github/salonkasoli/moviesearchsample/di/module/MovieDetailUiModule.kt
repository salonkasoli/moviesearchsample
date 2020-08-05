package com.github.salonkasoli.moviesearchsample.di.module

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.ui.LoadingWidget
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailActivity
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailController
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailInteractor
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailWidget
import dagger.Module
import dagger.Provides

@Module
class MovieDetailUiModule(
    private val activity: MovieDetailActivity,
    private val movieId: Int
) {

    @Provides
    fun lifecycleScope(): LifecycleCoroutineScope {
        return activity.lifecycleScope
    }

    @Provides
    fun controller(
        widget: MovieDetailWidget,
        interactor: MovieDetailInteractor
    ): MovieDetailController {
        return MovieDetailController(
            widget,
            interactor,
            movieId,
            activity,
            activity.lifecycle
        )
    }

    @Provides
    fun widget(): MovieDetailWidget {
        return MovieDetailWidget(
            LoadingWidget(activity.findViewById(R.id.loading_container)),
            activity.findViewById(R.id.movie_detail_container),
            activity.findViewById(R.id.photo),
            activity.findViewById(R.id.title),
            activity.findViewById(R.id.year),
            activity.findViewById(R.id.genres),
            activity.findViewById(R.id.vote),
            activity.findViewById(R.id.user_vote),
            activity.findViewById(R.id.divider_vote),
            activity.findViewById(R.id.overview),
            activity.findViewById(R.id.overview_divider)
        )
    }

}