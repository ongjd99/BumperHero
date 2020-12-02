package com.johnnyong.android.gamedevbumperhero

import android.content.Context
import android.preference.PreferenceManager

private const val PREF_UPGRADES = "upgrades"

object SavedPreferences {
    fun getStoredQuery(context: Context): String {
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_UPGRADES, "")!!
    }
    fun setStoredQuery(context: Context, upgrades: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_UPGRADES, upgrades)
            .apply()
    }
}

