package msq.inok.bel.belhunt.presentation.presenters

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import msq.inok.bel.belhunt.App
import msq.inok.bel.belhunt.data.ApplicationSettings
import msq.inok.bel.belhunt.presentation.view.ImvpMainView
import msq.inok.bel.belhunt.serverApi.Communicator
import msq.inok.bel.belhunt.checkers.BadWeatherGuard
import msq.inok.bel.belhunt.checkers.InetChecker
import msq.inok.bel.belhunt.util.converters.WeatherMapConverter
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by User on 09.03.2018.
 */
@InjectViewState
class Presenter : MvpPresenter<ImvpMainView>() {


	private var subscription: Disposable? = null

	@Inject
	lateinit var applicationSettings: ApplicationSettings

	@Inject
	lateinit var inetChecker: InetChecker

	@Inject
	lateinit var communicator: Communicator

	@Inject
	lateinit var badWeatherGuard: BadWeatherGuard

	init {
		App.component.inject(this)
	}

	override fun attachView(view: ImvpMainView?) {
		super.attachView(view)
	}

	override fun detachView(view: ImvpMainView?) {
		super.detachView(view)
	}

	override fun onFirstViewAttach() {
		super.onFirstViewAttach()


	}

	override fun isInRestoreState(view: ImvpMainView?): Boolean {
		return super.isInRestoreState(view)

	}

	//start here!
	/*fun loadForecast(){


	}*/

	fun setEditTextObservable(observable: Observable<CharSequence> ){
		Log.d("TAG", "setEditTextObservable")
		observable.filter { e -> e.length >= 3 }
				.debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
				.subscribe {e -> loadForecast(16, e.toString()) }
	}



	fun loadForecast(days: Int, city: String): Deferred<Unit> {

		Log.d("TAG", days.toString() + " " + city)


		return async(UI) {
			if (inetChecker.checInternet()) {
				val result = bg { communicator.getForecast(days, city) }
				val forecastList = WeatherMapConverter().convertResultToForList(city, result.await()!!)
				if (forecastList.size > 0) {
					/*val intentBroad = Intent(WeatherIService.BROADCAST_ACTION)
			intentBroad.putExtra(FORECAST_LIST_ACTION_SEND, forecastList)
			sendBroadcast(intentBroad)*/
					viewState.onForecastsLoaded(forecastList)
				} else
					Log.d("TAG", "forecastList.size == 0! ")
				Log.d("TAG", "was getting from the server: " + forecastList.toString())
			}
		}
	}


}