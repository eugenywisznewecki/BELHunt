package msq.inok.bel.belhunt.mvp.presenters

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import msq.inok.bel.belhunt.App
import msq.inok.bel.belhunt.checkers.BadWeatherGuard
import msq.inok.bel.belhunt.checkers.InetChecker
import msq.inok.bel.belhunt.data.ApplicationSettings
import msq.inok.bel.belhunt.entities.ForecastList
import msq.inok.bel.belhunt.mvp.view.ImvpMainView
import msq.inok.bel.belhunt.serverApi.Communicator
import msq.inok.bel.belhunt.util.INITdaysToFORECAST
import msq.inok.bel.belhunt.util.converters.WeatherMapConverter
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by User on 09.03.2018.
 */

@InjectViewState
class Presenter : MvpPresenter<ImvpMainView>() {

	@Inject
	lateinit var applicationSettings: ApplicationSettings

	@Inject
	lateinit var inetChecker: InetChecker

	@Inject
	lateinit var communicator: Communicator

	@Inject
	lateinit var badWeatherGuard: BadWeatherGuard

	lateinit var forecastListIN: ForecastList

    lateinit var observableIN: Observable<CharSequence>


	init {
		App.component.inject(this)
		Log.d("TAG", "presenter inicialized")
	}

	fun setObservableInPresenter(observable: Observable<CharSequence>) {
			this.observableIN = observable
			Log.d("TAG", "observable is inicialized")
	}

	fun setSubscribe(){
		observableIN.filter { e -> e.length >= 3 }
				.debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
				.cache()
				.subscribe { e -> loadForecast(INITdaysToFORECAST, e.toString()) }
	}


	override fun attachView(view: ImvpMainView?) {
		super.attachView(view)
		Log.d("TAG", "attachView")

		if (::forecastListIN.isInitialized)
			viewState.onForecastsLoaded(forecastListIN)
	}

	override fun detachView(view: ImvpMainView?) {
		super.detachView(view)
		Log.d("TAG", "detachView")


	}

	override fun onFirstViewAttach() {
		super.onFirstViewAttach()
		Log.d("TAG", "onFirstViewAttach")

		observableIN.filter { e -> e.length >= 3 }
				.debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
				.subscribe { e -> loadForecast(INITdaysToFORECAST, e.toString()) }
	}

	override fun isInRestoreState(view: ImvpMainView?): Boolean {
		return super.isInRestoreState(view)
	}

	fun setEditTextListener(){
		observableIN.filter { e -> e.length >= 3 }
				.debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
				.cache()
				.subscribe { e -> loadForecast(INITdaysToFORECAST, e.toString()) }
	}

	//TODO remove all logs
	fun loadForecast(days: Int, city: String): Deferred<Unit> {
		Log.d("TAG", "try do download " + days.toString() + " " + city)

		return async(UI) {
			if (inetChecker.checInternet()) {
				val result = bg { communicator.getForecast(days, city) }
				val forecastList = WeatherMapConverter().convertResultToForList(city, result.await()!!)
				if (forecastList.size > 0) {
					viewState.onForecastsLoaded(forecastList)
					forecastListIN = forecastList
				} else
					Log.d("TAG", "forecastList.size == 0! ")
				Log.d("TAG", "was getting from the server: " + forecastList.toString())
			}
		}
	}


}