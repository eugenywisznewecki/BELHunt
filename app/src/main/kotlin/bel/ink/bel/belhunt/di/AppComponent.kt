package msq.inok.bel.belhunt.di

import bel.ink.bel.belhunt.activities.WeatherActivity
import bel.ink.bel.belhunt.viewmodels.WeatherViewModel
import bel.ink.bel.belhunt.weatherPackage.weatherServerApi.Communicator
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(activity: WeatherActivity)

    fun inject(viewModel: WeatherViewModel)

    fun inject(communicator: Communicator)

}