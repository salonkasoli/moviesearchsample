package com.github.salonkasoli.moviesearchsample.di.module

import androidx.savedstate.SavedStateRegistry
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.ui.LoadingWidget
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailActivity
import com.github.salonkasoli.moviesearchsample.detail.ui.MovieDetailWidget
import dagger.Module
import dagger.Provides

@Module
class MovieDetailUiModule(
    private val activity: MovieDetailActivity,
    private val movieId: Int
) {

    @Provides
    fun movieId(): Int {
        return movieId
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

    @Provides
    fun savedStateRegistry(): SavedStateRegistry {
        return activity.savedStateRegistry
    }
}