package com.github.salonkasoli.moviesearchsample.auth

import android.app.Activity
import com.github.salonkasoli.moviesearchsample.auth.session.SessionInteractor
import com.github.salonkasoli.moviesearchsample.auth.ui.AuthWidget
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailCache

class AuthController constructor(
    private val authWidget: AuthWidget,
    private val sessionInteractor: SessionInteractor,
    private val movieDetailCache: MovieDetailCache,
    private val activity: Activity
) {

    init {
        sessionInteractor.successListener = {
            movieDetailCache.clear()
            activity.finish()
        }
        sessionInteractor.errorListener = {
            authWidget.showError()
        }

        authWidget.authClickListener = { login: String, password: String ->
            authWidget.showLoading()
            sessionInteractor.createSessionId(login, password)
        }
    }
}