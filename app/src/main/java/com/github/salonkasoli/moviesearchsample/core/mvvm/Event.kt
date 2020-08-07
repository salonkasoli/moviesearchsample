package com.github.salonkasoli.moviesearchsample.core.mvvm

// Украдено из https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
/**
 * Штука, чтобы удобно было работать с liveDatой + ивентами
 */
open class Event<out T>(
    private val content: T,
    initialHandled: Boolean = false
) {

    var hasBeenHandled = initialHandled
        private set

    fun handle(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun getContent(): T = content
}

class SimpleEvent(initialHandled: Boolean = false) : Event<Unit>(Unit, initialHandled)