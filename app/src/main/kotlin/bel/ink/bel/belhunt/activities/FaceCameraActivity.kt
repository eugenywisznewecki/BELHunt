package bel.ink.bel.belhunt.activities


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.hardware.Camera
import android.hardware.SensorManager
import android.os.Bundle
import android.support.transition.ChangeTransform
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.OrientationEventListener
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import bel.ink.bel.belhunt.R
import bel.ink.bel.belhunt.camViews.GraphicOverlay
import bel.ink.bel.belhunt.cameraSources.Camera1Api
import bel.ink.bel.belhunt.cameraSources.ICameraApi
import bel.ink.bel.belhunt.cameraSources.actionInterfaces.AutoFocusCallback
import bel.ink.bel.belhunt.cameraSources.actionInterfaces.PictureCallback
import bel.ink.bel.belhunt.cameraSources.actionInterfaces.ShutCallback
import bel.ink.bel.belhunt.detectors.FaceGraphic
import bel.ink.bel.belhunt.utilits.dpToPx
import bel.ink.bel.belhunt.viewmodels.CameraViewModel
import com.crashlytics.android.Crashlytics
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import kotlinx.android.synthetic.main.activity_q.*
import timber.log.Timber
import java.io.IOException

@Suppress("DEPRECATION")
class FaceCameraActivity : AppCompatActivity(), View.OnClickListener {


    //INJECTS
    // one interface for both cameras
    private lateinit var camera1Api: ICameraApi

    private lateinit var viewModel: CameraViewModel

    private var autoFocus: Boolean = true
        set(value) {
            field = value
            when (value) {
                true -> camera1Api.setFocus(Camera.Parameters.FOCUS_MODE_AUTO)
                false -> camera1Api.setFocus(Camera.Parameters.FOCUS_MODE_MACRO)
            }
        }
    private var useFlash: Boolean? = false
        set(value) {
            field = value
            if (!isFrontCamera) {
                when (value) {
                    true -> {
                        camera1Api.setFlash(Camera.Parameters.FLASH_MODE_TORCH)
                        flashLightView.setImageResource(R.drawable.flash)
                    }
                    false -> {
                        camera1Api.setFlash(Camera.Parameters.FLASH_MODE_OFF)
                        flashLightView.setImageResource(R.drawable.flash_off)
                    }
                }

            }
        }

    private var isFrontCamera = false


    //object to listen and rotate imageButtons and photos at realtime
    private var rotateDegree = 0F
    val orientationEventListener: OrientationEventListener by lazy {
        object : OrientationEventListener(applicationContext, SensorManager.SENSOR_DELAY_UI) {
            var previousValueOfRotation = -1F
            override fun onOrientationChanged(orientation: Int) {
                when {
                    (orientation in 315..365 || orientation in 0..45) -> {
                        setRotationToElements(0F)
                        rotateDegree = 0F

                    }
                    (orientation in 46..179) -> {
                        setRotationToElements(-90F)
                        rotateDegree = -90F
                    }
                    (orientation in 180..318) -> {
                        setRotationToElements(90F)
                        rotateDegree = 90F
                    }
                }
            }

            fun setRotationToElements(rotation: Float) {
                if (previousValueOfRotation == rotation) {
                    previousValueOfRotation = rotation
                    return
                }
                previousValueOfRotation = rotation
                for (i in 0..constratinLayoutView.childCount - 1) {
                    val changeTransform = ChangeTransform().apply {
                        duration = 600
                        interpolator = DecelerateInterpolator()
                    }
                    TransitionManager.beginDelayedTransition(constratinLayoutView, changeTransform)
                    constratinLayoutView.getChildAt(i).rotation = rotation
                }
            }
        }
    }

    private val previewFaceDetector: FaceDetector by lazy {
        FaceDetector.Builder(applicationContext)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(true)
                .setTrackingEnabled(true)
                .build()


    }
    private lateinit var faceGraphic: FaceGraphic
    private var wasActivityResumed = false

    private val camera1ApiShutCD: ShutCallback by lazy {
        object : ShutCallback {
            override fun onShutter() {
                Toast.makeText(applicationContext, "Picture saved", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val cameraSourcePictureCallback: PictureCallback by lazy {

        object : PictureCallback {
            override fun onCaptureImage(pictureGeted: Bitmap) {
                changeCamView.isEnabled = true
                snapShotView.isEnabled = true
                val rotatedPicture = viewModel.rotatePicture(isFrontCamera, rotateDegree, pictureGeted)
                viewModel.savePicture(rotatedPicture)
            }
        }
    }
    private val cameraPreviewTouchListener by lazy {
        object : View.OnTouchListener {
            override fun onTouch(v: View, motionEvent: MotionEvent): Boolean {
                v.onTouchEvent(motionEvent)

                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    val autoFocusX = (motionEvent.x - dpToPx(60) / 2).toInt()
                    val autoFocusY = (motionEvent.y - dpToPx(60) / 2).toInt()

                    ivAutoFocus.apply {
                        translationX = autoFocusX.toFloat()
                        translationY = autoFocusY.toFloat()
                        visibility = View.VISIBLE
                        bringToFront()
                    }

                    camera1Api.autoFocus(object : AutoFocusCallback {
                        override fun onAutoFocus(success: Boolean) {
                            runOnUiThread { ivAutoFocus.visibility = View.GONE }
                        }
                    })
                }
                return false
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_q)

        for (i in 0 .. constratinLayoutView.childCount-1) {
            constratinLayoutView.getChildAt(i).setOnClickListener(this)
        }


        viewModel = ViewModelProviders.of(this).get(CameraViewModel::class.java)

        viewModel.getFlash().observe(this, Observer { isFl ->
            isFl?.let { useFlash = isFl }
        })

        viewModel.getIsFrontCamera().observe(this, Observer { isFront ->
            isFront?.let { isFrontCamera = isFront }
        })

        viewModel.getFocus().observe(this, Observer { focus ->
            focus?.let { autoFocus = focus }
        })




        createCameraSource()

        preview.setOnTouchListener(cameraPreviewTouchListener)
    }

    override fun onResume() {
        super.onResume()
        if (wasActivityResumed)
            beginCameraVision()

        //TODO disable it later!
        orientationEventListener.enable()

    }

    override fun onPause() {
        super.onPause()
        wasActivityResumed = true
        stopCameraSource()
        orientationEventListener.disable()
    }

    override fun onDestroy() {
        super.onDestroy()

        previewFaceDetector.release()
    }

    private fun createCameraSource() {
        if (previewFaceDetector.isOperational) {
            previewFaceDetector.setProcessor(MultiProcessor.Builder(GraphicFaceTrackerFactory()).build())
        } else {
            Toast.makeText(applicationContext, "FACE DETECTION NOT AVAILABLE", Toast.LENGTH_SHORT).show()
        }
        camera1Api = Camera1Api(applicationContext, previewFaceDetector,
                facing = if (isFrontCamera) Camera1Api.CAMERA_FRONT else Camera1Api.CAMERA_BACK)
        beginCameraVision()

    }


    private fun beginCameraVision() {
        try {
            preview.start(camera1Api, faceOverlay)
        } catch (e: IOException) {
            Timber.e("cannt to start camera 1  $e")
            Crashlytics.log(e.message)
            camera1Api.release()

        }
    }

    private fun stopCameraSource() {
        preview.stop()
    }


    override fun onClick(v: View) {
        when (v) {
            (changeCamView) -> {
                if (isFrontCamera) {
                    isFrontCamera = false
                } else {
                    isFrontCamera = true
                }
                viewModel.setFront(isFrontCamera)
                stopCameraSource()
                createCameraSource()
            }
            (galleryButtonView) -> {
                viewModel.routeToMain(this)
                finish()
            }
            (snapShotView) -> {
                changeCamView.isEnabled = false

                snapShotView.isEnabled = false
                if (this::camera1Api.isInitialized)
                    camera1Api.capturePicture(camera1ApiShutCD, cameraSourcePictureCallback)

            }
            (flashLightView) -> {
                when (useFlash) {
                    (true) -> {
                        useFlash = false

                        viewModel.setFlash(false)
                        /*camera1Api.setFlash(Camera.Parameters.FLASH_MODE_OFF)*/
                    }
                    (false) -> {
                        useFlash = true

                        viewModel.setFlash(true)
                        /* camera1Api.setFlash(Camera.Parameters.FLASH_MODE_TORCH)*/
                    }
                }
            }
            (qrImageView) -> {
                viewModel.routeToQR(this)
                finish()
            }
        }
    }

    //Insjs - classes - don't touch it
    private inner class GraphicFaceTrackerFactory : MultiProcessor.Factory<Face> {
        override fun create(face: Face): Tracker<Face> {
            //WARN
            return GraphicFaceTracker(faceOverlay as GraphicOverlay<FaceGraphic>)
        }
    }

    private inner class GraphicFaceTracker(private val overlayTracker: GraphicOverlay<FaceGraphic>) : Tracker<Face>() {

        init {
            faceGraphic = FaceGraphic(overlayTracker)
        }

        override fun onNewItem(p0: Int, p1: Face?) {
            super.onNewItem(p0, p1)

            faceGraphic.setId(p0)
        }

        override fun onMissing(p0: Detector.Detections<Face>?) {
            super.onMissing(p0)
            faceGraphic.goneFace()
            overlayTracker.remove(faceGraphic)
        }

        override fun onUpdate(p0: Detector.Detections<Face>?, p1: Face) {
            super.onUpdate(p0, p1)

            overlayTracker.add(faceGraphic)
            faceGraphic.updateFace(p1)
        }

        override fun onDone() {
            super.onDone()
            faceGraphic.goneFace()
            overlayTracker.remove(faceGraphic)
        }

    }

}
