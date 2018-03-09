package msq.inok.bel.belhunt.ui.activities

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_start.*
import msq.inok.bel.belhunt.App
import msq.inok.bel.belhunt.R
import msq.inok.bel.belhunt.data.ApplicationSettings
import msq.inok.bel.belhunt.data.CITY
import msq.inok.bel.belhunt.data.DAYSFORECAST
import msq.inok.bel.belhunt.data.INTERVAL_UPDATES
import msq.inok.bel.belhunt.services.WeatherIService
import msq.inok.bel.belhunt.util.WifiChecker
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject


class StartActivity : AppCompatActivity() {

	@Inject
	lateinit var applicationSettings: ApplicationSettings

	@Inject
	lateinit var wifiChecker: WifiChecker

	lateinit var citiesSet: MutableSet<String>

	var resultValue = Intent()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setWidgetSettAnswer()
		setContentView(R.layout.activity_start)

		App.component.inject(this) // inject point

		citiesSet = applicationSettings.getCitiesList()
		Log.d("TAG", "set: " + citiesSet.toString())


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


		//Spinner
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
		}


		addCityButton.setOnClickListener {
			val city = cityTitleView.text.toString()
			citiesSet.add(city)
			Log.d("TAG", "set: " + citiesSet.toString())

			val b = applicationSettings.setCitiesList(citiesSet)
			Log.d("TAG", "set upda: " + b.toString())

			spinnerViewAdapter.add(city)
			spinnerViewAdapter.notifyDataSetChanged()
		}



		okSetttingChangesButton.setOnClickListener {
			val setting = readSaveSetting()
			Log.d("TAG", "SETTINGS: " + setting.toString())

			//does wifi work - start work of the program. point
			if (wifiChecker.checkWifi()) {

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
		}

		seekBarTimeView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
			}

			override fun onStartTrackingTouch(p0: SeekBar?) {
			}

			override fun onStopTrackingTouch(p0: SeekBar?) {
				textSeekView.setText(seekBarTimeView.progress.toString())
			}
		})
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

}

