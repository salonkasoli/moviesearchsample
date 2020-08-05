package com.github.salonkasoli.moviesearchsample.detail.api

import com.github.salonkasoli.moviesearchsample.configuration.Config
import com.github.salonkasoli.moviesearchsample.configuration.ConfigRepository
import com.github.salonkasoli.moviesearchsample.core.api.RepoError
import com.github.salonkasoli.moviesearchsample.core.api.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovieDetailModelMapperFactory @Inject constructor(
    private val configRepository: ConfigRepository
) {

    /**
     * Создаем маппер из Network модельки в UI.
     * Потенциально придется залезть в интернет т.к. url до фоток нужно формировать хитрым образом,
     *
     * @return Маппер или null, если не удалось получить конфигурацию АПИ.
     */
    suspend fun createMapper(): MovieDetailModelMapper? = withContext(Dispatchers.IO) {
        val configResponse: RepoResponse<Config> = configRepository.getConfig()
        if (configResponse is RepoError) {
            return@withContext null
        }
        configResponse as RepoSuccess

        return@withContext MovieDetailModelMapper(
            configResponse.data
        )
    }
}