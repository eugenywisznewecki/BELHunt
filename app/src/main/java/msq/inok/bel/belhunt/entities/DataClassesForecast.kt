package msq.inok.bel.belhunt.entities

import java.io.Serializable

/**
 * Created by inoknote on 09/03/18.
 */

data class ForecastResult(val city: City, val list: List<Forecast>) : Serializable

data class City(val id: Long, val name: String, val coord: Coordinates, val country: String, val population: Int) : Serializable

data class Coordinates(val lon: Float, val lat: Float) : Serializable

data class Forecast(val dt: Long, val temp: Temperature, val pressure: Float, val humidity: Int,
                    val weather: List<Weather>, val speed: Float, val deg: Int, val clouds: Int, val rain: Float) : Serializable


data class Temperature(val day: Float, val min: Float, val max: Float, val night: Float, val eve: Float, val morn: Float) : Serializable
data class Weather(val id: Long, val main: String, val description: String, val icon: String) : Serializable
/*data class Wind (val speed: Float, val deg: Float)*/

data class ForecastList(val city: String, val country: String,
                        val dailyForecast: List<ForecastIn>) : Serializable {

	//Instead of iterable, its simplier
	val size: Int
		get() = dailyForecast.size

	//overload to get only forecast position
	operator fun get(position: Int) = dailyForecast[position]
}

data class ForecastIn(val id: Long, val date: Long, val description: String, val high: Int, val low: Int, val speed: Float,
                      val iconUrl: String) : Serializable

/*
{"city":{"id":1851632,"name":"Shuzenji",
	"coord":{"lon":138.933334,"lat":34.966671},
	"country":"JP",
	"cod":"200",
	"message":0.0045,
	"cnt":38,
	"list":[{
		"dt":1406106000,
		"main":{
		"temp":298.77,
		"temp_min":298.77,
		"temp_max":298.774,
		"pressure":1005.93,
		"sea_level":1018.18,
		"grnd_level":1005.93,
		"humidity":87,
		"temp_kf":0.26},
		"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}],
		"clouds":{"all":88},
		"wind":{"speed":5.71,"deg":229.501},
		"sys":{"pod":"d"},
		"dt_txt":"2014-07-23 09:00:00"}
	]}
}
*/
