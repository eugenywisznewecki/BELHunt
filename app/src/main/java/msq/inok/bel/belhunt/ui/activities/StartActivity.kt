package msq.inok.bel.belhunt.ui.activities

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.jakewharton.rxbinding2.widget.RxTextView
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import msq.inok.bel.belhunt.App
import msq.inok.bel.belhunt.R
import msq.inok.bel.belhunt.data.ApplicationSettings
import msq.inok.bel.belhunt.entities.ForecastIn
import msq.inok.bel.belhunt.entities.ForecastList
import msq.inok.bel.belhunt.serverApi.Communicator
import msq.inok.bel.belhunt.ui.listAdapters.WeatherListAdapter
import msq.inok.bel.belhunt.util.BadWeatherGuard
import msq.inok.bel.belhunt.util.InetChecker
import msq.inok.bel.belhunt.util.converters.WeatherMapConverter
import msq.inok.bel.belhunt.util.extensionsFuns.toDateString
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class StartActivity : AppCompatActivity() {

	@Inject
	lateinit var applicationSettings: ApplicationSettings

	@Inject
	lateinit var inetChecker: InetChecker

	@Inject
	lateinit var communicator: Communicator

	@Inject
	lateinit var badWeatherGuard: BadWeatherGuard


	//lateinit var citiesSet: MutableSet<String>


	var resultValue = Intent()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setWidgetSettAnswer()
		setContentView(R.layout.activity_start)

		App.component.inject(this) // inject point


		listStartActivity.layoutManager = LinearLayoutManager(this)
		//citiesSet = applicationSettings.getCitiesList()
		//Log.d("TAG", "set: " + citiesSet.toString())


		//permission
		Dexter.withActivity(this)
				.withPermissions(
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.ACCESS_NETWORK_STATE,
						Manifest.permission.INTERNET,
						Manifest.permission.BIND_REMOTEVIEWS // possibly doesnt not work
				)
				.withListener(BaseMultiplePermissionsListener())
				.check()


/*		//Spinner
		val spinnerViewAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				citiesSet.toList())
		spinnerViewAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		spinnerView.adapter = spinnerViewAdapter
		spinnerView.prompt = "Select City"
		spinnerView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
				val text = spinnerView.selectedItem.toString()
				cityTitleView.setText(text, TextView.BufferType.EDITABLE)
			}

			override fun onNothingSelected(parent: AdapterView<*>) {}
		}*/


		/*	addCityButton.setOnClickListener {
				val city = cityTitleView.text.toString()
				citiesSet.add(city)
				Log.d("TAG", "set: " + citiesSet.toString())

				val b = applicationSettings.setCitiesList(citiesSet)
				Log.d("TAG", "set upda: " + b.toString())

				spinnerViewAdapter.add(city)
				spinnerViewAdapter.notifyDataSetChanged()
			}*/


		/*okSetttingChangesButton.setOnClickListener {
			val setting = readSaveSetting()
			Log.d("TAG", "SETTINGS: " + setting.toString())

			//does wifi work - start work of the program. point
			if (inetChecker.checInternet()) {

				setResult(RESULT_OK, resultValue)

				if (WeatherIService.isSericeAlarnOn(ctx))
					WeatherIService.setServiceAlarm(ctx, false, setting.first, setting.second.toLong(), setting.third)

				//starting IntentService
				val intent = WeatherIService.newIntent(ctx)
				intent.putExtra(DAYSFORECAST, setting.first)
				intent.putExtra(INTERVAL_UPDATES, setting.second)
				intent.putExtra(CITY, setting.third)
				WeatherIService.startForecastingService(ctx, setting.first, setting.second.toLong(), setting.third)

				//starting alarming IntentService
				///days, time to update, city
				WeatherIService.setServiceAlarm(ctx, true, setting.first, setting.second.toLong(), setting.third)

				startActivity<MainActivity>()
				finish()
			} else toast("Cannot work without WIFI")
		}*/

		seekBarTimeView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
			}

			override fun onStartTrackingTouch(p0: SeekBar?) {
			}

			override fun onStopTrackingTouch(p0: SeekBar?) {
				textSeekView.setText(seekBarTimeView.progress.toString())
			}
		})


		//RX+RXBindings
		RxTextView.textChangeEvents(cityTitleView)
				.filter { e -> e.text().length >= 3 }
				.debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
				.subscribe { e -> loadWeather(16, e.text().toString()) }
	}

	private fun setWidgetSettAnswer() {
		var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
		val intent = getIntent()
		val extras = intent.extras
		if (extras != null) {
			widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID)
		}
		if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {

		}
		resultValue = Intent()
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
		setResult(RESULT_CANCELED, resultValue)
	}

	//returns days, time to update, city
	fun readSaveSetting(): Triple<Int, Int, String> {

		var showDaysForecast = 1;
		var timeToUpdates = 30;

		with(rgDaysForecast) {
			when {
				(radio1.isChecked) -> showDaysForecast = 1
				(radio5.isChecked) -> showDaysForecast = 5
				(radio16.isChecked) -> showDaysForecast = 16
			}
		}

		timeToUpdates = seekBarTimeView.progress
		val city = cityTitleView.text.toString()

		applicationSettings.saveSettings(showDaysForecast, timeToUpdates, city)

		return Triple(showDaysForecast, timeToUpdates, city)
	}


	//with defaults bcsof NPE upper
	private fun loadWeather(days: Int, city: String) = async(UI) {
		if (inetChecker.checInternet()) {
			val result = bg { communicator.communicate(days, city) }
			val forecastList = WeatherMapConverter().convertResultToForList(city, result.await()!!)
			if (forecastList.size > 0) {
				/*val intentBroad = Intent(WeatherIService.BROADCAST_ACTION)
		intentBroad.putExtra(FORECAST_LIST_ACTION_SEND, forecastList)
		sendBroadcast(intentBroad)*/
				updateDataUI(forecastList)
			} else
				Log.d("TAG", "forecastList.size == 0! ")
			Log.d("TAG", "was getting from the server: " + forecastList.toString())
		}
	}

	private fun startForecasting(days: Int, city: String): ForecastList? {
		if (inetChecker.checInternet()) {
			val result = communicator.communicate(days, city)
			if (result != null) {
				val forecastList = WeatherMapConverter().convertResultToForList(city, result)
				val negativeForecast = badWeatherGuard.checkNextBadWeather(forecastList)
				if (negativeForecast != null) {
					setNotification(negativeForecast)
				}
				//save to static field
				//WeatherIService.forecastListStatic = forecastList
				if (forecastList.size > 0) {
					updateDataUI(forecastList)

				} else Log.d("TAG", "forecastList.size == 0! ")
			} else Log.d("TAG", "result = null ")
		} else Log.d("TAG", "no internet")
		return null
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

	private fun updateDataUI(listForecast: ForecastList) {

		imageGUNS.visibility = View.GONE
		val adapter = WeatherListAdapter(listForecast)
		listStartActivity.adapter = adapter
		listStartActivity.adapter.notifyDataSetChanged()
		toast("updated")
	}


}

