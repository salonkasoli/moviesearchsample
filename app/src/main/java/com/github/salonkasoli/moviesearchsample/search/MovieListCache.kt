package com.github.salonkasoli.moviesearchsample.search

import android.util.LruCache
import com.github.salonkasoli.moviesearchsample.search.ui.MovieSearchCache
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieListCache @Inject constructor() {

    private val lruCache = LruCache<String, MovieSearchCache>(10)

    fun put(query: String, state: MovieSearchCache) {
        lruCache.put(query, state)
    }

    fun get(query: String): MovieSearchCache {
        val state: MovieSearchCache? = lruCache.get(query)
        if (state != null) {
            return state
        }

        val newState = MovieSearchCache(0, -1, ArrayList())
        put(query, newState)
        return newState
    }
}