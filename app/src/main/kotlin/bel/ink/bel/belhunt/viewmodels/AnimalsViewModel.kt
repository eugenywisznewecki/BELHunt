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
                Animal(images[0],"Кабан", "description111"),
                Animal(images[1],"Лось", "description222"),
                Animal(images[2],"Олень", "description333"),
                Animal(images[3],"Лесная куница", "description111"),
                Animal(images[4],"Волк", "description222"),
                Animal(images[5],"Белка", "description333"),
                Animal(images[6],"Хорек", "description111"),
                Animal(images[7],"Каменная куница", "description222"),
                Animal(images[8],"Американская норка", "description333"),
                Animal(images[9],"Заяц русак", "description111"),
                Animal(images[10],"Косуля", "description222"),
                Animal(images[11],"Заяц беляк", "description333"),
                Animal(images[12],"Бобр", "description111"),
                Animal(images[13],"Ондатра", "description222"),
                Animal(images[14],"Лисица", "description333")
                )

            liveListPhotos.value = animalList

    }


}