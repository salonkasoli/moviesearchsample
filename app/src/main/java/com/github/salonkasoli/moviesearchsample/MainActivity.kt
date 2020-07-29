package com.github.salonkasoli.moviesearchsample

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.salonkasoli.moviesearchsample.search.MovieListCache
import com.github.salonkasoli.moviesearchsample.search.SearchMovieController
import com.github.salonkasoli.moviesearchsample.search.ui.MoviesListWidget
import com.github.salonkasoli.moviesearchsample.search.ui.SearchMovieToolbarWidget

class MainActivity : AppCompatActivity(R.layout.activity_search) {

    private lateinit var controller: SearchMovieController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = SearchMovieController(
            MoviesListWidget(findViewById(R.id.list)),
            App.get(MovieListCache::class.java),
            this,
            lifecycleScope,
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