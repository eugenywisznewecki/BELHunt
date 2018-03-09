package msq.inok.bel.testKKD.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import msq.inok.bel.testKKD.App
import msq.inok.bel.testKKD.R
import msq.inok.bel.testKKD.entities.ForecastList
import msq.inok.bel.testKKD.services.WeatherIService
import msq.inok.bel.testKKD.ui.listAdapters.WeatherListAdapter
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {


	lateinit var br: BroadcastReceiver


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		App.component.inject(this) // inject point

		weatherList.layoutManager = LinearLayoutManager(this)

		br = object : BroadcastReceiver() {
			override fun onReceive(context: Context, intent: Intent?) {

				val list = intent?.getSerializableExtra(WeatherIService.FORECAST_LIST_ACTION_SEND) as ForecastList
				updateDataUI(list)
				Log.d("TAG", "broacast receivers " + list.toString())
			}
		}

		val intentFilter = IntentFilter(WeatherIService.BROADCAST_ACTION)
		registerReceiver(br, intentFilter)
	}


	private fun updateDataUI(listForecast: ForecastList) {

		val adapter = WeatherListAdapter(listForecast)
		weatherList.adapter = adapter
		weatherList.adapter.notifyDataSetChanged()
		toast("updated Data by interval")
	}

	override fun onDestroy() {
		super.onDestroy()
		unregisterReceiver(br)
	}
}
