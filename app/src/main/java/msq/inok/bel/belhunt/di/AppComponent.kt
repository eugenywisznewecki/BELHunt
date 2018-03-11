package msq.inok.bel.belhunt.di

import dagger.Component
import msq.inok.bel.belhunt.mvp.presenters.Presenter
import msq.inok.bel.belhunt.serverApi.Communicator

import msq.inok.bel.belhunt.ui.activities.StartActivity
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

	fun inject(activity: StartActivity)

	fun inject(presenter: Presenter)

	fun inject(communicator: Communicator)

}