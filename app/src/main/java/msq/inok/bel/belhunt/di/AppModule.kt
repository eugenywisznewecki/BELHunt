package msq.inok.bel.belhunt.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import msq.inok.bel.belhunt.checkers.InetChecker
import msq.inok.bel.belhunt.data.ApplicationSettings
import msq.inok.bel.belhunt.mvp.presenters.Presenter
import msq.inok.bel.belhunt.serverApi.Communicator
import msq.inok.bel.belhunt.serverApi.WeatherAPI
import msq.inok.bel.belhunt.util.BASE_URL
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class AppModule {

	constructor(app: Application) {
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
	fun provideCommunicator(context: Context, retrofit: Retrofit): Communicator = Communicator(context)

	@Provides
	@Singleton
	fun provideWifiChecker(context: Context): InetChecker = InetChecker(context)


	@Provides
	@Singleton
	fun provideRetrofit(url: String) = Retrofit.Builder()
	.baseUrl(url)
	.addConverterFactory(GsonConverterFactory.create())
	.build()

	@Provides
	fun provideURL() = BASE_URL

	@Provides
	@Singleton
	fun provideWeatherAPI(retrofit: Retrofit) = retrofit.create(WeatherAPI::class.java)


}