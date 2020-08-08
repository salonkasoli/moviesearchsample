package com.github.salonkasoli.moviesearchsample.rate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.salonkasoli.moviesearchsample.App
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.mvvm.LoadingState
import com.github.salonkasoli.moviesearchsample.core.mvvm.SimpleEvent
import com.github.salonkasoli.moviesearchsample.di.module.RateUiModule

/**
 * Экран, на котором можно оценить фильм.
 */
class RateActivity : AppCompatActivity(R.layout.activity_rate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val movieId: Int = intent.getIntExtra(EXTRA_MOVIE_ID, -1)

        val rateComponent = App.instance.appComponent.rateComponent()
            .create(RateUiModule(this))

        val widget: RateWidget = rateComponent.widget()
        val viewModel: RateViewModel = ViewModelProvider(this, rateComponent.vmFactory())
            .get(RateViewModel::class.java)

        widget.saveListener = { rate: Float ->
            viewModel.rateMovie(movieId, rate)
        }

        viewModel.state.observe(this, Observer { state: LoadingState ->
            when (state) {
                LoadingState.WAITING, LoadingState.ERROR -> {
                    widget.show()
                }
                LoadingState.LOADING -> {
                    widget.showLoading()
                }
                LoadingState.SUCCESS -> {
                    finish()
                }
            }
        })
        viewModel.errorEvent.observe(this, Observer { event: SimpleEvent ->
            event.handle()?.let {
                widget.showError()
            }
        })
    }

    companion object {

        private const val EXTRA_MOVIE_ID = "movie_id"

        fun intent(context: Context, movieId: Int): Intent {
            return Intent(context, RateActivity::class.java)
                .putExtra(EXTRA_MOVIE_ID, movieId)
        }
    }
}