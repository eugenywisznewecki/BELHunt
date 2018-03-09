package msq.inok.bel.testKKD.widget.factories

import android.content.Intent
import android.widget.RemoteViewsService


class MyService : RemoteViewsService() {

	override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
		return MyWidgetFactory(applicationContext, intent)
	}

}
