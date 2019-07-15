package bez.dev.featurenotes.data

import android.preference.PreferenceManager
import bez.dev.featurenotes.misc.App

object SharedPrefs {

    fun setBoolValue(key: String, bool: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(App.appContext)
        preferences.edit().putBoolean(key, bool).apply()
    }

    fun getBoolValue(key: String, defaultVal: Boolean): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(App.appContext)
        return preferences.getBoolean(key, defaultVal)
    }

}