package com.github.salonkasoli.moviesearchsample.genre

import android.content.Context
import com.github.salonkasoli.moviesearchsample.Const
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GenresRepository(
    context: Context,
    private val gson: Gson = Gson()
) {
    // TODO
    // Сделать так, чтобы кэш обновлялся отдельно от получения. Мб на старте приложения?
    private val prefs = context.getSharedPreferences("genre_repo", Context.MODE_PRIVATE)

    private val apiKey = context.getString(R.string.moviedb_api_key)

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Const.MOVIE_DB_URL)
        .build()

    /**
     * @return Конфигурацию АПИ. Она понадобится, например, для формирования урлов к фоткам.
     * Конфигурацию хранится в кэше и обновляется раз в 24 часа.
     */
    suspend fun getMovieGenres(): RepoResponse<GenreResponse> = withContext(Dispatchers.IO) {
        getCachedGenres()?.let { genres: GenreResponse ->
            return@withContext RepoSuccess(genres)
        }

        val result: ExecutionResult<GenreResponse> = retrofit.create(GenresApi::class.java)
            .getMovieGenres(apiKey)
            .executeSafe()

        if (result is ExecutionError) {
            return@withContext RepoError<GenreResponse>(result.exception)
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

        return@withContext RepoSuccess(genres)
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