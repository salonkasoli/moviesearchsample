package com.github.salonkasoli.moviesearchsample.search.ui

data class MovieSearchState(
    var lastLoadedPage: Int,
    var totalPages: Int,
    var movies: MutableList<MovieUiModel>
) {

    fun isFullyLoaded() : Boolean {
        return lastLoadedPage == totalPages
    }
}