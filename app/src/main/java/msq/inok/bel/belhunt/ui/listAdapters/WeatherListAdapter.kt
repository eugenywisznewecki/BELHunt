package msq.inok.bel.belhunt.ui.listAdapters

import android.graphics.Color.RED
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_weather.view.*
import msq.inok.bel.belhunt.R
import msq.inok.bel.belhunt.entities.ForecastIn
import msq.inok.bel.belhunt.entities.ForecastList
import msq.inok.bel.belhunt.util.extensionsFuns.toDateString

/**
 * Created by inoknote on 13/01/18.
 */
class WeatherListAdapter(private val listForecast: ForecastList) : RecyclerView.Adapter<WeatherListAdapter.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.setWeatherInList(listForecast[position])
	}

	override fun getItemCount() = listForecast.size

	class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

		fun setWeatherInList(forecast: ForecastIn) {
			with(forecast) {
				Picasso.with(itemView.context).load(iconUrl).into(itemView.icon)
				itemView.date.text = date.toDateString()
				itemView.description.text = description
				itemView.nightTemperature.text = "night: ${low}º"
				itemView.dayTemperature.text = "day: ${high}º"
				itemView.wind.text = "wind speed: ${speed}"

				when {
					(high > 29 || high < -10) -> itemView.dayTemperature.setBackgroundColor(RED)
					(low > 29 || low < -10) -> itemView.nightTemperature.setBackgroundColor(RED)
					(speed > 10) -> itemView.wind.setBackgroundColor(RED)

				}
			}
		}
	}
}
