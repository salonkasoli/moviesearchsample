package com.github.salonkasoli.moviesearchsample.detail.api

import android.content.Context
import androidx.annotation.WorkerThread
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.SessionIdCache
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailCache
import com.github.salonkasoli.moviesearchsample.detail.ui.MovieDetailUiModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDetailRepository @Inject constructor(
    private val cache: MovieDetailCache,
    private val retrofit: Retrofit,
    private val mapperFactory: MovieDetailModelMapperFactory,
    context: Context,
    private val sessionIdCache: SessionIdCache
) {

    private val apiKey = context.getString(R.string.moviedb_api_key)

    private val mapper: MovieDetailModelMapper by lazy {
        return@lazy mapperFactory.createMapper()
    }

    fun getMovieDetailObservable(id: Int): Observable<MovieDetailUiModel> {
        return Observable.just(id)
            .observeOn(Schedulers.io())
            .map { movieId -> getMovieDetails(movieId) }
    }

    @Throws(Exception::class)
    @WorkerThread
    fun getMovieDetails(id: Int): MovieDetailUiModel {
        cache.get(id)?.let {
            return it
        }

        val res: Response<MovieDetailNetworkModel> = retrofit.create(MovieDetailApi::class.java)
            .getMovieDetail(id, apiKey, sessionIdCache.getSessionId())
            .execute()

        if (!res.isSuccessful || res.body() == null) {
            throw IllegalStateException("response = $res, body = ${res.body()}")
        }

        val networkMovieDetail: MovieDetailNetworkModel = res.body()!!
        val uiMovieDetail: MovieDetailUiModel = mapper.toUiModel(networkMovieDetail)

        cache.put(id, uiMovieDetail)
        return uiMovieDetail
    }
}