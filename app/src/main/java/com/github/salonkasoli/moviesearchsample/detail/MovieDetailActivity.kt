package com.github.salonkasoli.moviesearchsample.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.salonkasoli.moviesearchsample.App
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.SessionIdCache
import com.github.salonkasoli.moviesearchsample.configuration.ConfigRepository
import com.github.salonkasoli.moviesearchsample.core.ui.LoadingWidget
import com.github.salonkasoli.moviesearchsample.detail.api.MovieDetailModelMapperFactory
import com.github.salonkasoli.moviesearchsample.detail.api.MovieDetailRepository
import retrofit2.Retrofit

/**
 * Экран, на котором можно посмотреть детальную инфомрацию о фильме.
 */
class MovieDetailActivity : AppCompatActivity(R.layout.activity_movie_detail) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val retrofit: Retrofit = App.get(Retrofit::class.java)
        MovieDetailController(
            MovieDetailWidget(
                LoadingWidget(findViewById(R.id.loading_container)),
                findViewById(R.id.movie_detail_container),
                findViewById(R.id.photo),
                findViewById(R.id.title),
                findViewById(R.id.year),
                findViewById(R.id.genres),
                findViewById(R.id.vote),
                findViewById(R.id.user_vote),
                findViewById(R.id.divider_vote),
                findViewById(R.id.overview),
                findViewById(R.id.overview_divider)
            ),
            MovieDetailInteractor(
                MovieDetailRepository(
                    retrofit,
                    this,
                    App.get(SessionIdCache::class.java)
                ),
                MovieDetailModelMapperFactory(ConfigRepository(retrofit, this)),
                lifecycleScope,
                App.get(MovieDetailCache::class.java)
            ),
            intent.getIntExtra(EXTRA_MOVIE_ID, -1),
            this,
            lifecycle
        )
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = intent.extras?.getString(EXTRA_TITLE)
        }
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