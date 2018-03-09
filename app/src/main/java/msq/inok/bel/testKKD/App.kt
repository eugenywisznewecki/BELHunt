package msq.inok.bel.testKKD

import android.app.Application
import msq.inok.bel.testKKD.util.Dagger.Component.AppComponent
import com.facebook.stetho.Stetho
import msq.inok.bel.testKKD.util.Dagger.Component.DaggerAppComponent
import msq.inok.bel.testKKD.util.Dagger.Modules.AppModule


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
				.build()// name DaggerYOURCLASSNAME_COMPONENT, COMPILE TIME
	}
}
