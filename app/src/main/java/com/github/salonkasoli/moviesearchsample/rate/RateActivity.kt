package com.github.salonkasoli.moviesearchsample.rate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.salonkasoli.moviesearchsample.App
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.di.module.RateUiModule

/**
 * Экран, на котором можно оценить фильм.
 */
class RateActivity : AppCompatActivity(R.layout.activity_rate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val movieId: Int = intent.getIntExtra(EXTRA_MOVIE_ID, -1)
        App.instance.appComponent.rateComponent()
            .create(RateUiModule(this, movieId))
            .controller()
    }

    companion object {

        private const val EXTRA_MOVIE_ID = "movie_id"

        fun intent(context: Context, movieId: Int): Intent {
            return Intent(context, RateActivity::class.java)
                .putExtra(EXTRA_MOVIE_ID, movieId)
        }
    }
}