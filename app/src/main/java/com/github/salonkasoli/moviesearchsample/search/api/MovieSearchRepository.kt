package com.github.salonkasoli.moviesearchsample.search.api

import android.content.Context
import androidx.annotation.WorkerThread
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.search.MovieListCache
import com.github.salonkasoli.moviesearchsample.search.ui.MovieSearchCache
import com.github.salonkasoli.moviesearchsample.search.ui.MovieUiModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
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

    fun loadMoreObservable(query: String): Observable<MovieSearchCache> {
        return Observable.just(query)
            .observeOn(Schedulers.io())
            .map { cache.get(query) }
            .map { oldState: MovieSearchCache ->
                return@map mergeState(
                    oldState,
                    searchMovie(query, oldState.lastLoadedPage + 1)
                )
            }
            // Кэшируем данные
            .doOnNext { movieSearchCache: MovieSearchCache ->
                cache.put(query, movieSearchCache)
            }
    }

    @Throws(Exception::class)
    @WorkerThread
    private fun searchMovie(query: String, page: Int): MovieSearchResponse {
        val response: Response<MovieSearchResponse> = retrofit.create(MovieSearchApi::class.java)
            .searchMovie(query, page, apiKey)
            .execute()

        if (!response.isSuccessful || response.body() == null) {
            throw  IllegalStateException("response = $response, body = ${response.body()}")
        }
        return response.body()!!
    }

    private fun mergeState(
        oldState: MovieSearchCache,
        response: MovieSearchResponse
    ): MovieSearchCache {
        val uiModels: List<MovieUiModel> = response.result.map { movieNetworkModel ->
            movieMapper.toUiModel(movieNetworkModel)
        }

        val newList = ArrayList(oldState.movies)
        newList.addAll(uiModels)
        return MovieSearchCache(
            response.page,
            response.totalPages,
            newList
        )
    }
}