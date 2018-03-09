package msq.inok.bel.testKKD.serverApi

import android.content.Context
import msq.inok.bel.testKKD.data.ApplicationSettings
import msq.inok.bel.testKKD.entities.ForecastResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by inoknote on 13/01/18.
 */

class Communicator(val context: Context) {


	fun communicate(days: Int, city: String): ForecastResult? {

		val baseURL = ApplicationSettings.BASE_URL
		val retrofit = Retrofit.Builder()
				.baseUrl(baseURL)
				.addConverterFactory(GsonConverterFactory.create())
				.build()

		val weatherAPI = retrofit.create(WeatherAPI::class.java)
		val forecastCall = weatherAPI.getForecast(city, days, ApplicationSettings.API_KEY_STOLEN)

		val response = forecastCall.execute()

		if (response.body() != null) {
			val forecastResult = response.body()
			return forecastResult!!
		}
		return null
	}

	//historical request must be, but there is no possibility on the resourse
	private fun communicateHistory(city: String, days: Int): Unit {

	}

}