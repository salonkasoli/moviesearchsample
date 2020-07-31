package com.github.salonkasoli.moviesearchsample.auth

import android.util.Log
import com.github.salonkasoli.moviesearchsample.auth.session.SessionInteractor
import com.github.salonkasoli.moviesearchsample.auth.ui.AuthWidget

class AuthController(
    private val authWidget: AuthWidget,
    private val sessionInteractor: SessionInteractor
) {

    init {
        sessionInteractor.successListener = { sessionId: String ->
            // TODO добавить нормальную обработку
            Log.wtf("lol", "sessionId created! $sessionId")
        }
        sessionInteractor.errorListener = {
            authWidget.showError()
        }

        authWidget.authClickListener = { login: String, password: String ->
            authWidget.showLoading()
            sessionInteractor.getSessionId(login, password)
        }
    }
}