package msq.inok.bel.belhunt.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import msq.inok.bel.belhunt.data.ApplicationSettings
import msq.inok.bel.belhunt.mvp.presenters.Presenter
import msq.inok.bel.belhunt.serverApi.Communicator
import msq.inok.bel.belhunt.checkers.BadWeatherGuard
import msq.inok.bel.belhunt.checkers.InetChecker
import javax.inject.Singleton

@Module
class AppModule {

	constructor(app: Application){
		this.context = app
	}

	private val context: Context

	@Provides
	@Singleton
	fun providePresenter(): Presenter = Presenter()

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