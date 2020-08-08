package com.github.salonkasoli.moviesearchsample.di.module

import androidx.savedstate.SavedStateRegistry
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.AuthActivity
import com.github.salonkasoli.moviesearchsample.auth.ui.AuthWidget
import dagger.Module
import dagger.Provides

@Module
class AuthUiModule(
    private val activity: AuthActivity
) {

    @Provides
    fun savedStateRegistry(): SavedStateRegistry {
        return activity.savedStateRegistry
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
}