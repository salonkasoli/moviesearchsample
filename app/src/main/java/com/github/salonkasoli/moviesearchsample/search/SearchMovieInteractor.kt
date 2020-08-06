package com.github.salonkasoli.moviesearchsample.search

import androidx.lifecycle.LifecycleCoroutineScope
import com.github.salonkasoli.moviesearchsample.core.api.RepoError
import com.github.salonkasoli.moviesearchsample.core.api.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoSuccess
import com.github.salonkasoli.moviesearchsample.search.api.MovieModelMapper
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchMapperFactory
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchRepository
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchResponse
import com.github.salonkasoli.moviesearchsample.search.ui.MovieSearchState
import com.github.salonkasoli.moviesearchsample.search.ui.MovieUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchMovieInteractor @Inject constructor(
    private val scope: LifecycleCoroutineScope,
    private val movieRepository: MovieSearchRepository,
    private val mapperFactory: MovieSearchMapperFactory,
    private val cache: MovieListCache
) {

    var loadingCallback: ((query: String, currentState: MovieSearchState) -> Unit)? = null
    var successListener: ((query: String, newState: MovieSearchState) -> Unit)? = null
    var errorListener: ((query: String, prevState: MovieSearchState) -> Unit)? = null

    private var movieMapper: MovieModelMapper? = null

    fun loadMoreMovies(query: String) = scope.launchWhenResumed {
        val oldState: MovieSearchState = cache.get(query)
        withContext(Dispatchers.Main) {
            loadingCallback?.invoke(query, oldState)
        }

        if (movieMapper == null) {
            movieMapper = mapperFactory.createMapper() ?: run {
                withContext(Dispatchers.Main) {
                    errorListener?.invoke(query, oldState)
                }
                return@launchWhenResumed
            }
        }

        val searchRepoResponse: RepoResponse<MovieSearchResponse> =
            movieRepository.searchMovie(query, oldState.lastLoadedPage + 1)
        if (searchRepoResponse is RepoError) {
            withContext(Dispatchers.Main) {
                errorListener?.invoke(query, oldState)
            }
            return@launchWhenResumed
        }

        val movieSearchResponse: MovieSearchResponse = (searchRepoResponse as RepoSuccess).data
        val uiModels: List<MovieUiModel> = movieSearchResponse.result.map { movieNetworkModel ->
            movieMapper!!.toUiModel(movieNetworkModel)
        }

        val newList = ArrayList(oldState.movies)
        newList.addAll(uiModels)
        val newState = MovieSearchState(
            movieSearchResponse.page,
            movieSearchResponse.totalPages,
            newList
        )
        cache.put(query, newState)

        withContext(Dispatchers.Main) {
            successListener?.invoke(query, newState)
        }
    }

}