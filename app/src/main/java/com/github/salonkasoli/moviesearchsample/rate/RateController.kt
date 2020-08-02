package com.github.salonkasoli.moviesearchsample.rate

import androidx.appcompat.app.AppCompatActivity
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailCache
import com.github.salonkasoli.moviesearchsample.rate.api.RateInteractor

class RateController(
    private val rateWidget: RateWidget,
    private val rateInteractor: RateInteractor,
    private val movieDetailCache: MovieDetailCache,
    private val movieId: Int,
    private val activity: AppCompatActivity
) {

    init {
        rateWidget.saveListener = { rate: Float ->
            rateWidget.showLoading()
            rateInteractor.rateMovie(movieId, rate)
        }

        rateInteractor.successListener = {
            movieDetailCache.clear()
            activity.finish()
        }
        rateInteractor.errorListener = {
            rateWidget.showError()
        }
    }
}