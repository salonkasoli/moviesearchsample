package com.github.salonkasoli.moviesearchsample.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.salonkasoli.moviesearchsample.App
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.configuration.ConfigRepository
import com.github.salonkasoli.moviesearchsample.core.ui.LoadingWidget
import com.github.salonkasoli.moviesearchsample.detail.api.MovieDetailModelMapperFactory
import com.github.salonkasoli.moviesearchsample.detail.api.MovieDetailRepository

class MovieDetailActivity : AppCompatActivity(R.layout.activity_movie_detail) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MovieDetailController(
            MovieDetailWidget(
                LoadingWidget(findViewById(R.id.loading_container)),
                findViewById(R.id.movie_detail_container),
                findViewById(R.id.photo),
                findViewById(R.id.title),
                findViewById(R.id.year),
                findViewById(R.id.genres),
                findViewById(R.id.vote),
                findViewById(R.id.divider_vote),
                findViewById(R.id.overview),
                findViewById(R.id.overview_divider)
            ),
            MovieDetailInteractor(
                MovieDetailRepository(this),
                MovieDetailModelMapperFactory(ConfigRepository(this)),
                lifecycleScope,
                App.get(MovieDetailCache::class.java)
            ),
            intent.getIntExtra(EXTRA_MOVIE_ID, -1)
        )
    }

    companion object {

        private const val EXTRA_MOVIE_ID = "movie_id"

        fun intent(context: Context, movieId: Int): Intent {
            return Intent(context, MovieDetailActivity::class.java)
                .putExtra(EXTRA_MOVIE_ID, movieId)
        }
    }
}