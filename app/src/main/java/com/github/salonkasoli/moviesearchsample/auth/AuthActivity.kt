package com.github.salonkasoli.moviesearchsample.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.session.SessionInteractor
import com.github.salonkasoli.moviesearchsample.auth.session.SessionRepository
import com.github.salonkasoli.moviesearchsample.auth.token.authed.AuthedTokenRepository
import com.github.salonkasoli.moviesearchsample.auth.token.newly.NewTokenRepository
import com.github.salonkasoli.moviesearchsample.auth.ui.AuthWidget

class AuthActivity : AppCompatActivity(R.layout.activity_auth) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthController(
            AuthWidget(
                findViewById(R.id.credentials_container),
                findViewById(R.id.login),
                findViewById(R.id.password),
                findViewById(R.id.login_button),
                findViewById(R.id.progress_bar)
            ),
            SessionInteractor(
                NewTokenRepository(this),
                AuthedTokenRepository(this),
                SessionRepository(this),
                lifecycleScope
            )
        )
    }

    companion object {

        fun intent(context: Context): Intent {
            return Intent(context, AuthActivity::class.java)
        }
    }
}