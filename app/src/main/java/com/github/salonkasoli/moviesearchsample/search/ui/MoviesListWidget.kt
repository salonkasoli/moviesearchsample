package com.github.salonkasoli.moviesearchsample.search.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.salonkasoli.moviesearchsample.core.rv.PaginationController

class MoviesListWidget(
    private val list: RecyclerView,
    private val loadingStatusContainer: View,
    private val progressBar: View,
    private val emptyText: View,
    private val emptyImage: View,
    private val errorImage: View
) {

    val paginationController = PaginationController(list)

    var movieClickListener: ((MovieUiModel) -> Unit)? = null
    var errorClickListener: (() -> Unit)? = null

    private val adapter = MoviesListAdapter()

    init {
        val layoutManager = LinearLayoutManager(list.context)
        list.layoutManager = layoutManager
        list.adapter = adapter
        adapter.errorClickListener = {
            errorClickListener?.invoke()
        }
        adapter.clickListener = { movieUiModel: MovieUiModel ->
            movieClickListener?.invoke(movieUiModel)
        }
    }

    fun showMovies(movies: List<MovieUiModel>) {
        adapter.movies.clear()
        adapter.movies.addAll(movies)
        adapter.loadingState = MoviesListAdapter.LoadingState.GONE
        paginationController.isLoading = false
        adapter.notifyDataSetChanged()

        if (movies.isNotEmpty()) {
            list.visibility = View.VISIBLE
            loadingStatusContainer.visibility = View.GONE
        } else {
            list.visibility = View.GONE
            loadingStatusContainer.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
            emptyImage.visibility = View.VISIBLE
            errorImage.visibility = View.GONE
        }
    }

    fun showLoading(movies: List<MovieUiModel>) {
        adapter.movies.clear()
        adapter.movies.addAll(movies)
        adapter.loadingState = if (movies.isNotEmpty()) {
            MoviesListAdapter.LoadingState.LOADING
        } else {
            MoviesListAdapter.LoadingState.GONE
        }
        paginationController.isLoading = true
        adapter.notifyDataSetChanged()

        if (movies.isNotEmpty()) {
            list.visibility = View.VISIBLE
            loadingStatusContainer.visibility = View.GONE
        } else {
            list.visibility = View.GONE
            loadingStatusContainer.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
            emptyImage.visibility = View.GONE
            errorImage.visibility = View.GONE
        }
    }

    fun showError(movies: List<MovieUiModel>) {
        adapter.movies.clear()
        adapter.movies.addAll(movies)
        adapter.loadingState = if (movies.isNotEmpty()) {
            MoviesListAdapter.LoadingState.ERROR
        } else {
            MoviesListAdapter.LoadingState.GONE
        }
        paginationController.isLoading = true
        adapter.notifyDataSetChanged()

        if (movies.isNotEmpty()) {
            list.visibility = View.VISIBLE
            loadingStatusContainer.visibility = View.GONE
        } else {
            list.visibility = View.GONE
            loadingStatusContainer.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
            emptyImage.visibility = View.GONE
            errorImage.visibility = View.VISIBLE
        }
    }
}