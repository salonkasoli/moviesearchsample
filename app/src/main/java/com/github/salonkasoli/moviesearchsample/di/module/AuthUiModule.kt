package com.github.salonkasoli.moviesearchsample.di.module

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.AuthActivity
import com.github.salonkasoli.moviesearchsample.auth.AuthController
import com.github.salonkasoli.moviesearchsample.auth.session.SessionInteractor
import com.github.salonkasoli.moviesearchsample.auth.ui.AuthWidget
import com.github.salonkasoli.moviesearchsample.detail.MovieDetailCache
import dagger.Module
import dagger.Provides

@Module
class AuthUiModule(
    private val activity: AuthActivity
) {

    @Provides
    fun controller(
        widget: AuthWidget,
        interactor: SessionInteractor,
        movieDetailCache: MovieDetailCache
    ): AuthController {
        return AuthController(
            widget,
            interactor,
            movieDetailCache,
            activity
        )
    }

    @Provides
    fun widget(): AuthWidget {
        return AuthWidget(
            activity.findViewById(R.id.credentials_container),
            activity.findViewById(R.id.login),
            activity.findViewById(R.id.password),
            activity.findViewById(R.id.login_button),
            activity.findViewById(R.id.progress_bar)
        )
    }

    @Provides
    fun lifecycleScope(): LifecycleCoroutineScope {
        return activity.lifecycleScope
    }
}