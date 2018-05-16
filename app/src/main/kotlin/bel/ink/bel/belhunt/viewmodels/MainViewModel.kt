package bel.ink.bel.belhunt.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import bel.ink.bel.belhunt.utilits.AppRouter
import bel.ink.bel.belhunt.utilits.GooglePlayChecker
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.io.File
import java.io.IOException

class MainViewModel(val applic: Application) : AndroidViewModel(applic) {

    private val router by lazy { AppRouter(applic.applicationContext) }
    private val authFB: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    var counRows: Int = 3
        set(value) {
            field = value
            liveCoutRows.value = value
        }

    lateinit var liveCoutRows: MutableLiveData<Int>
    fun getCoutRows(): MutableLiveData<Int> {
        if (!this::liveCoutRows.isInitialized)
            liveCoutRows = MutableLiveData<Int>()
        return liveCoutRows
    }


    lateinit var liveListPhotos: MutableLiveData<List<File>>

    fun getLiveLisststPhotos(): MutableLiveData<List<File>> {
        if (!this::liveListPhotos.isInitialized) {
            liveListPhotos = MutableLiveData<List<File>>()
            getListPath()
        }

        return liveListPhotos
    }


    fun getListPath() {

        val photoFolder = AppRouter.PATH_PHOTO_DIRECTORY

        try {
            if (!photoFolder.exists()) {
                photoFolder.mkdirs()

            }

            val listPhotos = photoFolder.listFiles().toList()
            liveListPhotos.value = listPhotos

        } catch (e: IOException) {
            Crashlytics.log(e.message)
            Timber.d("++ ${e.message}")
        }
    }

    fun startCameraActivity() {
        router.openCameraFromViewModel()
    }

    fun logout() {
        authFB.signOut()

        router.openLoginActivityFromViewModel()
    }

    fun isPlayMarker() = GooglePlayChecker(applic.applicationContext).checkGooglePlay()


}