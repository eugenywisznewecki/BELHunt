package msq.inok.bel.belhunt.serverApi

import android.content.Context
import msq.inok.bel.belhunt.App
import msq.inok.bel.belhunt.entities.ForecastResult
import msq.inok.bel.belhunt.util.API_KEY_STOLEN
import javax.inject.Inject

/**
 * Created by inoknote on 09/03/18.
 */

class Communicator(val context: Context) {


	@Inject
	lateinit var weatherAPI: WeatherAPI

	init {
		App.component.inject(this)
	}

	fun getForecast(days: Int, city: String): ForecastResult? {

		val forecastCall = weatherAPI.getForecast(city, days, API_KEY_STOLEN)
		val response = forecastCall.execute()

		//TODO wtf:)
		if (response.body() != null) {
			val forecastResult = response.body()
			return forecastResult
		}
		return null
	}

	//TODO Later? historical request
	private fun communicateHistory(city: String, days: Int): Unit {

	}

}