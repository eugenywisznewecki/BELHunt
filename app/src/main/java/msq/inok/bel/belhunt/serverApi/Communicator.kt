package msq.inok.bel.belhunt.serverApi

import android.content.Context
import msq.inok.bel.belhunt.util.API_KEY_STOLEN
import msq.inok.bel.belhunt.util.BASE_URL
import msq.inok.bel.belhunt.entities.ForecastResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by inoknote on 09/03/18.
 */

class Communicator(val context: Context) {


	fun getForecast(days: Int, city: String): ForecastResult? {

		val baseURL = BASE_URL
		val retrofit = Retrofit.Builder()
				.baseUrl(baseURL)
				.addConverterFactory(GsonConverterFactory.create())
				.build()

		val weatherAPI = retrofit.create(WeatherAPI::class.java)
		val forecastCall = weatherAPI.getForecast(city, days, API_KEY_STOLEN)

		val response = forecastCall.execute()

		if (response.body() != null) {
			val forecastResult = response.body()
			return forecastResult!!
		}
		return null
	}

	//TODO Later? historical request
	private fun communicateHistory(city: String, days: Int): Unit {

	}

}