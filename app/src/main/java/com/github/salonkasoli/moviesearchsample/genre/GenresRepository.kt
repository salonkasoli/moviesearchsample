package com.github.salonkasoli.moviesearchsample.genre

import android.content.Context
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.api.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий, позволяющий получить жанры фильмов (см. [Genre]).
 *
 * Некоторые АПИ возвращают только id жанров. Для того, чтобы мы смогли показзать юзеру жанры
 * фильмов необходимо заблаговременно их запросить.
 */
@Singleton
class GenresRepository @Inject constructor(
    private val retrofit: Retrofit,
    context: Context,
    private val gson: Gson
) {
    // TODO
    // Сделать так, чтобы кэш обновлялся отдельно от получения. Мб на старте приложения?
    private val prefs = context.getSharedPreferences("genre_repo", Context.MODE_PRIVATE)

    private val apiKey = context.getString(R.string.moviedb_api_key)

    suspend fun getMovieGenres(): RepoResponse<GenreResponse> = withContext(Dispatchers.IO) {
        getCachedGenres()?.let { genres: GenreResponse ->
            return@withContext RepoSuccess(
                genres
            )
        }

        val result: ExecutionResult<GenreResponse> = retrofit.create(GenresApi::class.java)
            .getMovieGenres(apiKey)
            .executeSafe()

        if (result is ExecutionError) {
            return@withContext RepoError<GenreResponse>(
                result.exception
            )
        }

        val response: Response<GenreResponse> = (result as ExecutionSuccess).response

        if (!response.isSuccessful || response.body() == null) {
            return@withContext RepoError<GenreResponse>(
                IllegalStateException("response = $response, body = ${response.body()}")
            )
        }

        val genres: GenreResponse = response.body()!!
        prefs.edit()
            .putString(PREF_CACHE, gson.toJson(genres))
            .putLong(PREF_LAST_UPDATE_TIME, System.currentTimeMillis())
            .apply()

        return@withContext RepoSuccess(
            genres
        )
    }

    private fun getCachedGenres(): GenreResponse? {
        val lastUpdateTime: Long = prefs.getLong(PREF_LAST_UPDATE_TIME, 0L)
        val isCacheActual: Boolean = if (lastUpdateTime > 0) {
            System.currentTimeMillis() - lastUpdateTime < CACHE_TTL
        } else {
            false
        }

        if (!isCacheActual || !prefs.contains(PREF_CACHE)) {
            return null
        }

        return gson.fromJson(
            prefs.getString(PREF_CACHE, null)!!,
            GenreResponse::class.java
        )
    }

    companion object {
        private const val PREF_CACHE = "genres"
        private const val PREF_LAST_UPDATE_TIME = "last_update_time"

        private val CACHE_TTL: Long = TimeUnit.DAYS.toMillis(1)
    }
}