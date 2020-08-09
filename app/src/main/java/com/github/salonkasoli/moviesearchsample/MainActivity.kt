package com.github.salonkasoli.moviesearchsample

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.salonkasoli.moviesearchsample.core.mvvm.LoadingState
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailActivity
import com.github.salonkasoli.moviesearchsample.di.MovieSearchComponent
import com.github.salonkasoli.moviesearchsample.di.module.MovieSearchUiModule
import com.github.salonkasoli.moviesearchsample.search.SearchViewModel
import com.github.salonkasoli.moviesearchsample.search.ui.MovieSearchUiState
import com.github.salonkasoli.moviesearchsample.search.ui.MovieUiModel
import com.github.salonkasoli.moviesearchsample.search.ui.SearchMovieToolbarWidget

/**
 * Экран с лентой фильмов.
 */
class MainActivity : AppCompatActivity(R.layout.activity_search) {

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val component: MovieSearchComponent = App.instance.appComponent.movieSearchComponent()
            .create(MovieSearchUiModule(this))

        val widget = component.widget()
        viewModel = ViewModelProvider(this, component.vmFactory())
            .get(SearchViewModel::class.java)

        viewModel.handleState(savedStateRegistry)

        widget.movieClickListener = { movieUiModel: MovieUiModel ->
            startActivity(
                MovieDetailActivity.intent(this, movieUiModel.id, movieUiModel.title)
            )
        }
        widget.paginationController.loadMoreListener = {
            viewModel.loadMoreMovies()
        }
        widget.errorClickListener = {
            viewModel.loadMoreMovies()
        }

        viewModel.state.observe(this, Observer { state: MovieSearchUiState ->
            val movies: List<MovieUiModel> = state.movies
            when (state.loadingState) {
                LoadingState.WAITING -> {
                    widget.paginationController.isEnabled = true
                    widget.paginationController.isLoading = false
                    widget.showMovies(movies)
                }
                LoadingState.LOADING -> {
                    widget.paginationController.isEnabled = true
                    widget.paginationController.isLoading = true
                    widget.showLoading(movies)
                }
                LoadingState.SUCCESS -> {
                    widget.paginationController.isEnabled = false
                    widget.showMovies(movies)
                }
                LoadingState.ERROR -> {
                    widget.paginationController.isEnabled = true
                    widget.paginationController.isLoading = true
                    widget.showError(movies)
                }
            }
        })

        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val widget = SearchMovieToolbarWidget(
            menu.findItem(R.id.search),
            savedStateRegistry,
            lifecycle
        )
        widget.queryChangedListener = { query: String ->
            viewModel.updateQuery(query)
        }
        widget.searchClickedListener = {
            widget.updateQuery(viewModel.currentQuery)
        }

        widget.updateQuery(viewModel.currentQuery)
        return true
    }
}