package com.github.salonkasoli.moviesearchsample.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.salonkasoli.moviesearchsample.App
import com.github.salonkasoli.moviesearchsample.R
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

        authComponent.controller()

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