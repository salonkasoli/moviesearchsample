package com.github.salonkasoli.moviesearchsample.search.ui

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.salonkasoli.moviesearchsample.core.rv.PaginationController

class MoviesListWidget(
    list: RecyclerView
) {

    val paginationController = PaginationController(list)

    var errorClickListener: (() -> Unit)? = null

    private val adapter = MoviesListAdapter()

    init {
        val layoutManager = LinearLayoutManager(list.context)
        list.layoutManager = layoutManager
        list.adapter = adapter
        adapter.errorClickListener = {
            errorClickListener?.invoke()
        }
    }

    fun showMovies(movies: List<MovieUiModel>) {
        adapter.movies.clear()
        adapter.movies.addAll(movies)
        adapter.loadingState = MoviesListAdapter.LoadingState.GONE
        paginationController.isLoading = false
        adapter.notifyDataSetChanged()
    }

    fun showLoading(movies: List<MovieUiModel>) {
        Log.wtf("lol", "showing loading")
        adapter.movies.clear()
        adapter.movies.addAll(movies)
        adapter.loadingState = MoviesListAdapter.LoadingState.LOADING
        paginationController.isLoading = false
        adapter.notifyDataSetChanged()
    }

    fun showError(movies: List<MovieUiModel>) {
        Log.wtf("lol", "showing error")
        adapter.movies.clear()
        adapter.movies.addAll(movies)
        adapter.loadingState = MoviesListAdapter.LoadingState.ERROR
        paginationController.isLoading = false
        adapter.notifyDataSetChanged()
    }
}