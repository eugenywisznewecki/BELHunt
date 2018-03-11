package msq.inok.bel.belhunt.data

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import msq.inok.bel.belhunt.util.*

/**
 * Created by inoknote on 13/01/18.
 */
// only one class with data settings for the whole app
class ApplicationSettings(val context: Context) {

	val sharedPref by lazy { PreferenceManager.getDefaultSharedPreferences(context) }


	//with defaults
	fun saveSettings(dayforecast: Int = INITdaysToFORECAST, city: String = INITCity): Boolean {
		val editor = sharedPref.edit()
		editor.putInt(DAYSFORECAST, dayforecast)
		/*editor.putInt(INTERVAL_UPDATES, intervalUpdates)*/
		editor.putString(CITY, city)

		return editor.commit()

	}

	fun loadDays() = sharedPref.getInt("DAYSFORECAST", -1)
	fun loadInterval() = sharedPref.getInt("INTERVAL_UPDATES", -1)
	fun loadCity() = sharedPref.getString("CITY", "Minsk")

	fun setTripSettings() = Triple(loadDays(), loadInterval(), loadCity())


	fun getCitiesList(): MutableSet<String> {
		val set = mutableSetOf("Minsk")
		return sharedPref.getStringSet(CITIES_LIST, set)
	}

	fun setCitiesList(set: MutableSet<String>): Boolean {

		Log.d("TAG", "pref: " + set.toString())
		val editor = sharedPref.edit()

		editor.putStringSet(CITIES_LIST, set)

		if (editor.commit())
			return true
		return false

	}

}