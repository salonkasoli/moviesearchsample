package com.github.salonkasoli.moviesearchsample.search.api

import com.github.salonkasoli.moviesearchsample.configuration.Config
import com.github.salonkasoli.moviesearchsample.configuration.ConfigRepository
import com.github.salonkasoli.moviesearchsample.core.api.RepoError
import com.github.salonkasoli.moviesearchsample.core.api.RepoResponse
import com.github.salonkasoli.moviesearchsample.core.api.RepoSuccess
import com.github.salonkasoli.moviesearchsample.genre.GenreResponse
import com.github.salonkasoli.moviesearchsample.genre.GenresRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieSearchMapperFactory(
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
    suspend fun createMapper(): MovieModelMapper? = withContext(Dispatchers.IO) {
        val configResponse: RepoResponse<Config> = configRepository.getConfig()
        if (configResponse is RepoError) {
            return@withContext null
        }
        configResponse as RepoSuccess

        val genresResponse: RepoResponse<GenreResponse> = genresRepository.getMovieGenres()
        if (genresResponse is RepoError) {
            return@withContext null
        }
        genresResponse as RepoSuccess

        return@withContext MovieModelMapper(
            configResponse.data,
            genresResponse.data.genres
        )
    }
}