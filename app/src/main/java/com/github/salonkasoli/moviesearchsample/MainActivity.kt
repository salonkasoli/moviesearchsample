package com.github.salonkasoli.moviesearchsample

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.salonkasoli.moviesearchsample.configuration.ConfigRepository
import com.github.salonkasoli.moviesearchsample.genre.GenresRepository
import com.github.salonkasoli.moviesearchsample.search.MovieListCache
import com.github.salonkasoli.moviesearchsample.search.SearchMovieController
import com.github.salonkasoli.moviesearchsample.search.SearchMovieInteractor
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchMapperFactory
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchRepository
import com.github.salonkasoli.moviesearchsample.search.ui.MoviesListWidget
import com.github.salonkasoli.moviesearchsample.search.ui.SearchMovieToolbarWidget
import com.google.gson.Gson
import retrofit2.Retrofit

/**
 * Экран с лентой фильмов.
 */
class MainActivity : AppCompatActivity(R.layout.activity_search) {

    private lateinit var controller: SearchMovieController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cache: MovieListCache = App.get(MovieListCache::class.java)
        val retrofit: Retrofit = App.get(Retrofit::class.java)
        controller = SearchMovieController(
            MoviesListWidget(
                findViewById(R.id.list),
                findViewById(R.id.search_status_container),
                findViewById(R.id.progress_bar),
                findViewById(R.id.status_text),
                findViewById(R.id.travolta),
                findViewById(R.id.error_image)
            ),
            cache,
            SearchMovieInteractor(
                lifecycleScope,
                MovieSearchRepository(retrofit, this),
                MovieSearchMapperFactory(
                    ConfigRepository(retrofit, this, Gson()),
                    GenresRepository(retrofit, this, Gson())
                ),
                cache
            ),
            this,
            savedStateRegistry
        )
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        controller.setSearchWidget(
            SearchMovieToolbarWidget(menu.findItem(R.id.search), savedStateRegistry, lifecycle)
        )
        return true
    }
}