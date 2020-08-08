package com.github.salonkasoli.moviesearchsample.di.module

import com.github.salonkasoli.moviesearchsample.MainActivity
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.search.ui.MoviesListWidget
import dagger.Module
import dagger.Provides

@Module
class MovieSearchUiModule(
    private val activity: MainActivity
) {

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
}