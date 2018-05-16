package msq.inok.bel.belhunt.di

import android.app.Application
import android.content.Context
import bel.ink.bel.belhunt.utilits.NetChecker
import bel.ink.bel.belhunt.weatherPackage.weatherServerApi.Communicator
import bel.ink.bel.belhunt.weatherPackage.weatherServerApi.WeatherAPI
import dagger.Module
import dagger.Provides
import msq.inok.bel.belhunt.util.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/*
Module for this project did errors at the last hours
before dead-line, thatswhy I deleted it almost at all
to improve at least app-work
*/


@Module
class AppModule {


    constructor(app: Application) {
        this.context = app
    }

    private val context: Context

    @Provides
    fun providesContext(): Context = context

    @Provides
    @Singleton
    fun provideCommunicator(context: Context): Communicator = Communicator(context)

    @Provides
    @Singleton
    fun provideWifiChecker(context: Context): NetChecker = NetChecker(context)

    @Provides
    @Singleton
    fun provideUrl() = BASE_URL

    @Provides
    @Singleton
    fun provideRetrofit(url: String) = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideWeatherAPI(retrofit: Retrofit) = retrofit.create(WeatherAPI::class.java)


}