package msq.inok.bel.belhunt

import android.app.Application
import msq.inok.bel.belhunt.di.AppComponent
import com.facebook.stetho.Stetho
import msq.inok.bel.belhunt.di.DaggerAppComponent
import msq.inok.bel.belhunt.di.AppModule


class App: Application(){

	companion object {
		lateinit var component: AppComponent
	}

	override fun onCreate() {
		super.onCreate()

		//stetho
		val inicializerBuilder = Stetho.newInitializerBuilder(this);
		inicializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
		inicializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
		val inicializer = inicializerBuilder.build();
		Stetho.initialize(inicializer)

		component = builComponent()

	}

	private fun builComponent(): AppComponent {
		return DaggerAppComponent.builder()
				.appModule(AppModule(this))
				.build()
	}
}
