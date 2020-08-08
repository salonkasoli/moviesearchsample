package com.github.salonkasoli.moviesearchsample.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.salonkasoli.moviesearchsample.App
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.AuthActivity
import com.github.salonkasoli.moviesearchsample.core.mvvm.LoadingState
import com.github.salonkasoli.moviesearchsample.detail.ui.MovieDetailUiModel
import com.github.salonkasoli.moviesearchsample.detail.ui.MovieDetailWidget
import com.github.salonkasoli.moviesearchsample.di.MovieDetailComponent
import com.github.salonkasoli.moviesearchsample.di.module.MovieDetailUiModule
import com.github.salonkasoli.moviesearchsample.rate.RateActivity

/**
 * Экран, на котором можно посмотреть детальную инфомрацию о фильме.
 */
class MovieDetailActivity : AppCompatActivity(R.layout.activity_movie_detail) {

    lateinit var viewModel: MovieDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val movieId: Int = intent.getIntExtra(EXTRA_MOVIE_ID, -1)

        val component: MovieDetailComponent = App.instance.appComponent.movieDetailComponent()
            .create(MovieDetailUiModule(this, movieId))

        val widget: MovieDetailWidget = component.widget()
        viewModel = ViewModelProvider(this, component.vmFactory())
            .get(MovieDetailViewModel::class.java)

        widget.authClickListener = {
            startActivity(AuthActivity.intent(this))
        }
        widget.rateClickListener = {
            startActivity(RateActivity.intent(this, movieId))
        }
        widget.retryClickListener = {
            viewModel.updateMovieDetails()
        }

        viewModel.loadingState.observe(this, Observer { loadingState: LoadingState ->
            when (loadingState) {
                LoadingState.WAITING, LoadingState.SUCCESS -> {
                    // Do nothing
                }
                LoadingState.LOADING -> {
                    widget.showLoading()
                }
                LoadingState.ERROR -> {
                    widget.showError()
                }
            }
        })
        viewModel.movieDetail.observe(this, Observer { movieDetail: MovieDetailUiModel? ->
            movieDetail?.let {
                widget.showMovieDetail(it)
            }
        })

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = intent.extras?.getString(EXTRA_TITLE)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateMovieDetails()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private const val EXTRA_MOVIE_ID = "movie_id"
        private const val EXTRA_TITLE = "movie_title"

        fun intent(context: Context, movieId: Int, movieTitle: String): Intent {
            return Intent(context, MovieDetailActivity::class.java)
                .putExtra(EXTRA_MOVIE_ID, movieId)
                .putExtra(EXTRA_TITLE, movieTitle)
        }
    }
}