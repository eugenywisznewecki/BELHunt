package msq.inok.bel.testKKD.util

import android.content.Context
import msq.inok.bel.testKKD.entities.ForecastIn
import msq.inok.bel.testKKD.entities.ForecastList

/**
 * Created by User on 09.03.2018.
 */

//to warn a hunter about bad weather

class BadWeatherGuard(val context: Context) {

	fun checkNextBadWeather(listForecast: ForecastList): ForecastIn? {
		//have to find only the first
		for (i in 0..listForecast.size-1) {
			if (listForecast[i].high > 29 || listForecast[i].low < -10
					|| listForecast[i].high < -10 || listForecast[i].low > 29 ||
					listForecast[i].speed > 15) {
				return listForecast[i]
			}
		}
		return null
	}

}
