package com.github.salonkasoli.moviesearchsample.search.api

import com.github.salonkasoli.moviesearchsample.configuration.Config
import com.github.salonkasoli.moviesearchsample.search.ui.MovieUiModel

class MovieSearchMapper(
    private val configuration: Config
) {

    private val posterPreviewUrlPrefix: String = getPosterPreviewUrlPrefix()
    private val posterUrlPrefix: String = getPosterPreviewUrlPrefix()

    fun toUiModel(movieNetworkModel: MovieNetworkModel): MovieUiModel {
        return MovieUiModel(
            movieNetworkModel.id,
            movieNetworkModel.title,
            movieNetworkModel.releaseDate,
            posterPreviewUrlPrefix + movieNetworkModel.posterPath
        )
    }

    private fun getPosterPreviewUrlPrefix(): String {
        return configuration.images.secureBaseUrl + configuration.images.posterSizes.first()
    }
}