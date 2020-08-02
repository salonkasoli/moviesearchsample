package com.github.salonkasoli.moviesearchsample.detail.ui

import com.github.salonkasoli.moviesearchsample.genre.Genre

data class MovieDetailUiModel(
    val id: Int,
    val title: String,
    val year: String,
    val genres: List<Genre>,
    val overview: String?,
    val posterUrl: String?,
    val averageVote: Float,
    val voteCount: Int,
    val userVote: Float
) {

    companion object {
        const val USER_VOTE_ERROR = -3f
        const val USER_VOTE_NO_AUTH = -2f
        const val USER_VOTE_EMPTY = -1f
    }
}