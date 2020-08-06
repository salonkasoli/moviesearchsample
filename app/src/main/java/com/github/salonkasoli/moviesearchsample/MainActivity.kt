package com.github.salonkasoli.moviesearchsample

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.github.salonkasoli.moviesearchsample.di.module.MovieSearchUiModule
import com.github.salonkasoli.moviesearchsample.search.SearchMovieController
import com.github.salonkasoli.moviesearchsample.search.ui.SearchMovieToolbarWidget

/**
 * Экран с лентой фильмов.
 */
class MainActivity : AppCompatActivity(R.layout.activity_search) {

    private lateinit var controller: SearchMovieController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = App.instance.appComponent.movieSearchComponent()
            .create(MovieSearchUiModule(this))
            .controller()
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