package com.github.salonkasoli.moviesearchsample.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.salonkasoli.moviesearchsample.App
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.auth.ui.AuthWidget
import com.github.salonkasoli.moviesearchsample.core.mvvm.LoadingState
import com.github.salonkasoli.moviesearchsample.core.mvvm.SimpleEvent
import com.github.salonkasoli.moviesearchsample.di.AuthComponent
import com.github.salonkasoli.moviesearchsample.di.module.AuthUiModule

/**
 * Экран, на котором юзер может авторизоваться.
 */
class AuthActivity : AppCompatActivity(R.layout.activity_auth) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authComponent: AuthComponent = App.instance.appComponent
            .authComponent()
            .create(AuthUiModule(this))

        val widget: AuthWidget = authComponent.widget()
        val viewModel: AuthViewModel = ViewModelProvider(this, authComponent.vmFactory())
            .get(AuthViewModel::class.java)

        widget.authClickListener = { login: String, password: String ->
            viewModel.createSessionId(login, password)
        }
        viewModel.loadingState.observe(this, Observer { loadingState: LoadingState ->
            when (loadingState) {
                LoadingState.WAITING -> {
                    widget.show()
                }
                LoadingState.LOADING -> {
                    widget.showLoading()
                }
                LoadingState.SUCCESS -> {
                    finish()
                }
                LoadingState.ERROR -> {
                    throw IllegalStateException("Unexpected loading state $loadingState")
                }
            }
        })
        viewModel.errorEvent.observe(this, Observer { event: SimpleEvent ->
            event.handle()?.let {
                widget.showError()
            }
        })

        initToolbar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Авторизация"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {

        fun intent(context: Context): Intent {
            return Intent(context, AuthActivity::class.java)
        }
    }
}