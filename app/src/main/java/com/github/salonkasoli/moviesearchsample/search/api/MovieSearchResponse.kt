package com.github.salonkasoli.moviesearchsample.search.api

import com.google.gson.annotations.SerializedName

data class MovieSearchResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("total_results")
    val totalResults: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("results")
    val result: List<MovieNetworkModel>
)

data class MovieNetworkModel(
    @SerializedName("popularity")
    val popularity: Float,
    @SerializedName("id")
    val id: Int,
    @SerializedName("video")
    val video: Boolean,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("vote_average")
    val voteAverage: Float,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("title")
    val title: String,
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("poster_path")
    val posterPath: String?
)