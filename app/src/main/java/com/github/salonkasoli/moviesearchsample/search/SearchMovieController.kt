package com.github.salonkasoli.moviesearchsample.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.savedstate.SavedStateRegistry
import com.github.salonkasoli.moviesearchsample.configuration.Config
import com.github.salonkasoli.moviesearchsample.configuration.ConfigRepository
import com.github.salonkasoli.moviesearchsample.core.RepoError
import com.github.salonkasoli.moviesearchsample.core.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.RepoSuccess
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchMapper
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchRepository
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchResponse
import com.github.salonkasoli.moviesearchsample.search.ui.MovieSearchState
import com.github.salonkasoli.moviesearchsample.search.ui.MovieUiModel
import com.github.salonkasoli.moviesearchsample.search.ui.MoviesListWidget
import com.github.salonkasoli.moviesearchsample.search.ui.SearchMovieToolbarWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchMovieController(
    private val moviesWidget: MoviesListWidget,
    private val cache: MovieListCache,
    context: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    savedStateRegistry: SavedStateRegistry
) {

    private val movieRepository = MovieSearchRepository(context)
    private val configRepository = ConfigRepository(context)

    private var movieMapper: MovieSearchMapper? = null
    private var searchMovieToolbarWidget: SearchMovieToolbarWidget? = null

    private var query: String = ""

    init {
        savedStateRegistry.consumeRestoredStateForKey(BUNDLE_KEY)?.let { bundle: Bundle ->
            query = bundle.getString(QUERY, "")
        }
        savedStateRegistry.registerSavedStateProvider(BUNDLE_KEY, {
            return@registerSavedStateProvider Bundle().apply {
                putString(QUERY, query)
            }
        })

        moviesWidget.paginationController.loadMoreListener = {
            val state: MovieSearchState = cache.get(query)
            searchMovies(query, state.lastLoadedPage + 1)
        }
        moviesWidget.errorClickListener = {
            val state: MovieSearchState = cache.get(query)
            searchMovies(query, state.lastLoadedPage + 1)
        }
        moviesWidget.paginationController.listSizeProvider = {
            cache.get(query).movies.size
        }

        moviesWidget.showMovies(cache.get(query).movies)
    }

    fun setSearchWidget(searchMovieToolbarWidget: SearchMovieToolbarWidget) {
        this.searchMovieToolbarWidget = searchMovieToolbarWidget
        searchMovieToolbarWidget.queryChangedListener = { query: String ->
            this.query = query
            val state: MovieSearchState = cache.get(query)
            if (query.isEmpty()) {
                moviesWidget.paginationController.isEnabled = false
                moviesWidget.showMovies(state.movies)
            } else {
                moviesWidget.paginationController.isEnabled = true
                searchMovies(query, state.lastLoadedPage + 1)
            }
        }
        searchMovieToolbarWidget.searchClickedListener = {
            searchMovieToolbarWidget.updateQuery(query)
        }
        searchMovieToolbarWidget.updateQuery(query)
    }


    private fun searchMovies(query: String, page: Int) = lifecycleScope.launchWhenResumed {
        Log.wtf("lol", "search query = $query")
        val state: MovieSearchState = cache.get(query)
        moviesWidget.showLoading(state.movies)

        if (movieMapper == null) {
            movieMapper = createMapper() ?: run {
                withContext(Dispatchers.Main) {
                    moviesWidget.showError(state.movies)
                }
                return@launchWhenResumed
            }
        }

        val searchRepoResponse: RepoResponse<MovieSearchResponse> =
            movieRepository.searchMovie(query, page)
        if (searchRepoResponse is RepoError) {
            withContext(Dispatchers.Main) {
                moviesWidget.showError(state.movies)
            }
            return@launchWhenResumed
        }

        val movieSearchResponse: MovieSearchResponse = (searchRepoResponse as RepoSuccess).data
        val uiModels: List<MovieUiModel> = movieSearchResponse.result.map { movieNetworkModel ->
            movieMapper!!.toUiModel(movieNetworkModel)
        }

        state.lastLoadedPage = movieSearchResponse.page
        state.totalPages = movieSearchResponse.totalPages
        state.movies.addAll(uiModels)


        Log.wtf("lol", "new state = $state")

        withContext(Dispatchers.Main) {
            moviesWidget.showMovies(state.movies)
            if (state.isFullyLoaded()) {
                moviesWidget.paginationController.isEnabled = false
            }
        }
    }

    /**
     * Создаем маппер из Network модельки в UI.
     * Потенциально придется залезть в интернет т.к. url до фоток нужно формировать хитрым образом.
     *
     * @return Маппер или null, если не удалось получить конфигурацию АПИ.
     */
    private suspend fun createMapper(): MovieSearchMapper? = withContext(Dispatchers.IO) {
        val configResponse: RepoResponse<Config> = configRepository.getConfig()
        return@withContext when (configResponse) {
            is RepoError -> null
            is RepoSuccess -> MovieSearchMapper(configResponse.data)
        }
    }

    companion object {
        private const val BUNDLE_KEY = "search_controller"
        private const val QUERY = "query"
    }
}