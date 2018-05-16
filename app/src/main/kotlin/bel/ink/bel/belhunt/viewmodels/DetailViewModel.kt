package bel.ink.bel.belhunt.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import bel.ink.bel.belhunt.utilits.AppRouter

class DetailViewModel(app: Application) : AndroidViewModel(app) {

    private val router: AppRouter by lazy { AppRouter(app.applicationContext) }


    internal fun share(images: String) {
        router.sharePhoto(images)
    }


}