package com.github.salonkasoli.moviesearchsample.rate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.salonkasoli.moviesearchsample.App
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.SessionIdCache
import com.github.salonkasoli.moviesearchsample.core.ui.LoadingWidget
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailCache
import com.github.salonkasoli.moviesearchsample.rate.api.RateInteractor
import com.github.salonkasoli.moviesearchsample.rate.api.RateRepository
import retrofit2.Retrofit

class RateActivity : AppCompatActivity(R.layout.activity_rate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RateController(
            RateWidget(
                findViewById(R.id.seek_bar_container),
                findViewById(R.id.rate_text),
                findViewById(R.id.seek_bar),
                findViewById(R.id.save_button),
                LoadingWidget(findViewById(R.id.loading_container)),
                savedStateRegistry
            ),
            RateInteractor(
                RateRepository(
                    App.get(Retrofit::class.java),
                    this,
                    App.get(SessionIdCache::class.java)
                ),
                lifecycleScope
            ),
            App.get(MovieDetailCache::class.java),
            intent.getIntExtra(EXTRA_MOVIE_ID, -1),
            this
        )
    }

    companion object {

        private const val EXTRA_MOVIE_ID = "movie_id"

        fun intent(context: Context, movieId: Int): Intent {
            return Intent(context, RateActivity::class.java)
                .putExtra(EXTRA_MOVIE_ID, movieId)
        }
    }
}