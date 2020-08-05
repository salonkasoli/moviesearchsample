package com.github.salonkasoli.moviesearchsample.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.salonkasoli.moviesearchsample.App
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.di.module.MovieDetailUiModule

/**
 * Экран, на котором можно посмотреть детальную инфомрацию о фильме.
 */
class MovieDetailActivity : AppCompatActivity(R.layout.activity_movie_detail) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val movieId: Int = intent.getIntExtra(EXTRA_MOVIE_ID, -1)
        App.instance.appComponent.movieDetailComponent()
            .create(MovieDetailUiModule(this, movieId))
            .controller()

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