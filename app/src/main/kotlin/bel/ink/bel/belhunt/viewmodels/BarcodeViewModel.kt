package bel.ink.bel.belhunt.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.support.v7.app.AppCompatActivity
import bel.ink.bel.belhunt.utilits.AppRouter

//Inject this
class BarcodeViewModel(application: Application/*,
                       private var router: AppRouter*/) : AndroidViewModel(application) {

    val router: AppRouter by lazy { AppRouter(application.applicationContext) }



    lateinit var isAutoFocus: MutableLiveData<Boolean>
    lateinit var isAutoFlash: MutableLiveData<Boolean>


    var userFocus: Boolean = false

    /* var userFlash: Boolean = true
         set(value) {
             field = value
             isAutoFlash.value = value
         }*/

    fun getFocus(): MutableLiveData<Boolean> {
        if (!this::isAutoFlash.isInitialized) {
            isAutoFlash = MutableLiveData<Boolean>()

        }
        return isAutoFlash
    }

    fun getFlash(): MutableLiveData<Boolean> {
        if (!this::isAutoFocus.isInitialized) {
            isAutoFocus = MutableLiveData<Boolean>()
        }

        return isAutoFocus
    }

    fun setFlash(bo: Boolean) {
        if (this::isAutoFlash.isInitialized) {
            isAutoFlash.value = bo


        } else {
            isAutoFlash = MutableLiveData<Boolean>()
            isAutoFlash.value = bo

        }

    }

    fun setFocus(bo: Boolean) {
        if (this::isAutoFocus.isInitialized) {
            isAutoFocus.value = bo

        } else {
            isAutoFocus = MutableLiveData<Boolean>()
            isAutoFocus.value = bo

        }
    }

    internal fun route(activity: AppCompatActivity) {
        router.openMainGalery(activity)
    }


}