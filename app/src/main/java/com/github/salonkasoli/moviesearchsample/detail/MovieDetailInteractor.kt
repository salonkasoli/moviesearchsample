package com.github.salonkasoli.moviesearchsample.detail

import androidx.lifecycle.LifecycleCoroutineScope
import com.github.salonkasoli.moviesearchsample.core.api.RepoError
import com.github.salonkasoli.moviesearchsample.core.api.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoSuccess
import com.github.salonkasoli.moviesearchsample.detail.api.MovieDetailModelMapper
import com.github.salonkasoli.moviesearchsample.detail.api.MovieDetailModelMapperFactory
import com.github.salonkasoli.moviesearchsample.detail.api.MovieDetailNetworkModel
import com.github.salonkasoli.moviesearchsample.detail.api.MovieDetailRepository
import com.github.salonkasoli.moviesearchsample.detail.ui.MovieDetailUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieDetailInteractor(
    private val movieDetailRepository: MovieDetailRepository,
    private val mapperFactory: MovieDetailModelMapperFactory,
    private val lifecycleCoroutineScope: LifecycleCoroutineScope,
    private val cache: MovieDetailCache
) {

    /**
     * Дернется, когда пойдет загрузка фильма.
     * Может и не вызваться, если фильм уже есть в кэше. Тогда сразу вызовется success.
     */
    var loadingListener: (() -> Unit)? = null

    /**
     * Дернется, когда мы успешно загрузим фильм (или достанем его из кэша).
     */
    var successListener: ((movieDetail: MovieDetailUiModel) -> Unit)? = null

    /**
     * Дернется, если не удастся получить фильм из кэша и интернета.
     */
    var errorListener: (() -> Unit)? = null

    private var mapper: MovieDetailModelMapper? = null

    fun loadMovieDetail(id: Int) = lifecycleCoroutineScope.launchWhenResumed {
        cache.get(id)?.let { movieDetail: MovieDetailUiModel ->
            withContext(Dispatchers.Main) {
                successListener?.invoke(movieDetail)
            }
            return@launchWhenResumed
        }
        withContext(Dispatchers.Main) {
            loadingListener?.invoke()
        }

        if (mapper == null) {
            mapper = mapperFactory.createMapper() ?: run {
                withContext(Dispatchers.Main) {
                    errorListener?.invoke()
                }
                return@launchWhenResumed
            }
        }

        val response: RepoResponse<MovieDetailNetworkModel> =
            movieDetailRepository.getMovieDetails(id)
        if (response is RepoError) {
            withContext(Dispatchers.Main) {
                errorListener?.invoke()
            }
        }
        response as RepoSuccess

        val networkMovieDetail: MovieDetailNetworkModel = response.data
        val uiMovieDetail: MovieDetailUiModel = mapper!!.toUiModel(networkMovieDetail)

        cache.put(id, uiMovieDetail)
        withContext(Dispatchers.Main) {
            successListener?.invoke(uiMovieDetail)
        }
    }
}