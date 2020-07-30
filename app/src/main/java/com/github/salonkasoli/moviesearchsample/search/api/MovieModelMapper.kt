package com.github.salonkasoli.moviesearchsample.search.api

import com.github.salonkasoli.moviesearchsample.configuration.Config
import com.github.salonkasoli.moviesearchsample.genre.Genre
import com.github.salonkasoli.moviesearchsample.search.ui.MovieUiModel

class MovieModelMapper(
    private val configuration: Config,
    private val genres: List<Genre>
) {

    private val posterPreviewUrlPrefix: String = getPosterPreviewUrlPrefix()
    private val posterUrlPrefix: String = getPosterPreviewUrlPrefix()

    fun toUiModel(movieNetworkModel: MovieNetworkModel): MovieUiModel {
        return MovieUiModel(
            movieNetworkModel.id,
            movieNetworkModel.title,
            movieNetworkModel.releaseDate,
            posterPreviewUrlPrefix + movieNetworkModel.posterPath,
            genres.filter { genre: Genre -> movieNetworkModel.genreIds.contains(genre.id) }
        )
    }

    private fun getPosterPreviewUrlPrefix(): String {
        return configuration.images.secureBaseUrl + configuration.images.posterSizes.first()
    }
}