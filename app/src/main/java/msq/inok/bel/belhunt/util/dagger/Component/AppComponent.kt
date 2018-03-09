package msq.inok.bel.belhunt.util.dagger.Component

import dagger.Component
import msq.inok.bel.belhunt.services.WeatherIService
import msq.inok.bel.belhunt.ui.activities.MainActivity
import msq.inok.bel.belhunt.ui.activities.StartActivity
import msq.inok.bel.belhunt.util.dagger.Modules.AppModule
import javax.inject.Singleton

/**
 * Created by User on 13.01.2018.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

	fun inject(activity: StartActivity)

	fun inject(activity: MainActivity)

	fun inject(intentService: WeatherIService)

}