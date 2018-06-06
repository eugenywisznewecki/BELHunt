package bel.ink.bel.belhunt.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import bel.ink.bel.belhunt.entities.Animal
import bel.ink.bel.belhunt.utilits.AppRouter
import com.crashlytics.android.Crashlytics
import msq.inok.bel.belhunt.util.ANIMAL_PHOTO_PATH
import timber.log.Timber
import java.io.File
import java.io.IOException

class AnimalsViewModel(val applic: Application) : AndroidViewModel(applic) {


    private val router by lazy { AppRouter(applic.applicationContext) }

    lateinit var liveListPhotos: MutableLiveData<List<Animal>>

    fun getLiveLisststAnimals(): MutableLiveData<List<Animal>> {
        if (!this::liveListPhotos.isInitialized) {
            liveListPhotos = MutableLiveData<List<Animal>>()
            getListPath()
        }

        return liveListPhotos
    }


    fun getListPath() {

        val images = applic.assets.list(ANIMAL_PHOTO_PATH)


        Timber.d(images[0].toString())
        val animalList = listOf(
                Animal(images[0],"Deer", "description111"),
                Animal(images[1],"Deer2", "description222"),
                Animal(images[1],"Deer3", "description333")
                )

            liveListPhotos.value = animalList

    }


}