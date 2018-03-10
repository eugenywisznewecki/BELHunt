package msq.inok.bel.belhunt.ui.activities

import android.Manifest
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.jakewharton.rxbinding2.widget.RxTextView
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_start.*
import msq.inok.bel.belhunt.App
import msq.inok.bel.belhunt.R
import msq.inok.bel.belhunt.data.ApplicationSettings
import msq.inok.bel.belhunt.entities.ForecastIn
import msq.inok.bel.belhunt.entities.ForecastList
import msq.inok.bel.belhunt.presentation.presenters.Presenter
import msq.inok.bel.belhunt.serverApi.Communicator
import msq.inok.bel.belhunt.ui.listAdapters.WeatherListAdapter
import msq.inok.bel.belhunt.checkers.BadWeatherGuard
import msq.inok.bel.belhunt.checkers.InetChecker
import msq.inok.bel.belhunt.util.extensionsFuns.toDateString
import msq.inok.bel.belhunt.presentation.view.ImvpMainView
import javax.inject.Inject
import com.arellomobile.mvp.MvpAppCompatActivity


class StartActivity : MvpAppCompatActivity(), ImvpMainView {

	@InjectPresenter(type = PresenterType.GLOBAL)
	lateinit var presenter: Presenter

	@Inject
	lateinit var applicationSettings: ApplicationSettings

	@Inject
	lateinit var inetChecker: InetChecker

	@Inject
	lateinit var communicator: Communicator

	@Inject
	lateinit var badWeatherGuard: BadWeatherGuard


	//lateinit var citiesSet: MutableSet<String>


	//var resultValue = Intent()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		/*setWidgetSettAnswer()*/
		setContentView(R.layout.activity_start)

		App.component.inject(this)

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


		listStartActivity.layoutManager = LinearLayoutManager(this)

		presenter.setEditTextObservable(RxTextView.textChanges(cityTitleView))



		//TODO replace this !

		//citiesSet = applicationSettings.getCitiesList()
		//Log.d("TAG", "set: " + citiesSet.toString())


//TODO main point
		//RX+RXBindings
	/*	RxTextView.textChangeEvents(cityTitleView)
				.filter { e -> e.text().length >= 3 }
				.debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
				.subscribe { e -> loadWeather(16, e.text().toString()) }*/
	}


	//returns days, time to update, city
/*	fun readSaveSetting(): Pair<Int, String> {

		var showDaysForecast = 1

		with(rgDaysForecast) {
			when {
				(radio1.isChecked) -> showDaysForecast = 1
				(radio5.isChecked) -> showDaysForecast = 5
				(radio16.isChecked) -> showDaysForecast = 16
			}
		}

		val city = cityTitleView.text.toString()

		applicationSettings.saveSettings(showDaysForecast, city)

		return Pair(showDaysForecast, city)
	}*/


	//with defaults bcsof NPE upper
/*	private fun loadWeather(days: Int, city: String) = async(UI) {
		if (inetChecker.checInternet()) {
			val result = bg { communicator.getForecast(days, city) }
			val forecastList = WeatherMapConverter().convertResultToForList(city, result.await()!!)
			if (forecastList.size > 0) {
				*//*val intentBroad = Intent(WeatherIService.BROADCAST_ACTION)
		intentBroad.putExtra(FORECAST_LIST_ACTION_SEND, forecastList)
		sendBroadcast(intentBroad)*//*
				updateDataUI(forecastList)
			} else
				Log.d("TAG", "forecastList.size == 0! ")
			Log.d("TAG", "was getting from the server: " + forecastList.toString())
		}
	}*/

/*	private fun startForecasting(days: Int, city: String): ForecastList? {
		if (inetChecker.checInternet()) {
			val result = communicator.getForecast(days, city)
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
	}*/


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


	override fun updateDataUI() {

		listStartActivity.adapter.notifyDataSetChanged()

		when {
			(listStartActivity.adapter.itemCount == 0) -> {
				listStartActivity.visibility = View.GONE
				imageGUNS.visibility = View.VISIBLE
			}
			(listStartActivity.adapter.itemCount > 0) -> {
				listStartActivity.visibility = View.VISIBLE
				imageGUNS.visibility = View.GONE
			}
		}
	}

	override fun onForecastsLoaded(forecastList: ForecastList) {

		listStartActivity.adapter = WeatherListAdapter(forecastList)
		updateDataUI()
	}

/*	override fun onEditTextEntered(): Observable<CharSequence> {
		return RxTextView.textChanges(cityTitleView)
	}*/


	override fun onShowErrorMessage(message: String) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
/*

	override fun onEditTextEntered(): Observable<CharSequence> = RxTextView.textChanges(cityTitleView)
*/

	override fun onShowForecastList() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}




}

