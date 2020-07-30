package com.github.salonkasoli.moviesearchsample.detail.ui

import com.github.salonkasoli.moviesearchsample.genre.Genre

data class MovieDetailUiModel(
    val id: Int,
    val title: String,
    val year: String,
    val genres: List<Genre>,
    val overview: String?,
    val posterUrl: String?
)