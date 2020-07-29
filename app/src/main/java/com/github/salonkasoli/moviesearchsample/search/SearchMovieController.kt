package com.github.salonkasoli.moviesearchsample.search

import android.os.Bundle
import androidx.savedstate.SavedStateRegistry
import com.github.salonkasoli.moviesearchsample.search.ui.MovieSearchState
import com.github.salonkasoli.moviesearchsample.search.ui.MoviesListWidget
import com.github.salonkasoli.moviesearchsample.search.ui.SearchMovieToolbarWidget

class SearchMovieController(
    private val moviesWidget: MoviesListWidget,
    private val cache: MovieListCache,
    private val searchInteractor: SearchMovieInteractor,
    savedStateRegistry: SavedStateRegistry
) {

    private var searchMovieToolbarWidget: SearchMovieToolbarWidget? = null

    private var currentQuery: String = ""

    init {
        savedStateRegistry.consumeRestoredStateForKey(BUNDLE_KEY)?.let { bundle: Bundle ->
            currentQuery = bundle.getString(QUERY, "")
        }
        savedStateRegistry.registerSavedStateProvider(BUNDLE_KEY, {
            return@registerSavedStateProvider Bundle().apply {
                putString(QUERY, currentQuery)
            }
        })

        moviesWidget.paginationController.loadMoreListener = {
            searchInteractor.loadMoreMovies(currentQuery)
        }
        moviesWidget.errorClickListener = {
            searchInteractor.loadMoreMovies(currentQuery)
        }
        moviesWidget.paginationController.listSizeProvider = {
            cache.get(currentQuery).movies.size
        }

        searchInteractor.successListener = success@{ query: String, newState: MovieSearchState ->
            if (query != currentQuery) {
                return@success
            }
            if (newState.isFullyLoaded()) {
                moviesWidget.paginationController.isEnabled = false
            }
            moviesWidget.showMovies(newState.movies)
        }
        searchInteractor.errorListener = error@{ query: String, oldState: MovieSearchState ->
            if (query != currentQuery) {
                return@error
            }
            moviesWidget.showError(oldState.movies)
        }
        searchInteractor.loadingCallback = loading@{ query: String, state: MovieSearchState ->
            if (query != currentQuery) {
                return@loading
            }
            moviesWidget.showLoading(state.movies)
        }
        moviesWidget.showMovies(cache.get(currentQuery).movies)
    }

    fun setSearchWidget(searchMovieToolbarWidget: SearchMovieToolbarWidget) {
        this.searchMovieToolbarWidget = searchMovieToolbarWidget
        searchMovieToolbarWidget.queryChangedListener = { query: String ->
            this.currentQuery = query
            val state: MovieSearchState = cache.get(query)
            if (query.isEmpty()) {
                moviesWidget.paginationController.isEnabled = false
                moviesWidget.showMovies(state.movies)
            } else {
                moviesWidget.paginationController.isEnabled = true
                searchInteractor.loadMoreMovies(query)
            }
        }
        searchMovieToolbarWidget.searchClickedListener = {
            searchMovieToolbarWidget.updateQuery(currentQuery)
        }
        searchMovieToolbarWidget.updateQuery(currentQuery)
    }

    companion object {
        private const val BUNDLE_KEY = "search_controller"
        private const val QUERY = "query"
    }
}