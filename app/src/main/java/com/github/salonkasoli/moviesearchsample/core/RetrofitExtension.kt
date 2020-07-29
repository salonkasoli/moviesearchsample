package com.github.salonkasoli.moviesearchsample.core

import retrofit2.Call
import retrofit2.Response

sealed class ExecutionResult<T>

data class ExecutionSuccess<T>(
    val response: Response<T>
) : ExecutionResult<T>()

data class ExecutionError<T>(
    val exception: Exception
) : ExecutionResult<T>()

fun <T> Call<T>.executeSafe(): ExecutionResult<T> {
    return try {
        ExecutionSuccess(execute())
    } catch (e: Exception) {
        ExecutionError(e)
    }
}