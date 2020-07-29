package com.github.salonkasoli.moviesearchsample.search

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.github.salonkasoli.moviesearchsample.configuration.Config
import com.github.salonkasoli.moviesearchsample.configuration.ConfigRepository
import com.github.salonkasoli.moviesearchsample.core.RepoError
import com.github.salonkasoli.moviesearchsample.core.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.RepoSuccess
import com.github.salonkasoli.moviesearchsample.genre.GenreResponse
import com.github.salonkasoli.moviesearchsample.genre.GenresRepository
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchMapper
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchRepository
import com.github.salonkasoli.moviesearchsample.search.api.MovieSearchResponse
import com.github.salonkasoli.moviesearchsample.search.ui.MovieSearchState
import com.github.salonkasoli.moviesearchsample.search.ui.MovieUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchMovieInteractor(
    private val scope: LifecycleCoroutineScope,
    private val movieRepository: MovieSearchRepository,
    private val configRepository: ConfigRepository,
    private val genresRepository: GenresRepository,
    private val cache: MovieListCache
) {

    var loadingCallback: ((query: String, currentState: MovieSearchState) -> Unit)? = null
    var successListener: ((query: String, newState: MovieSearchState) -> Unit)? = null
    var errorListener: ((query: String, prevState: MovieSearchState) -> Unit)? = null

    private var movieMapper: MovieSearchMapper? = null

    fun loadMoreMovies(query: String) = scope.launchWhenResumed {
        Log.wtf("lol", "search query = $query")
        val oldState: MovieSearchState = cache.get(query)
        withContext(Dispatchers.Main) {
            loadingCallback?.invoke(query, oldState)
        }

        if (movieMapper == null) {
            movieMapper = createMapper() ?: run {
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
        Log.wtf("lol", "new state = $newState")

        withContext(Dispatchers.Main) {
            successListener?.invoke(query, newState)
        }
    }

    /**
     * Создаем маппер из Network модельки в UI.
     * Потенциально придется залезть в интернет т.к. url до фоток нужно формировать хитрым образом,
     * а также чтобы смапить жанры нужно дополнительное АПИ.
     *
     * @return Маппер или null, если не удалось получить конфигурацию АПИ.
     */
    private suspend fun createMapper(): MovieSearchMapper? = withContext(Dispatchers.IO) {
        val configResponse: RepoResponse<Config> = configRepository.getConfig()
        if (configResponse is RepoError) {
            return@withContext null
        }
        configResponse as RepoSuccess

        val genresResponse: RepoResponse<GenreResponse> = genresRepository.getMovieGenres()
        if (genresResponse is RepoError) {
            return@withContext null
        }
        genresResponse as RepoSuccess

        return@withContext MovieSearchMapper(configResponse.data, genresResponse.data.genres)
    }

}