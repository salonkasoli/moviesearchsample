package com.github.salonkasoli.moviesearchsample.detail.api

import com.github.salonkasoli.moviesearchsample.configuration.Config
import com.github.salonkasoli.moviesearchsample.detail.ui.MovieDetailUiModel

/**
 * Маппит [MovieDetailNetworkModel] в [MovieDetailUiModel].
 */
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
            getPosterPreviewUrlPrefix() + networkModel.posterPath,
            networkModel.voteAverage,
            networkModel.voteCount,
            mapUserRate(networkModel.accountStates)
        )
    }

    private fun mapUserRate(accountStates: Map<String, Any?>?): Float {
        if (accountStates == null) {
            return MovieDetailUiModel.USER_VOTE_NO_AUTH
        }
        val rated: Any? = accountStates["rated"]
        if (false == rated || "false" == rated) {
            return MovieDetailUiModel.USER_VOTE_EMPTY
        }
        try {
            return (rated as Map<*, *>).get("value").toString().toFloat()
        } catch (e: Exception) {
            return MovieDetailUiModel.USER_VOTE_ERROR
        }
    }

    private fun getPosterPreviewUrlPrefix(): String {
        return configuration.images.secureBaseUrl + configuration.images.posterSizes.first()
    }
}