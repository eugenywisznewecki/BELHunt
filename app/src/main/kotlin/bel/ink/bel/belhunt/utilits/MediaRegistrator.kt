package bel.ink.bel.belhunt.utilits

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

class MediaRegistrator(private val contextIn: Context) {
    fun registerInSystem(photoFileName: String) {
        val intentMediaStored = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intentMediaStored.setData(Uri.fromFile(File(photoFileName)))
        contextIn.sendBroadcast(intentMediaStored)
    }
}