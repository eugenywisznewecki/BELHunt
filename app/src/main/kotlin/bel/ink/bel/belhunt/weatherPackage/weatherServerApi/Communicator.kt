package bel.ink.bel.belhunt.weatherPackage.weatherServerApi

import android.content.Context
import bel.ink.bel.belhunt.App
import bel.ink.bel.belhunt.weatherPackage.entities.ForecastResult
import msq.inok.bel.belhunt.util.API_KEY_STOLEN
import javax.inject.Inject


class Communicator(val context: Context) {

    @Inject
    lateinit var weatherAPI: WeatherAPI

    init {
        App.component.inject(this)
    }

    fun getForecastByCoordinated(days: Int, lat: Int, lonq: Int): ForecastResult? {
        val forecastCall = weatherAPI.getForecastCoordinates(lat, lonq, days, API_KEY_STOLEN)
        val response = forecastCall.execute()

        if (response.body() != null) {
            val forecastResult = response.body()
            return forecastResult
        }
        return null
    }
}