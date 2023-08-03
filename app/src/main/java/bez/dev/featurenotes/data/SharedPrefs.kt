package bez.dev.featurenotes.data

import android.content.Context
import android.preference.PreferenceManager

class SharedPrefs (val context: Context) {

    fun setBoolValue(key: String, bool: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putBoolean(key, bool).apply()
    }

    fun getBoolValue(key: String, defaultVal: Boolean): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(key, defaultVal)
    }

    fun deleteAllPrefs() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().clear().apply()
    }

}