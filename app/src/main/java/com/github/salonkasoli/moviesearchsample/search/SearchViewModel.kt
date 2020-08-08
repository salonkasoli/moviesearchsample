package com.github.salonkasoli.moviesearchsample.search

import androidx.lifecycle.*
import com.github.salonkasoli.moviesearchsample.core.api.RepoError
import com.github.salonkasoli.moviesearchsample.core.api.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoSuccess
import com.github.salonkasoli.moviesearchsample.core.mvvm.LoadingState
import com.github.salonkasoli.moviesearchsample.search.api.MovieModelMapper
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchMapperFactory
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchRepository
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchResponse
import com.github.salonkasoli.moviesearchsample.search.ui.MovieSearchCache
import com.github.salonkasoli.moviesearchsample.search.ui.MovieSearchUiState
import com.github.salonkasoli.moviesearchsample.search.ui.MovieUiModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel(
    private val cache: MovieListCache,
    private val mapperFactory: MovieSearchMapperFactory,
    private val repository: MovieSearchRepository
) : ViewModel() {

    val state: LiveData<MovieSearchUiState>
        get() = _state

    var currentQuery: String = ""

    private val _state = MutableLiveData<MovieSearchUiState>()

    private var movieMapper: MovieModelMapper? = null

    fun updateQuery(query: String) {
        currentQuery = query
        loadMoreMovies()
    }

    fun loadMoreMovies() = viewModelScope.launch {
        val query: String = currentQuery
        val oldState: MovieSearchCache = cache.get(query)

        if (query.isBlank()) {
            updateState(query, MovieSearchUiState(oldState.movies, LoadingState.SUCCESS))
            return@launch
        }

        updateState(query, MovieSearchUiState(oldState.movies, LoadingState.LOADING))

        if (movieMapper == null) {
            movieMapper = mapperFactory.createMapper() ?: run {
                updateState(query, MovieSearchUiState(oldState.movies, LoadingState.ERROR))
                return@launch
            }
        }

        val searchRepoResponse: RepoResponse<MovieSearchResponse> =
            repository.searchMovie(query, oldState.lastLoadedPage + 1)
        if (searchRepoResponse is RepoError) {
            updateState(query, MovieSearchUiState(oldState.movies, LoadingState.ERROR))
            return@launch
        }

        val movieSearchResponse: MovieSearchResponse = (searchRepoResponse as RepoSuccess).data
        val uiModels: List<MovieUiModel> = movieSearchResponse.result.map { movieNetworkModel ->
            movieMapper!!.toUiModel(movieNetworkModel)
        }

        val newList = ArrayList(oldState.movies)
        newList.addAll(uiModels)
        val newState = MovieSearchCache(
            movieSearchResponse.page,
            movieSearchResponse.totalPages,
            newList
        )
        cache.put(query, newState)

        val newLoadingState = if (newState.isFullyLoaded()) {
            LoadingState.SUCCESS
        } else {
            LoadingState.WAITING
        }
        updateState(query, MovieSearchUiState(newState.movies, newLoadingState))
    }

    private fun updateState(query: String, newState: MovieSearchUiState) {
        if (query == currentQuery) {
            _state.postValue(newState)
        }
    }

    class Factory @Inject constructor(
        private val cache: MovieListCache,
        private val mapperFactory: MovieSearchMapperFactory,
        private val movieSearchRepository: MovieSearchRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(
                cache,
                mapperFactory,
                movieSearchRepository
            ) as T
        }
    }
}