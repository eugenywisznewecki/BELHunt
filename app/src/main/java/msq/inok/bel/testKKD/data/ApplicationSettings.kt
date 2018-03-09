package msq.inok.bel.testKKD.data

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log

/**
 * Created by inoknote on 13/01/18.
 */
// only one class with data settings for the whole app
public class ApplicationSettings(val context: Context) {

	val sharedPref by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

	companion object {

		// URL to API
		val API_KEY_STOLEN = "15646a06818f61f7b8d7823ca833e1ce"
		private val APP_ID = "220b17d30854858c0c47192689cf6d4e"
		val BASE_URL = "http://api.openweathermap.org/data/2.5/"

		//string values
		val DAYSFORECAST = "days_forecast_"
		val INTERVAL_UPDATES = "intervat_updates_"
		val CITY = "_city_"

		val CITIES_LIST = "list_cities"

		// initially
		val INITTimeUpdate = 30 // min
		val INITdaysToForecast = 2
		val INITCity = "Minsk"
	}

	//with defaults
	fun saveSettings(dayforecast: Int = INITdaysToForecast, intervalUpdates: Int = INITTimeUpdate, city: String = INITCity): Boolean {
		val editor = sharedPref.edit()
		editor.putInt(DAYSFORECAST, dayforecast)
		editor.putInt(INTERVAL_UPDATES, intervalUpdates)
		editor.putString(CITY, city)

		if (editor.commit())
			return true
		else return false
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