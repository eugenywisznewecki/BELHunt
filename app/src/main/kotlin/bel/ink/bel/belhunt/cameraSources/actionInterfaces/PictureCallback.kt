package bel.ink.bel.belhunt.cameraSources.actionInterfaces

import android.graphics.Bitmap

interface PictureCallback {
    fun onCaptureImage(pictureGeted: Bitmap)
}