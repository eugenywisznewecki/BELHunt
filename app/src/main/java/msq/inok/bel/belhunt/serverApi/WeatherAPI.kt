package msq.inok.bel.belhunt.serverApi

import msq.inok.bel.belhunt.entities.ForecastResult
import msq.inok.bel.belhunt.entities.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by inoknote on 13/01/18.
 */
interface WeatherAPI {

	@GET("weather/?mode=json&units=metric")
	fun getWeather(@Query("q") city: String, @Query("APPID") appId: String): Call<Weather>

	@GET("forecast/daily?mode=json&units=metric")
	fun getForecast(@Query("q") city: String, @Query("cnt") days: Int, @Query("APPID") appId: String):
			Call<ForecastResult>

}

