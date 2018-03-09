package msq.inok.bel.belhunt.util.dagger.Modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import msq.inok.bel.belhunt.data.ApplicationSettings
import msq.inok.bel.belhunt.serverApi.Communicator
import msq.inok.bel.belhunt.util.BadWeatherGuard
import msq.inok.bel.belhunt.util.InetChecker
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
	fun provideWifiChecker(context: Context): InetChecker = InetChecker(context)

	@Provides
	@Singleton
	fun provideBadWeatherGuard(context: Context): BadWeatherGuard = BadWeatherGuard(context)

}