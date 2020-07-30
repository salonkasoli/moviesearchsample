package com.github.salonkasoli.moviesearchsample.detail

import com.github.salonkasoli.moviesearchsample.detail.ui.MovieDetailUiModel

class MovieDetailController(
    private val widget: MovieDetailWidget,
    private val interactor: MovieDetailInteractor,
    private val movieId: Int
) {

    init {
        interactor.loadingListener = {
            widget.showLoading()
        }
        interactor.errorListener = {
            widget.showError()
        }
        interactor.successListener = { movieDetail: MovieDetailUiModel ->
            widget.showMovieDetail(movieDetail)
        }

        interactor.loadMovieDetail(movieId)
    }

}