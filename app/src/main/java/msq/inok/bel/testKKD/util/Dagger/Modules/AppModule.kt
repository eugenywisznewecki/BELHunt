package msq.inok.bel.testKKD.util.Dagger.Modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import msq.inok.bel.testKKD.data.ApplicationSettings
import msq.inok.bel.testKKD.serverApi.Communicator
import msq.inok.bel.testKKD.util.BadWeatherGuard
import msq.inok.bel.testKKD.util.WifiChecker
import javax.inject.Singleton

/**
 * Created by User on 13.01.2018.
 */
@Module
class AppModule {
	constructor(app: Application){
		this.context = app
	}

	private val context: Context

	@Provides
	fun providesContext(): Context = context

	@Provides
	@Singleton
	fun provideApplicationSettings(context: Context): ApplicationSettings =
			ApplicationSettings(context)

	@Provides
	@Singleton
	fun provideCommunicator(context: Context): Communicator = Communicator(context)

	@Provides
	@Singleton
	fun provideWifiChecker(context: Context): WifiChecker = WifiChecker(context)

	@Provides
	@Singleton
	fun provideBadWeatherGuard(context: Context): BadWeatherGuard = BadWeatherGuard(context)

}