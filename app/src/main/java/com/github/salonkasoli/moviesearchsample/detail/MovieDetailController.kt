package com.github.salonkasoli.moviesearchsample.detail

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.github.salonkasoli.moviesearchsample.auth.AuthActivity
import com.github.salonkasoli.moviesearchsample.detail.ui.MovieDetailUiModel
import com.github.salonkasoli.moviesearchsample.rate.RateActivity

class MovieDetailController(
    private val widget: MovieDetailWidget,
    private val interactor: MovieDetailInteractor,
    private val movieId: Int,
    private val context: Context,
    private val lifecycle: Lifecycle
) : LifecycleObserver {

    init {
        widget.authClickListener = {
            context.startActivity(AuthActivity.intent(context))
        }
        widget.rateClickListener = {
            context.startActivity(RateActivity.intent(context, movieId))
        }

        interactor.loadingListener = {
            widget.showLoading()
        }
        interactor.errorListener = {
            widget.showError()
        }
        interactor.successListener = { movieDetail: MovieDetailUiModel ->
            widget.showMovieDetail(movieDetail)
        }

        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        interactor.loadMovieDetail(movieId)
    }
}