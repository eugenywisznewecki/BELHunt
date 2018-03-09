package msq.inok.bel.belhunt.widget.factories

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import msq.inok.bel.belhunt.R
import android.widget.RemoteViewsService.RemoteViewsFactory
import java.text.SimpleDateFormat


/**
 * Created by inoknote on 13/01/18.
 */
class MyWidgetFactory(internal var context: Context, intent: Intent) : RemoteViewsFactory {

	val data = listOf("1111", "22222", "33333", "$$$$$$")
	var sdf: SimpleDateFormat
	var widgetID: Int = 0

	init {
		sdf = SimpleDateFormat("HH:mm:ss")
		widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID)
	}

	override fun onCreate() {
		//data = ArrayList()
	}

	override fun getCount(): Int {
		return data.size
	}

	override fun getItemId(position: Int): Long {
		return position.toLong()
	}

	override fun getLoadingView(): RemoteViews? {
		return null
	}

	override fun getViewAt(position: Int): RemoteViews {
		val rView = RemoteViews(context.packageName,
				R.layout.item_widget)

			rView.setTextViewText(R.id.tvItemText, data[position])
		return rView
	}

	override fun getViewTypeCount(): Int {
		return 1
	}

	override fun hasStableIds(): Boolean {
		return true
	}

	override fun onDataSetChanged() {
	/*	data.clear()
		data?.add(sdf.format(Date(System.currentTimeMillis())))
		data?.add(hashCode().toString())
		data?.add(widgetID.toString())
		for (i in 3..14) {
			data?.add("Item " + i)
		}*/
	}

	override fun onDestroy() {

	}

}