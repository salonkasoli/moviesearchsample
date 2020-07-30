package com.github.salonkasoli.moviesearchsample.detail

import android.util.LruCache
import com.github.salonkasoli.moviesearchsample.detail.ui.MovieDetailUiModel

class MovieDetailCache {

    private val cache = LruCache<Int, MovieDetailUiModel>(5)

    fun get(id: Int): MovieDetailUiModel? {
        return cache.get(id)
    }

    fun put(id: Int, movieDetail: MovieDetailUiModel) {
        cache.put(id, movieDetail)
    }
}