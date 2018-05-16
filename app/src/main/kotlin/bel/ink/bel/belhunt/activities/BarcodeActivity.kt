/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bel.ink.bel.belhunt.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.hardware.Camera
import android.hardware.SensorManager
import android.os.Bundle
import android.support.transition.ChangeTransform
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.view.animation.DecelerateInterpolator
import bel.ink.bel.belhunt.R
import bel.ink.bel.belhunt.camViews.GraphicOverlay
import bel.ink.bel.belhunt.cameraSources.Camera1Api
import bel.ink.bel.belhunt.cameraSources.ICameraApi
import bel.ink.bel.belhunt.detectors.BarcodeGraphic
import bel.ink.bel.belhunt.detectors.BarcodeGraphicTracker
import bel.ink.bel.belhunt.detectors.BarcodeTrackerFactory
import bel.ink.bel.belhunt.viewmodels.BarcodeViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_barcode.*
import msq.inok.bel.belhunt.util.RC_HANDLE_GMS
import timber.log.Timber
import java.io.IOException

@Suppress("DEPRECATION")
class BarcodeActivity : AppCompatActivity(), BarcodeGraphicTracker.BarcodeUpdateListener, View.OnClickListener {


    private lateinit var cameraApi: ICameraApi

    //some troubles with casting thatswhy not kotlin android extensions
    private lateinit var graphicOverlayBar: GraphicOverlay<BarcodeGraphic>
    private lateinit var barcodeViewMode: BarcodeViewModel

    private var autoFocus: Boolean? = true
        set(value) {
            field = value
            when (value) {
                true -> cameraApi.setFocus(Camera.Parameters.FOCUS_MODE_AUTO)
                false -> cameraApi.setFocus(Camera.Parameters.FOCUS_MODE_MACRO)
            }
        }

    private var useFlash: Boolean? = false
        set(value) {
            field = value

            when (value) {
                true -> {
                    cameraApi.setFlash(Camera.Parameters.FLASH_MODE_TORCH)
                    flashLightViewBarcode.setImageResource(R.drawable.flash)

                }
                false -> {
                    cameraApi.setFlash(Camera.Parameters.FLASH_MODE_OFF)
                    flashLightViewBarcode.setImageResource(R.drawable.flash_off)
                }
            }

        }

    private val scaleGestureDetector: ScaleGestureDetector by lazy {
        ScaleGestureDetector(this, ScaleListener())
    }
    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(this, CaptureGestureListener())
    }


    val orientationEventListener: OrientationEventListener by lazy {
        object : OrientationEventListener(applicationContext, SensorManager.SENSOR_DELAY_UI) {
            var previousValueOfRotation = -1F

            override fun onOrientationChanged(orientation: Int) {
                when {
                    (orientation in 315..365 || orientation in 0..45) -> {
                        setRotationToElements(0F)

                    }
                    (orientation in 46..179) -> {
                        setRotationToElements(-90F)
                    }
                    (orientation in 180..318) -> {
                        setRotationToElements(90F)
                    }
                }
            }

            fun setRotationToElements(rotation: Float) {
                if (previousValueOfRotation == rotation) {
                    previousValueOfRotation = rotation
                    return
                }
                previousValueOfRotation = rotation
                for (i in 0..constratinLayoutViewBarcode.childCount - 1) {
                    val changeTransform = ChangeTransform().apply {
                        duration = 600
                        interpolator = DecelerateInterpolator()
                    }
                    TransitionManager.beginDelayedTransition(constratinLayoutViewBarcode, changeTransform)
                    constratinLayoutViewBarcode.getChildAt(i).rotation = rotation
                }
            }
        }
    }


    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_barcode)
        orientationEventListener.enable()
        //some troubles with castion thatswhy not kotlin android extensions
        graphicOverlayBar = findViewById(R.id.graphicOverlayBarcode) as GraphicOverlay<BarcodeGraphic>
        flashLightViewBarcode.setOnClickListener(this)
        galleryButtonViewBarcode.setOnClickListener(this)

        barcodeViewMode = ViewModelProviders.of(this).get(BarcodeViewModel::class.java)
        barcodeViewMode.getFlash().observe(this,
                object : Observer<Boolean> {
                    override fun onChanged(t: Boolean?) {
                        useFlash = t
                        Timber.d("++ new boolean $t")
                    }
                })
        barcodeViewMode.getFocus().observe(this, Observer { focus ->
            autoFocus = focus
        })



        createCameraSource(autoFocus!!, useFlash!!)
    }


    override fun onTouchEvent(e: MotionEvent) = scaleGestureDetector.onTouchEvent(e) || gestureDetector.onTouchEvent(e) || super.onTouchEvent(e)

    private fun createCameraSource(autoFocus: Boolean, useFlash: Boolean) {

        val barcodeDetector = BarcodeDetector.Builder(applicationContext).build()

        graphicOverlayBarcode.let {
            val barcodeFactory = BarcodeTrackerFactory(it as GraphicOverlay<BarcodeGraphic>, this)
            barcodeDetector.setProcessor(
                    MultiProcessor.Builder(barcodeFactory).build())
        }

        cameraApi = Camera1Api(applicationContext, barcodeDetector, facing = Camera1Api.CAMERA_BACK,
                mRequestedPreviewWidth = 1600, mRequestedPreviewHeight = 1024, fps = 15f,
                focusMode = if (autoFocus) Camera.Parameters.FOCUS_MODE_AUTO else Camera.Parameters.FOCUS_MODE_FIXED,
                flashMode = if (useFlash) Camera.Parameters.FLASH_MODE_TORCH else Camera.Parameters.FLASH_MODE_OFF)
        // автофокус вообщее надо?? TODO
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        previewBarcode.stop()
        orientationEventListener.disable()
    }


    private fun startCameraSource() {
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS)
            dlg.show()
        }

        if (this::cameraApi.isInitialized) {
            try {
                previewBarcode.start(cameraApi, graphicOverlayBarcode)
            } catch (e: IOException) {
                Timber.e("Unable to start camera source. $e")
                cameraApi.release()
            }
        }
    }


    private fun onTap(rawX: Float, rawY: Float): Boolean {
        val location = IntArray(2)
        graphicOverlayBarcode.getLocationOnScreen(location)
        val x = (rawX - location[0]) / graphicOverlayBarcode.widthScaleFactor
        val y = (rawY - location[1]) / graphicOverlayBarcode.heightScaleFactor

        var best: Barcode? = null
        var bestDistance = java.lang.Float.MAX_VALUE


        for (vs: BarcodeGraphic in graphicOverlayBar.graphics) {
            val barcode = vs.barcode

            if (barcode.boundingBox.contains(x.toInt(), y.toInt())) {
                best = barcode
                break
            }
            val dx = x - barcode.boundingBox.centerX()
            val dy = y - barcode.boundingBox.centerY()
            val distance = dx * dx + dy * dy
            if (distance < bestDistance) {
                best = barcode
                bestDistance = distance
            }
        }

        if (best != null) {
            //to do smth with barcode
            //TODO
            finish()
            return true
        }
        return false
    }

    private inner class CaptureGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return onTap(e.rawX, e.rawY) || super.onSingleTapConfirmed(e)
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.OnScaleGestureListener {


        override fun onScale(detector: ScaleGestureDetector): Boolean {
            return false
        }


        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }


        override fun onScaleEnd(detector: ScaleGestureDetector) {
            cameraApi.doZoom(detector.scaleFactor)
        }
    }

    override fun onBarcodeDetected(barcode: Barcode) {
        barcode.rawValue
        //do something with barcode data returned
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onClick(v: View) {
        when (v) {
            (flashLightViewBarcode) -> {
                when (useFlash) {
                    (true) -> {
                        useFlash = false
                        flashLightViewBarcode.setImageResource(R.drawable.flash_off)
                        barcodeViewMode.setFlash(false)
                        cameraApi.setFlash(Camera.Parameters.FLASH_MODE_OFF)
                    }
                    (false) -> {
                        useFlash = true
                        flashLightViewBarcode.setImageResource(R.drawable.flash)
                        barcodeViewMode.setFlash(true)
                        cameraApi.setFlash(Camera.Parameters.FLASH_MODE_TORCH)
                    }
                }
            }
            (galleryButtonViewBarcode) -> {
                barcodeViewMode.route(this)
                finish()
            }
        }
    }
}
