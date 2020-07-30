package com.github.salonkasoli.moviesearchsample.detail.api

import com.github.salonkasoli.moviesearchsample.configuration.Config
import com.github.salonkasoli.moviesearchsample.detail.ui.MovieDetailUiModel

class MovieDetailModelMapper(
    private val configuration: Config
) {

    fun toUiModel(networkModel: MovieDetailNetworkModel): MovieDetailUiModel {
        return MovieDetailUiModel(
            networkModel.id,
            networkModel.title,
            networkModel.releaseDate,
            networkModel.genres,
            networkModel.overview,
            getPosterPreviewUrlPrefix() + networkModel.posterPath
        )
    }

    private fun getPosterPreviewUrlPrefix(): String {
        return configuration.images.secureBaseUrl + configuration.images.posterSizes.first()
    }
}