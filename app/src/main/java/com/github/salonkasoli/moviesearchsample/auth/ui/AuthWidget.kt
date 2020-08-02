package com.github.salonkasoli.moviesearchsample.auth.ui

import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

class AuthWidget(
    private val container: ViewGroup,
    private val loginEditText: EditText,
    private val passwordEditText: EditText,
    private val submitButton: Button,
    private val progressBar: ProgressBar
) {

    var authClickListener: ((login: String, password: String) -> Unit)? = null

    init {
        submitButton.setOnClickListener {
            authClickListener?.invoke(
                loginEditText.editableText.toString(),
                passwordEditText.editableText.toString()
            )
        }

        passwordEditText.setOnEditorActionListener { v, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                authClickListener?.invoke(
                    loginEditText.editableText.toString(),
                    passwordEditText.editableText.toString()
                )
            }
            return@setOnEditorActionListener false
        }
    }

    fun showLoading() {
        container.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    fun showError() {
        container.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        Toast.makeText(container.context, "Ошибка", Toast.LENGTH_SHORT).show()
    }
}