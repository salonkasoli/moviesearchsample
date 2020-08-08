package com.github.salonkasoli.moviesearchsample.search.api

import androidx.annotation.WorkerThread
import com.github.salonkasoli.moviesearchsample.configuration.Config
import com.github.salonkasoli.moviesearchsample.configuration.ConfigRepository
import com.github.salonkasoli.moviesearchsample.genre.GenreResponse
import com.github.salonkasoli.moviesearchsample.genre.GenresRepository
import javax.inject.Inject


class MovieSearchMapperFactory @Inject constructor(
    private val configRepository: ConfigRepository,
    private val genresRepository: GenresRepository
) {

    /**
     * Создаем маппер из Network модельки в UI.
     * Потенциально придется залезть в интернет т.к. url до фоток нужно формировать хитрым образом,
     * а также чтобы смапить жанры нужно дополнительное АПИ.
     *
     * @return Маппер или null, если не удалось получить конфигурацию АПИ.
     */
    @Throws(Exception::class)
    @WorkerThread
    fun createMapper(): MovieModelMapper {
        val config: Config = configRepository.getConfig()
        val genresResponse: GenreResponse = genresRepository.getMovieGenres()

        return MovieModelMapper(config, genresResponse.genres)
    }
}