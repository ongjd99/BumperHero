package com.johnnyong.android.gamedevbumperhero

import android.content.Context
import android.preference.PreferenceManager

private const val PREF_UPGRADES = "upgrades"
private const val PREF_GOLD = "gold"

object SavedPreferences {
    fun getStoredGold(context: Context): Int {
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(PREF_GOLD, 0)!!
    }
    fun setStoredGold(context: Context, gold: Int) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putInt(PREF_GOLD, gold)
            .apply()
    }

    fun getStoredUpgrades(context: Context): String {
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(context)

        return prefs.getString(PREF_UPGRADES, "")
    }
    fun setStoredUpgrades(context: Context, upgrades: IntArray) {
        val str = StringBuilder()

        for(i in upgrades.indices) {
            if(i < upgrades.lastIndex) {
                str.append(upgrades[i]).append(",")
            } else {
                str.append(upgrades[i])
            }
        }

        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_UPGRADES, str.toString())
            .apply()
    }
}

