package msq.inok.bel.belhunt.util.converters

import msq.inok.bel.belhunt.entities.Forecast
import msq.inok.bel.belhunt.entities.ForecastIn
import msq.inok.bel.belhunt.entities.ForecastList
import msq.inok.bel.belhunt.entities.ForecastResult
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by inoknote on 13/01/18.
 */

class WeatherMapConverter {

	fun convertResultToForList(cityWname: String, forecast: ForecastResult) = with(forecast) {
		ForecastList(cityWname, city.country, convertListForecastToInside(list))
	}

	private fun convertListForecastToInside(list: List<Forecast>): List<ForecastIn> {
		return list.mapIndexed { i, forecast ->
			val dt = Calendar.getInstance().timeInMillis + TimeUnit.DAYS.toMillis(i.toLong())
			convertForecastToIN(forecast.copy(dt = dt))
		}
	}

	private fun convertForecastToIN(forecast: Forecast): ForecastIn {

		with(forecast) {
			val forecastIn = ForecastIn(-1, dt, weather[0].description, temp.max.toInt(), temp.min.toInt(), speed,
					createIconUrl(weather[0].icon))
			return forecastIn
		}
	}

	private fun createIconUrl(icon: String) = "http://openweathermap.org/img/w/$icon.png"
}

