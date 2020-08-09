package com.github.salonkasoli.moviesearchsample.search.ui

import com.github.salonkasoli.moviesearchsample.core.mvvm.LoadingState
import java.io.Serializable

data class MovieSearchUiState(
    val movies: List<MovieUiModel>,
    val loadingState: LoadingState
) : Serializable