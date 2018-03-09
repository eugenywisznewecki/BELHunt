package msq.inok.bel.testKKD.widget.widgets

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import msq.inok.bel.testKKD.services.WeatherIService


class WeatherWidget : AppWidgetProvider() {
	val UPDATE_ALL_WIDGETS = "UPDATE_ALL_WIDGETS"

	override fun onReceive(context: Context, intent: Intent) {
		super.onReceive(context, intent)

		if (intent.action == UPDATE_ALL_WIDGETS) {
			val thisAppWidget = ComponentName(context.getPackageName(), javaClass.name)
			val appWidgetManager = AppWidgetManager.getInstance(context)
			val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
			for (appWidgetID in ids) {
				updateWidget(context, appWidgetManager, appWidgetID)
			}
		}
/*
		if (intent != null) {
			val action = intent.action
			when (action) {
				(WeatherIService.ACTION_WEATHER_FORECASTING_Widget) -> {
					val days = intent.getIntExtra("days", 2)
					val city = intent.getStringExtra("city")
				}
			}
		}
		else Log.d("TAG", "onHandleIntent doen't work")*/

		//val list = intent?.getSerializableExtra(WeatherIService.FORECAST_LIST_ACTION_SEND) as ForecastList

	}

	private fun updateWidget(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetID: Int) {

		Log.d("TAG", "updating widget" + WeatherIService.forecastListStatic.toString())

	}

	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

		for (appWidgetId in appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId)
		}

	}

	override fun onEnabled(context: Context) {
		super.onEnabled(context)
		val intent = Intent(context, WeatherWidget::class.java)
		intent.action = UPDATE_ALL_WIDGETS
		val pIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
		val alarmManager = context
				.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
				60000, pIntent)

		Log.d("TAG", "onEnabled")


	}

	override fun onDisabled(context: Context) {
		super.onDisabled(context)
		val intent = Intent(context, WeatherWidget::class.java)
		intent.action = UPDATE_ALL_WIDGETS
		val pIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
		val alarmManager = context
				.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		alarmManager.cancel(pIntent)

		Log.d("TAG", "onDisabled")
	}

	companion object {

		internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
		                             appWidgetId: Int) {

		}
	}
}

