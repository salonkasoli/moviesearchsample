package com.github.salonkasoli.moviesearchsample.core.api

sealed class RepoResponse<T>

data class RepoSuccess<T>(
    val data: T
) : RepoResponse<T>()

data class RepoError<T>(
    val exception: Exception
) : RepoResponse<T>()