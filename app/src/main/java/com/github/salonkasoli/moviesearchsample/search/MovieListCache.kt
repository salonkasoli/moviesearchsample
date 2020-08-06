package com.github.salonkasoli.moviesearchsample.search

import android.util.LruCache
import com.github.salonkasoli.moviesearchsample.search.ui.MovieSearchState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieListCache @Inject constructor() {

    private val lruCache = LruCache<String, MovieSearchState>(10)

    fun put(query: String, state: MovieSearchState) {
        lruCache.put(query, state)
    }

    fun get(query: String): MovieSearchState {
        val state: MovieSearchState? = lruCache.get(query)
        if (state != null) {
            return state
        }

        val newState = MovieSearchState(0, -1, ArrayList())
        put(query, newState)
        return newState
    }
}