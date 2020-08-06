package com.github.salonkasoli.moviesearchsample.di.module

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.github.salonkasoli.moviesearchsample.MainActivity
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.search.MovieListCache
import com.github.salonkasoli.moviesearchsample.search.SearchMovieController
import com.github.salonkasoli.moviesearchsample.search.SearchMovieInteractor
import com.github.salonkasoli.moviesearchsample.search.ui.MoviesListWidget
import dagger.Module
import dagger.Provides

@Module
class MovieSearchUiModule(
    private val activity: MainActivity
) {

    @Provides
    fun controller(
        widget: MoviesListWidget,
        cache: MovieListCache,
        interactor: SearchMovieInteractor
    ): SearchMovieController {
        return SearchMovieController(
            widget,
            cache,
            interactor,
            activity,
            activity.savedStateRegistry
        )
    }

    @Provides
    fun widget(): MoviesListWidget {
        return MoviesListWidget(
            activity.findViewById(R.id.list),
            activity.findViewById(R.id.search_status_container),
            activity.findViewById(R.id.progress_bar),
            activity.findViewById(R.id.status_text),
            activity.findViewById(R.id.travolta),
            activity.findViewById(R.id.error_image)
        )
    }

    @Provides
    fun lifecycleScope(): LifecycleCoroutineScope {
        return activity.lifecycleScope
    }
}