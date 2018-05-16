package bel.ink.bel.belhunt

import android.app.Application
import android.os.Build
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import io.fabric.sdk.android.Fabric
import msq.inok.bel.belhunt.di.AppComponent
import msq.inok.bel.belhunt.di.AppModule
import msq.inok.bel.belhunt.di.DaggerAppComponent
import timber.log.Timber


class App : Application() {

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = builComponent()


        //RELEASE <-> DEBUG
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Fabric.with(this, Crashlytics(), Answers())
        } else {
            Fabric.with(this, Crashlytics(), Answers())
        }
    }

    private fun builComponent(): AppComponent {
        return DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }


    private fun is2SupportedApi() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP


}