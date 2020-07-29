package com.github.salonkasoli.moviesearchsample.search.ui

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.savedstate.SavedStateRegistry

class SearchMovieToolbarWidget(
    menuItem: MenuItem,
    savedState: SavedStateRegistry,
    lifecycle: Lifecycle
) : LifecycleObserver {

    var queryChangedListener: ((String) -> Unit)? = null
    var searchClickedListener: (() -> Unit)? = null

    // Хак, чтобы поиск начинался с небольшой задержкой.
    // Нечего дергать АПИ на любое изменение текста.
    private val handler = Handler()
    private val searchView = menuItem.actionView as SearchView

    init {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                handler.removeCallbacks(searchRunnable)
                triggerSearchListener()
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                handler.removeCallbacks(searchRunnable)
                handler.postDelayed(
                    searchRunnable,
                    SEARCH_DELAY
                )
                return false
            }
        })
        searchView.setOnSearchClickListener {
            // Хак, чтобы SearchView нормально разворачивался в landscape ориентации
            searchView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT

            searchClickedListener?.invoke()
        }

        manageSaveState(savedState, menuItem)

        lifecycle.addObserver(this)
    }

    fun updateQuery(query: String) {
        searchView.setQuery(query, false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        handler.removeCallbacks(searchRunnable)
    }

    private fun triggerSearchListener() {
        queryChangedListener?.invoke(searchView.query.toString())
    }

    private val searchRunnable = Runnable {
        triggerSearchListener()
    }

    private fun manageSaveState(savedState: SavedStateRegistry, menuItem: MenuItem) {
        savedState.consumeRestoredStateForKey(BUNDLE_KEY)?.let { bundle: Bundle ->
            val wasExpanded = bundle.getBoolean(IS_SEARCH_EXPANDED, false)
            if (wasExpanded) {
                menuItem.expandActionView()
                // Маленький хак, чтобы после поворота экрана не выскакивала клавиатура.
                val hasFocus = bundle.getBoolean(IS_SEARCH_FOCUSED, false)
                if (!hasFocus) {
                    searchView.clearFocus()
                }
            }
        }
        savedState.registerSavedStateProvider(BUNDLE_KEY, {
            return@registerSavedStateProvider Bundle().apply {
                putBoolean(IS_SEARCH_EXPANDED, !searchView.isIconified)
                putBoolean(IS_SEARCH_FOCUSED, searchView.hasFocus())
            }
        })
    }

    companion object {
        private const val SEARCH_DELAY = 500L

        private const val BUNDLE_KEY = "search"
        private const val IS_SEARCH_EXPANDED = "is_expanded"
        private const val IS_SEARCH_FOCUSED = "is_search_focused"
    }
}