package com.github.salonkasoli.moviesearchsample.search.api

import android.content.Context
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.search.MovieListCache
import com.github.salonkasoli.moviesearchsample.search.ui.MovieSearchCache
import com.github.salonkasoli.moviesearchsample.search.ui.MovieUiModel
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieSearchRepository @Inject constructor(
    private val retrofit: Retrofit,
    context: Context,
    private val cache: MovieListCache,
    private val mapperFactory: MovieSearchMapperFactory
) {

    private val apiKey = context.getString(R.string.moviedb_api_key)

    private val movieMapper: MovieModelMapper by lazy {
        mapperFactory.createMapper()
    }

    fun getCached(query: String): MovieSearchCache {
        return cache.get(query)
    }

    fun searchMovie(query: String, page: Int): MovieSearchResponse {
        val response: Response<MovieSearchResponse> = retrofit.create(MovieSearchApi::class.java)
            .searchMovie(query, page, apiKey)
            .execute()

        if (!response.isSuccessful || response.body() == null) {
            throw  IllegalStateException("response = $response, body = ${response.body()}")
        }
        return response.body()!!
    }

    fun loadMore(query: String): MovieSearchCache {
        val oldState: MovieSearchCache = cache.get(query)

        val movieSearchResponse: MovieSearchResponse =
            searchMovie(query, oldState.lastLoadedPage + 1)

        val uiModels: List<MovieUiModel> = movieSearchResponse.result.map { movieNetworkModel ->
            movieMapper.toUiModel(movieNetworkModel)
        }

        val newList = ArrayList(oldState.movies)
        newList.addAll(uiModels)
        val newState = MovieSearchCache(
            movieSearchResponse.page,
            movieSearchResponse.totalPages,
            newList
        )

        cache.put(query, newState)
        return newState
    }
}