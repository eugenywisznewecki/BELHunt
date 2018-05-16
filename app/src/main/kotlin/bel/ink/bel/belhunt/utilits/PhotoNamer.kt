package bel.ink.bel.belhunt.utilits

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PhotoNamer() {

    val pathRouter by lazy { AppRouter.PATH_PHOTO_DIRECTORY }


    public val photoName: String
        get() {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val prepend = "photo_" + timeStamp + "_"
            val file = File.createTempFile(prepend, ".jpg", pathRouter)
            val photofileName = file.absolutePath
            return photofileName
        }


}