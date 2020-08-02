package com.github.salonkasoli.moviesearchsample.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.salonkasoli.moviesearchsample.App
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.session.SessionInteractor
import com.github.salonkasoli.moviesearchsample.auth.session.SessionRepository
import com.github.salonkasoli.moviesearchsample.auth.token.authed.AuthedTokenRepository
import com.github.salonkasoli.moviesearchsample.auth.token.newly.NewTokenRepository
import com.github.salonkasoli.moviesearchsample.auth.ui.AuthWidget
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailCache
import retrofit2.Retrofit

class AuthActivity : AppCompatActivity(R.layout.activity_auth) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val retrofit: Retrofit = App.get(Retrofit::class.java)
        AuthController(
            AuthWidget(
                findViewById(R.id.credentials_container),
                findViewById(R.id.login),
                findViewById(R.id.password),
                findViewById(R.id.login_button),
                findViewById(R.id.progress_bar)
            ),
            SessionInteractor(
                NewTokenRepository(retrofit, this),
                AuthedTokenRepository(retrofit, this),
                SessionRepository(retrofit, this),
                App.get(SessionIdCache::class.java),
                lifecycleScope
            ),
            App.get(MovieDetailCache::class.java),
            this
        )
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Авторизация"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        fun intent(context: Context): Intent {
            return Intent(context, AuthActivity::class.java)
        }
    }
}