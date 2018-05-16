package bel.ink.bel.belhunt.cameraSources

import android.view.SurfaceHolder
import bel.ink.bel.belhunt.cameraSources.actionInterfaces.AutoFocusCallback
import bel.ink.bel.belhunt.cameraSources.actionInterfaces.PictureCallback
import bel.ink.bel.belhunt.cameraSources.actionInterfaces.ShutCallback

//NOT USING CAMERA2 because of
//unstability work-processes on different device-types
// and crashes
interface ICameraApi {

    /* fun start(textureView: AutoFitTextureView, displayOrientation: Int)
 */
    fun start(surfaceHolder: SurfaceHolder): ICameraApi

    fun capturePicture(shut: ShutCallback, picCallback: PictureCallback)

    fun setFlash(flashMode: String): Boolean

    fun setFocus(focusMode: String): Boolean

    fun stop()

    fun release()

    fun doZoom(scale: Float): Int

    fun autoFocus(autoFocus: AutoFocusCallback): Unit

}