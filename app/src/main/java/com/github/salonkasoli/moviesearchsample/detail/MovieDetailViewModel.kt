package com.github.salonkasoli.moviesearchsample.detail

import androidx.lifecycle.*
import com.github.salonkasoli.moviesearchsample.core.api.RepoError
import com.github.salonkasoli.moviesearchsample.core.api.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoSuccess
import com.github.salonkasoli.moviesearchsample.core.mvvm.LoadingState
import com.github.salonkasoli.moviesearchsample.detail.api.MovieDetailModelMapper
import com.github.salonkasoli.moviesearchsample.detail.api.MovieDetailModelMapperFactory
import com.github.salonkasoli.moviesearchsample.detail.api.MovieDetailNetworkModel
import com.github.salonkasoli.moviesearchsample.detail.api.MovieDetailRepository
import com.github.salonkasoli.moviesearchsample.detail.ui.MovieDetailUiModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailViewModel(
    private val movieId: Int,
    private val cache: MovieDetailCache,
    private val mapperFactory: MovieDetailModelMapperFactory,
    private val movieDetailRepository: MovieDetailRepository
) : ViewModel() {

    val loadingState: LiveData<LoadingState>
        get() = _loadingState
    val movieDetail: LiveData<MovieDetailUiModel?>
        get() = _movieDetail

    private val _movieDetail = MutableLiveData<MovieDetailUiModel?>()
    private val _loadingState = MutableLiveData<LoadingState>()

    private var mapper: MovieDetailModelMapper? = null

    fun updateMovieDetails() = viewModelScope.launch {
        cache.get(movieId)?.let { movieDetail: MovieDetailUiModel ->
            _movieDetail.postValue(movieDetail)
            _loadingState.postValue(LoadingState.SUCCESS)
            return@launch
        }

        _loadingState.postValue(LoadingState.LOADING)

        if (mapper == null) {
            mapper = mapperFactory.createMapper() ?: run {
                _loadingState.postValue(LoadingState.ERROR)
                return@launch
            }
        }

        val response: RepoResponse<MovieDetailNetworkModel> =
            movieDetailRepository.getMovieDetails(movieId)
        if (response is RepoError) {
            _loadingState.postValue(LoadingState.ERROR)
            return@launch
        }
        response as RepoSuccess

        val networkMovieDetail: MovieDetailNetworkModel = response.data
        val uiMovieDetail: MovieDetailUiModel = mapper!!.toUiModel(networkMovieDetail)

        cache.put(movieId, uiMovieDetail)
        _movieDetail.postValue(uiMovieDetail)
        _loadingState.postValue(LoadingState.SUCCESS)
    }

    class Factory @Inject constructor(
        private val movieId: Int,
        private val movieDetailCache: MovieDetailCache,
        private val movieDetailModelMapperFactory: MovieDetailModelMapperFactory,
        private val movieDetailRepository: MovieDetailRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MovieDetailViewModel(
                movieId,
                movieDetailCache,
                movieDetailModelMapperFactory,
                movieDetailRepository
            ) as T
        }
    }
}