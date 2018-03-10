package msq.inok.bel.belhunt.presentation.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import io.reactivex.Observable
import msq.inok.bel.belhunt.entities.ForecastList

/**
 * Created by User on 09.03.2018.
 */
@StateStrategyType(value = AddToEndSingleStrategy::class)
interface ImvpMainView: MvpView {

	fun onForecastsLoaded(forecastList: ForecastList)

	fun onShowErrorMessage(message: String)

	fun onShowForecastList()

	fun updateDataUI()

/*
	//TODO
	fun onEditTextEntered()
*/



}