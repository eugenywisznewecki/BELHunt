package msq.inok.bel.belhunt.di

import dagger.Component
import msq.inok.bel.belhunt.presentation.presenters.Presenter
import msq.inok.bel.belhunt.services.WeatherIService
import msq.inok.bel.belhunt.ui.activities.StartActivity
import javax.inject.Singleton

/**
 * Created by User on 13.01.2018.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

	fun inject(activity: StartActivity)

	fun inject(intentService: WeatherIService)

	fun inject(presenter: Presenter)

}