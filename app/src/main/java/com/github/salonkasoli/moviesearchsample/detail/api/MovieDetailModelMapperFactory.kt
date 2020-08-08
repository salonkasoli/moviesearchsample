package com.github.salonkasoli.moviesearchsample.detail.api

import androidx.annotation.WorkerThread
import com.github.salonkasoli.moviesearchsample.configuration.Config
import com.github.salonkasoli.moviesearchsample.configuration.ConfigRepository
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
    @Throws
    @WorkerThread
    fun createMapper(): MovieDetailModelMapper {
        val config: Config = configRepository.getConfig()
        return MovieDetailModelMapper(config)
    }
}