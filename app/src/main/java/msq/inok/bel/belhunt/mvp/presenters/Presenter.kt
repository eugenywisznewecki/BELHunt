package msq.inok.bel.belhunt.mvp.presenters

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import msq.inok.bel.belhunt.App
import msq.inok.bel.belhunt.checkers.InetChecker
import msq.inok.bel.belhunt.mvp.view.ImvpMainView
import msq.inok.bel.belhunt.serverApi.Communicator
import msq.inok.bel.belhunt.util.INITdaysToFORECAST
import msq.inok.bel.belhunt.util.converters.WeatherMapConverter
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@InjectViewState
class Presenter : MvpPresenter<ImvpMainView>() {

	@Inject
	lateinit var inetChecker: InetChecker

	@Inject
	lateinit var communicator: Communicator


	lateinit var observableIN: Observable<CharSequence>

	lateinit var disposableIn: Disposable

	var isSubscribed: Boolean = false
	var isNewActivity: Boolean = true

	init {
		App.component.inject(this)

	}

	fun startSubscription() {
		if (!isSubscribed && isNewActivity && ::observableIN.isInitialized) {

			disposableIn = observableIN.filter { e -> e.length >= 3 }
					.debounce(800, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
					.subscribe { e -> loadForecast(INITdaysToFORECAST, e.toString()) }
			isSubscribed = true
		} else {
			Log.d("TAG", "already subscribed, passing the method")
		}
	}


	override fun attachView(view: ImvpMainView?) {
		super.attachView(view)
		Log.d("TAG", "attachView")
	}

	override fun detachView(view: ImvpMainView?) {
		super.detachView(view)

		isNewActivity = true
		isSubscribed = false

		if (::disposableIn.isInitialized)
			disposableIn.dispose()

	}

	override fun onFirstViewAttach() {
		super.onFirstViewAttach()
		Log.d("TAG", "onFirstViewAttach")

		//TODO to set some login about first launch?
	}


	// might be used Schedulers.io() from RX to asynch, but now I try to understand couroutines

	fun loadForecast(days: Int, city: String) = async(UI) {
		if (inetChecker.checInternet()) {
			val result = bg {
				communicator.getForecast(days, city)
			}

			val forecastList = WeatherMapConverter().convertResultToForList(city, result.await())

			viewState.onForecastsLoaded(forecastList)

			Log.d("TAG", "was getting from the server: " + forecastList.toString())
		} else Log.d("TAG", "no Internet")
	}

}