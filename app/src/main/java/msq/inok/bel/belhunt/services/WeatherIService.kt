package msq.inok.bel.belhunt.services

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import msq.inok.bel.belhunt.App
import msq.inok.bel.belhunt.R
import msq.inok.bel.belhunt.data.ApplicationSettings
import msq.inok.bel.belhunt.data.CITY
import msq.inok.bel.belhunt.data.DAYSFORECAST
import msq.inok.bel.belhunt.entities.ForecastIn
import msq.inok.bel.belhunt.entities.ForecastList
import msq.inok.bel.belhunt.serverApi.Communicator
import msq.inok.bel.belhunt.util.BadWeatherGuard
import msq.inok.bel.belhunt.util.WifiChecker
import msq.inok.bel.belhunt.util.converters.WeatherMapConverter
import msq.inok.bel.belhunt.util.extensionsFuns.toDateString
import javax.inject.Inject


class WeatherIService : IntentService("WeatherIService") {

	@Inject
	lateinit var communicator: Communicator

	@Inject
	lateinit var wifiChecker: WifiChecker

	@Inject
	lateinit var badWeatherGuard: BadWeatherGuard

	override fun onCreate() {
		super.onCreate()
		App.component.inject(this) // inject point
	}

	companion object {

		val ACTION_WEATHER_FORECASTING = "msq.inok.bel.belhunt.services.action.weatherForecasting"
		val BROADCAST_ACTION = "msq.inok.bel.belhunt.services.action.weatherForecasting"
		val FORECAST_LIST_ACTION_SEND = "FORECAST_LIST_ACTION_SEND"

		//to getting from manually updated widgets
		var forecastListStatic: ForecastList? = null

		fun newIntent(context: Context) = Intent(context, WeatherIService::class.java)

		fun setIntentToShow(context: Context, days: Int, interval: Long, city: String): Intent {
			val intent = Intent(context, WeatherIService::class.java)
			intent.action = ACTION_WEATHER_FORECASTING
			intent.putExtra(DAYSFORECAST, days)
			intent.putExtra(CITY, city)
			return intent
		}

		fun startForecastingService(context: Context, days: Int, interval: Long, city: String) {
			val intent = setIntentToShow(context, days, interval, city)
			context.startService(intent)
		}

		fun setServiceAlarm(context: Context, isOn: Boolean,
		                    days: Int, interval: Long, city: String) {

			val intent = setIntentToShow(context, days, interval, city)
			val pendingIntent = PendingIntent.getService(context, 1, intent, 0)
			val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

			if (isOn) {
				alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
						SystemClock.elapsedRealtime(), interval * 1000, pendingIntent) //interval in seconds/ min system apxmtly 60
			} else {
				alarmManager.cancel(pendingIntent)
				pendingIntent.cancel()
			}
		}

		fun isSericeAlarnOn(context: Context): Boolean {
			val int = WeatherIService.newIntent(context)
			val pi = PendingIntent.getService(context, 0, int, PendingIntent.FLAG_NO_CREATE)
			return pi != null
		}
	}


	override fun onHandleIntent(intent: Intent?) {
		if (intent != null) {
			val action = intent.action

			when (action) {
				(ACTION_WEATHER_FORECASTING) -> {
					val days = intent.getIntExtra(DAYSFORECAST, 2)
					val city = intent.getStringExtra(CITY)
					handleForecastingService(days, city)
				}

			}
		} else Log.d("TAG", "onHandleIntent doen't work")
	}


	private fun handleForecastingService(days: Int, city: String) {

		if (wifiChecker.checkWifi() && wifiChecker.isNetworkForBackgroundAwailable()) {

			val result = communicator.communicate(days, city)

			if (result != null) {
				val forecastList = WeatherMapConverter().convertResultToForList(city, result)

				val negativeForecast = badWeatherGuard.checkNextBadWeather(forecastList)
				if (negativeForecast != null) {
					setNotification(negativeForecast)
				}

				//save to static field
				WeatherIService.forecastListStatic = forecastList

				if (forecastList.size > 0) {
					val intentBroad = Intent(WeatherIService.BROADCAST_ACTION)
					intentBroad.putExtra(FORECAST_LIST_ACTION_SEND, forecastList)
					sendBroadcast(intentBroad)
				} else Log.d("TAG", "forecastList.size == 0! ")
			} else Log.d("TAG", "result = null ")
		} else Log.d("TAG", "no wifi")
	}

	private fun setNotification(negativeForecast: ForecastIn) {

		val notification = NotificationCompat.Builder(this)
				.setTicker("WARNING!")
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle("Negative forecast")
				.setContentText("${negativeForecast.date.toDateString()} ${negativeForecast.description} ${negativeForecast.high}C" +
						"${negativeForecast.low}C wind speed: ${negativeForecast.speed}")
				.build()

		val notificationManager = NotificationManagerCompat.from(this)
		notificationManager.notify(0, notification)
	}
}














/*	//with defaults bcsof NPE upper
	private fun loadWeather(days: Int, city: String) = async(UI) {
		val result = bg { communicator.communicate(days, city) }
		val forecastList = WeatherMapConverter().convertResultToForList(city, result.await()!!)

		if (forecastList.size > 0) {
			val intentBroad = Intent(WeatherIService.BROADCAST_ACTION)
			intentBroad.putExtra(FORECAST_LIST_ACTION_SEND, forecastList)
			sendBroadcast(intentBroad)
		} else
			Log.d("TAG", "forecastList.size == 0! ")

		Log.d("TAG", "was getting from the server: " + forecastList.toString())
	}*/

