package bel.ink.bel.belhunt.weatherPackage.entities

import java.io.Serializable


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