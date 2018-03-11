package msq.inok.bel.belhunt.mvp.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import msq.inok.bel.belhunt.entities.ForecastList

/**
 * Created by User on 09.03.2018.
 */
@StateStrategyType(value = AddToEndSingleStrategy::class)
interface ImvpMainView : MvpView {

	fun onForecastsLoaded(forecastList: ForecastList?)

}