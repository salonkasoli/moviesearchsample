package com.github.salonkasoli.moviesearchsample.search.ui

import com.github.salonkasoli.moviesearchsample.genre.Genre
import java.io.Serializable

data class MovieUiModel(
    val id: Int,
    val title: String,
    val releaseDate: String?,
    val posterPreviewUrl: String?,
    val genres: List<Genre>
) : Serializable