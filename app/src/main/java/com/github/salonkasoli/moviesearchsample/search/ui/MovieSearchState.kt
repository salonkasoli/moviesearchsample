package com.github.salonkasoli.moviesearchsample.search.ui

data class MovieSearchState(
    val lastLoadedPage: Int,
    val totalPages: Int,
    val movies: List<MovieUiModel>
) {

    fun isFullyLoaded() : Boolean {
        return lastLoadedPage == totalPages
    }
}