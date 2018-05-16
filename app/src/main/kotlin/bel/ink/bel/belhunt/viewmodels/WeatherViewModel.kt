package bel.ink.bel.belhunt.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import bel.ink.bel.belhunt.utilits.NetChecker
import bel.ink.bel.belhunt.weatherPackage.entities.ForecastList
import bel.ink.bel.belhunt.weatherPackage.weatherServerApi.Communicator
import com.crashlytics.android.Crashlytics
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import msq.inok.bel.belhunt.util.converters.WeatherMapConverter
import timber.log.Timber

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    /*  init {
          App.component.inject(this)
      }*/

    private val inetChecker by lazy { NetChecker(application.applicationContext) }
    private val communicator by lazy { Communicator(application.applicationContext) }

    var liveData: MutableLiveData<ForecastList?>? = null
    public var lat: Int = 0
    public var loq: Int = 0
    public var days: Int = 0

    fun setCoordinatesDay(day: Int, lat: Int, lonq: Int) {
        this.days = day
        this.lat = lat
        this.loq = lonq
        loadForecastByCoordinates()
    }

    fun getLive(): MutableLiveData<ForecastList?>? {
        if (liveData == null) {
            liveData = MutableLiveData()
            loadForecastByCoordinates()
        }
        return liveData
    }


    fun loadForecastByCoordinates() {

        launch(UI) {

            if (inetChecker.checInternet()) {
                val result = async {
                    communicator.getForecastByCoordinated(days, lat, loq)
                }
                try {
                    val forecastList = WeatherMapConverter().convertResultToForList("", result.await())

                    liveData?.value = forecastList

                } catch (e: Exception) {
                    Crashlytics.log(e.message)
                }

                Timber.d("was getting from the server: ")

            } else Timber.d("no Internet")
        }
    }

    override fun onCleared() {
        super.onCleared()

        lat = 0
        loq = 0
        days = 0
    }
}