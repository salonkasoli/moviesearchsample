package com.github.salonkasoli.moviesearchsample.core.ui

import android.view.View
import android.view.ViewGroup
import com.github.salonkasoli.moviesearchsample.R

class LoadingWidget(
    private val loadingContainer: ViewGroup,
    private val progressBar: View,
    private val errorText: View,
    private val retryButton: View
) {

    constructor(container: ViewGroup) : this(
        container.findViewById(R.id.loading_container),
        container.findViewById(R.id.progress_bar),
        container.findViewById(R.id.status_text),
        container.findViewById(R.id.retry_button)
    )

    var retryClickListener: (() -> Unit)? = null

    init {
        retryButton.setOnClickListener {
            retryClickListener?.invoke()
        }
    }

    fun showLoading() {
        loadingContainer.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        errorText.visibility = View.GONE
        retryButton.visibility = View.GONE
    }

    fun showError() {
        loadingContainer.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        errorText.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE
    }

    fun hide() {
        loadingContainer.visibility = View.GONE
    }
}