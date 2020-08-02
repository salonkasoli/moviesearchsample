package com.github.salonkasoli.moviesearchsample.configuration

import android.content.Context
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.api.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ConfigRepository(
    private val retrofit: Retrofit,
    context: Context,
    private val gson: Gson = Gson()
) {

    // TODO
    // Сделать так, чтобы кэш обновлялся отдельно от получения. Мб на старте приложения?
    private val prefs = context.getSharedPreferences("config_repo", Context.MODE_PRIVATE)

    private val apiKey = context.getString(R.string.moviedb_api_key)

    /**
     * @return Конфигурацию АПИ. Она понадобится, например, для формирования урлов к фоткам.
     * Конфигурацию хранится в кэше и обновляется раз в 24 часа.
     */
    suspend fun getConfig(): RepoResponse<Config> = withContext(Dispatchers.IO) {
        getCachedConfig()?.let { config: Config ->
            return@withContext RepoSuccess(
                config
            )
        }

        val result: ExecutionResult<Config> = retrofit.create(ConfigApi::class.java)
            .getConfiguration(apiKey)
            .executeSafe()

        if (result is ExecutionError) {
            return@withContext RepoError<Config>(
                result.exception
            )
        }

        val response: Response<Config> = (result as ExecutionSuccess).response

        if (!response.isSuccessful || response.body() == null) {
            return@withContext RepoError<Config>(
                IllegalStateException("response = $response, body = ${response.body()}")
            )
        }

        val config: Config = response.body()!!
        prefs.edit()
            .putString(PREF_CACHE, gson.toJson(config))
            .putLong(PREF_LAST_UPDATE_TIME, System.currentTimeMillis())
            .apply()

        return@withContext RepoSuccess(
            config
        )
    }

    private fun getCachedConfig(): Config? {
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
            Config::class.java
        )
    }

    companion object {
        private const val PREF_CACHE = "config"
        private const val PREF_LAST_UPDATE_TIME = "last_update_time"

        private val CACHE_TTL: Long = TimeUnit.DAYS.toMillis(1)
    }
}