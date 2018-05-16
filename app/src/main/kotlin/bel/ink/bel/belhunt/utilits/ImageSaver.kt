package bel.ink.bel.belhunt.utilits

import android.content.Context
import android.graphics.Bitmap
import com.crashlytics.android.Crashlytics
import timber.log.Timber
import java.io.FileOutputStream
import java.io.IOException


//INJECT THIS TODO
// and do for both camera Api
internal class ImageSaver(private val pic: Bitmap,
                          private val contextIn: Context

) : Runnable {

    val QUALITY_OF_PHOTO = 95
    lateinit var out: FileOutputStream

    override fun run() {

        val path = AppRouter.PATH_PHOTO_DIRECTORY
        if (!path.exists())
            path.mkdirs()

        val photofileName = PhotoNamer().photoName

        try {
            out = FileOutputStream(photofileName)
            pic.compress(Bitmap.CompressFormat.JPEG, QUALITY_OF_PHOTO, out)
            Timber.d("++ file written $photofileName")
        } catch (e: Exception) {
            Crashlytics.logException(e)
            e.printStackTrace()
            Crashlytics.logException(e)
        } finally {
            try {
                if (this::out.isInitialized) {
                    out.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        MediaRegistrator(contextIn).registerInSystem(photofileName)
    }
}


