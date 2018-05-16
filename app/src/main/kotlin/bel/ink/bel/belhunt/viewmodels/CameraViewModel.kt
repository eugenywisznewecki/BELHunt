package bel.ink.bel.belhunt.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.graphics.Matrix
import android.support.v7.app.AppCompatActivity
import bel.ink.bel.belhunt.utilits.AppRouter
import bel.ink.bel.belhunt.utilits.ImageSaver

class CameraViewModel(val applic: Application) : AndroidViewModel(applic) {


    val router: AppRouter by lazy { AppRouter(applic.applicationContext) }


    lateinit var isAutoFocus: MutableLiveData<Boolean>
    lateinit var isAutoFlash: MutableLiveData<Boolean>
    lateinit var isFrontLive: MutableLiveData<Boolean>


    fun setFront(bool: Boolean) {
        if (this::isFrontLive.isInitialized) {
            isFrontLive.value = bool
        } else {
            isFrontLive = MutableLiveData<Boolean>()
            isFrontLive.value = bool
        }
    }

    fun getIsFrontCamera(): MutableLiveData<Boolean> {
        if (!this::isFrontLive.isInitialized) {
            isFrontLive = MutableLiveData<Boolean>()

        }
        return isFrontLive
    }


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

    internal fun routeToMain(activity: AppCompatActivity) {
        router.openMainGalery(activity = activity)
    }

    internal fun routeToQR(activity: AppCompatActivity) {
        router.openQRScannerActivity(activity = activity)
    }


    internal fun rotatePicture(isFrontCamera: Boolean, rotation: Float, pic: Bitmap): Bitmap {
        val matrix = Matrix()
        if (isFrontCamera) {
            matrix.postRotate(rotation)
            return Bitmap.createBitmap(pic, 0, 0, pic.width, pic.height,
                    matrix, true)
        } else {
            matrix.postRotate(-rotation)
            return Bitmap.createBitmap(pic, 0, 0, pic.width, pic.height,
                    matrix, true)
        }
    }

    internal fun savePicture(picture: Bitmap) {

        ImageSaver(picture, applic.applicationContext).run()
    }


}