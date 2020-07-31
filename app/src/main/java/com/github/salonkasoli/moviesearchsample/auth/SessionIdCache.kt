package com.github.salonkasoli.moviesearchsample.auth

import android.content.Context

class SessionIdCache(
    context: Context
) {

    private val prefs = context.getSharedPreferences("session_id_cache", Context.MODE_PRIVATE)

    fun getSessionId(): String? {
        return prefs.getString(PREF_SESSION_ID, null)
    }

    fun setSessionId(sessionId: String?) {
        prefs.edit()
            .putString(PREF_SESSION_ID, sessionId)
            .apply()
    }

    companion object {

        private const val PREF_SESSION_ID = "session_id"
    }
}