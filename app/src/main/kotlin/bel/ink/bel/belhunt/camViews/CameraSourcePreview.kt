package bel.ink.bel.belhunt.camViews

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.view.ViewGroup
import bel.ink.bel.belhunt.cameraSources.Camera1Api
import bel.ink.bel.belhunt.cameraSources.ICameraApi
import com.crashlytics.android.Crashlytics
import bel.ink.bel.belhunt.utilits.getScreenHeight
import bel.ink.bel.belhunt.utilits.getScreenRotation
import bel.ink.bel.belhunt.utilits.getScreenWidth
import timber.log.Timber
import java.io.IOException

class CameraSourcePreview : ViewGroup {


    private var surfaceView: SurfaceView
    private var mAutoFitTextureView: AutoFitTextureView
    private var usingCameraOne: Boolean = false
    private var startRequested: Boolean = false
    private var surfaceAvailable: Boolean = false
    private var viewAdded = false


    private lateinit var cameraApi1: Camera1Api
    /*   private lateinit var mCamera2Api: ICameraApi*/


    private lateinit var overlay: GraphicOverlay<*>
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var screenRotation: Int = 0

    private val surfaceViewListener = object : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder) {
            surfaceAvailable = true
            overlay.bringToFront()
            try {
                startIfReady()
            } catch (e: IOException) {
                Timber.e("Could not start camera source. ++ " + e.message)
            }
        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            surfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    }

    private val mSurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            surfaceAvailable = true
            overlay.bringToFront()
            try {
                startIfReady()
            } catch (e: IOException) {
                Timber.e("Can't start camera $e")
                Crashlytics.log(e.message)
            }

        }

        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {}
        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
            surfaceAvailable = false
            return true
        }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
    }

    constructor(context: Context) : super(context) {
        screenHeight = getScreenHeight(context)
        screenWidth = getScreenWidth(context)
        screenRotation = getScreenRotation(context)
        startRequested = false
        surfaceAvailable = false
        surfaceView = SurfaceView(context)
        surfaceView!!.holder.addCallback(surfaceViewListener)
        mAutoFitTextureView = AutoFitTextureView(context)
        mAutoFitTextureView!!.surfaceTextureListener = mSurfaceTextureListener
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        screenHeight = getScreenHeight(context)
        screenWidth = getScreenWidth(context)
        screenRotation = getScreenRotation(context)
        startRequested = false
        surfaceAvailable = false
        surfaceView = SurfaceView(context)
        surfaceView!!.holder.addCallback(surfaceViewListener)
        mAutoFitTextureView = AutoFitTextureView(context)
        mAutoFitTextureView!!.surfaceTextureListener = mSurfaceTextureListener
    }

    @Throws(IOException::class)
    fun start(cameraSource: ICameraApi, overlay: GraphicOverlay<*>) {
        usingCameraOne = true
        this.overlay = overlay
        start(cameraSource)
    }


    @Throws(IOException::class)
    private fun start(cameraSourceInteface: ICameraApi?) {

        if (cameraSourceInteface is Camera1Api) {
            cameraApi1 = cameraSourceInteface as Camera1Api
            startRequested = true
            if (!viewAdded) {
                addView(surfaceView)
                viewAdded = true
            }
            try {
                startIfReady()
            } catch (e: IOException) {
                Timber.e("++ Could not start camera source." + e.message)
                Crashlytics.log(e.message)
            }
        } else {
            stop()
        }
    }


    fun stop() {
        startRequested = false

        if (this::cameraApi1.isInitialized) {
            cameraApi1.stop()
        }
    }

    @Throws(IOException::class)
    private fun startIfReady() {
        if (startRequested && surfaceAvailable) {
            try {
                cameraApi1.start(surfaceView.holder)
                if (this::overlay.isInitialized) {
                    val size = cameraApi1.previewSize
                    if (size != null) {
                        val min = Math.min(size.width, size.height)
                        val max = Math.max(size.width, size.height)
                        overlay.setCameraInfo(min / 4, max / 4, cameraApi1!!.facing)
                        overlay.clear()
                    } else {
                        stop()
                    }
                }
                startRequested = false

            } catch (e: SecurityException) {
                Timber.d("SECURITY EXCEPTION: $e")
                Crashlytics.log(e.message)
            }

        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var height = 720
        if (this::cameraApi1.isInitialized) {
            val size = cameraApi1.previewSize
            //доходим до этого
            size?.let {
                height = it.width
            }
        }

        val newWidth = height * screenWidth / screenHeight
        val layoutWidth = right - left
        val layoutHeight = bottom - top
        var childWidth = layoutWidth
        var childHeight = (layoutWidth.toFloat() / newWidth.toFloat() * height).toInt()
        if (childHeight > layoutHeight) {
            childHeight = layoutHeight
            childWidth = (layoutHeight.toFloat() / height.toFloat() * newWidth).toInt()
        }
        for (i in 0 until childCount) {
            getChildAt(i).layout(0, 0, childWidth, childHeight)
        }
        try {
            startIfReady()
        } catch (e: IOException) {
            Timber.e("Could not start camera source. $e")
            Crashlytics.log(e.message)
        }

    }
}


