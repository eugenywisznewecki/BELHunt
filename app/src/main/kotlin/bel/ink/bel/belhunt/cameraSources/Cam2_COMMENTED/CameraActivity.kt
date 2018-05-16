/*
package bel.ink.bel.belhunt.ui.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.Toast
import bel.ink.bel.belhunt.R
import bel.ink.bel.belhunt.model.ImageSaver
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_camera.*
import msq.inok.bel.belhunt.util.*
import timber.log.Timber
import java.io.File
import java.io.IOException


///CAMERA 2 API
internal class CameraActivity : AppCompatActivity() {

    //бар код сканнер
    private val detector: BarcodeDetector by lazy {
        BarcodeDetector.Builder(applicationContext).setBarcodeFormats(Barcode.ALL_FORMATS).build()
    }

    private val barcodeProcessor = detector.setProcessor(object : Detector.Processor<Barcode> {
        override fun release() {

        }

        //это фоновый поток
        override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
            val barcodes = detections?.detectedItems
            if (barcodes != null && barcodes.size() > 0) {
                Timber.d("barcode detector = ")
                textView.post {
                    textView.text = barcodes.valueAt(0).displayValue.toString()
                }
            }
        }
    })


    private val ORIENTATIONS: SparseIntArray = SparseIntArray()

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    private lateinit var photoFolder: File
    private lateinit var photoFile: File
    private lateinit var photofileName: String
    private var captureState = STATE_PREVIEW

    //размер картинки тут
    private lateinit var imageSize: Size
    private lateinit var previewSize: Size


    lateinit var previewCaptureSession: CameraCaptureSession


    private val sessionCaptureCallback by lazy {
        object : CameraCaptureSession.CaptureCallback() {

            private fun process(result: CaptureResult) {

                val mode = result.get(CaptureResult.STATISTICS_FACE_DETECT_MODE)
                val facesArray = result.get(CaptureResult.STATISTICS_FACES)
                facesArray.let {
                    Timber.d("++ faces ${it.size} mode ${mode}")
                }


                when (captureState) {
                    (STATE_PREVIEW) -> {
                        Toast.makeText(applicationContext, "nothing locked", Toast.LENGTH_SHORT).show()
                    }
                    (STATE_WAIT_LOCK) -> {

                        captureState = STATE_PREVIEW

                        val autofocusState = result.get(CaptureResult.CONTROL_AF_STATE)
                        if (autofocusState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED
                                || autofocusState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {


                            */
/*   unlockFocus()
                            Toast.makeText(applicationContext, "Auto focus locked", Toast.LENGTH_SHORT).show()*//*

                            captureStillImage()
                        }
                    }
                }

                //states?

                val afState = result.get(CaptureResult.CONTROL_AF_STATE)
                if (afState == CaptureRequest.CONTROL_AF_STATE_FOCUSED_LOCKED) {

                    */
/*captureStillImage()*//*

                }

            }

            override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?, result: TotalCaptureResult) {
                super.onCaptureCompleted(session, request, result)

                Timber.d("++ Capture completed")
                process(result)
            }
        }
    }


    private val previewCallback: CameraCaptureSession.CaptureCallback by lazy {
        object : CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?, result: TotalCaptureResult?) {
                super.onCaptureCompleted(session, request, result)
                //картинка снятая камерой доступна тепеь - можно передавать картинку
                // вызываетяс после захвата изображения
            }
        }
    }


    private val MAX_PREVIEW_WIDTH = 1280
    private val MAX_PREVIEW_HEIGHT = 720
    private lateinit var captureSession: CameraCaptureSession
    private lateinit var captureRequesBuilder: CaptureRequest.Builder

    private lateinit var backgroundThread: HandlerThread
    private lateinit var backgroundHadler: Handler

    private lateinit var imageFile: File


    //картинка
    private lateinit var imageReader: ImageReader
    private val onImageAvailibleListener by lazy {
        ImageReader.OnImageAvailableListener {
            backgroundHadler.post(ImageSaver(it.acquireNextImage(), applicationContext))
        }
    }


    private lateinit var cameraDevice: CameraDevice
    private val deviceStateCallBack = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice?) {
            Timber.d("Camera device opened")


            camera?.let {
                cameraDevice = it
                createCameraPreviewSession()
            }

        }

        override fun onClosed(camera: CameraDevice?) {
            super.onClosed(camera)
        }

        override fun onDisconnected(camera: CameraDevice?) {
            Timber.d("Device was disconnected")
            camera?.close()
        }

        override fun onError(camera: CameraDevice?, error: Int) {
            Timber.d("Camera device error")
            this@CameraActivity.finish()
        }
    }
    private val cameraManager by lazy {
        getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }


    private fun createCameraPreviewSession() {


        val surfaceTexture = captureTextureView.surfaceTexture

        surfaceTexture.setDefaultBufferSize(MAX_PREVIEW_WIDTH, MAX_PREVIEW_WIDTH)

        val previewSurface = Surface(surfaceTexture)

        //preview Capture builder
        captureRequesBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequesBuilder.addTarget(previewSurface)


        //инициализируем камер и ридер из вьюхи размерами. неправильно.
        setupCamera(captureTextureView.width, captureTextureView.height)

        cameraDevice.createCaptureSession(listOf(previewSurface, imageReader.surface),
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigureFailed(session: CameraCaptureSession?) {
                        Timber.d("++ Creating capture session failed")
                    }

                    override fun onConfigured(session: CameraCaptureSession) {
                        previewCaptureSession = session
                        try {
                            previewCaptureSession.setRepeatingRequest(captureRequesBuilder.build(),
                                    null, backgroundHadler)
                        } catch (e: CameraAccessException) { Crashlytics.logException(e) Crashlytics.logException(e)
                        }
                    }


                }, null)
    }

    private fun closeCamera() {
        if (this::captureSession.isInitialized) {
            captureSession.close()

        }
        if (this::cameraDevice.isInitialized) {
            cameraDevice.close()
        }

        if (this::imageReader.isInitialized) {
            imageReader.close()
        }
    }

    private fun startBackGroundThread() {
        backgroundThread = HandlerThread("Camera2").also { it.start() }
        backgroundHadler = Handler(backgroundThread.looper)
    }

    private fun stopBackGroundThread() {
        backgroundThread.quitSafely()

        try {
            backgroundThread.join()
        } catch (e: InterruptedException) {
            Timber.e("error $e")
        }

    }

    private fun <T> cameraCharacteristits(cameraId: String, key: CameraCharacteristics.Key<T>):
            T {
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        return when (key) {
            CameraCharacteristics.LENS_FACING -> characteristics.get(key)
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP -> characteristics.get(key)
            else -> throw IllegalArgumentException("Key not recognized")
        }
    }

    //Тут ищем камеры - выбираем, какая нам нужна
    private fun cameraID(lens: Int): String {

        var deviceId = listOf<String>()
        try {
            val cameraIdList = cameraManager.cameraIdList
            deviceId = cameraIdList.filter { lens == cameraCharacteristits(it, CameraCharacteristics.LENS_FACING) }
        } catch (e: CameraAccessException) { Crashlytics.logException(e) Crashlytics.logException(e)
            Timber.e("Error camera $e")
        }
        return deviceId[0]

    }

    private fun connectCamera() {
        val deviceId = cameraID(CameraCharacteristics.LENS_FACING_BACK)
        Timber.d("Device Id  $deviceId")

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(deviceId, deviceStateCallBack, backgroundHadler)
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "Camera app requiress acces to camera", Toast.LENGTH_LONG).show()

                    }
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSON_RESULT)
                }

            } else {

                cameraManager.openCamera(deviceId, deviceStateCallBack, backgroundHadler)
            }


        } catch (e: CameraAccessException) { Crashlytics.logException(e) Crashlytics.logException(e)
            Timber.e("$e")
        } catch (e: InterruptedException) {
            Timber.e("Open camera device interrupted $e")
        }


    }


    private val surfaceListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
            setupCamera(width, height)
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) = Unit

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?) = true

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            Timber.d("====> onSurfaceTextureAvailable  + $width + $height")
            connectCamera()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        snapShotIcon.setOnClickListener {
            checkWriteStoragePermission()
            lockFocus()
        }
    }

    override fun onResume() {
        super.onResume()

        startBackGroundThread()


        if (captureTextureView.isAvailable) {
            setupCamera(captureTextureView.width, captureTextureView.height)

            connectCamera()

        } else {
            captureTextureView.surfaceTextureListener = surfaceListener
        }
    }


    override fun onPause() {
        closeCamera()
        stopBackGroundThread()
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (requestCode == REQUEST_CAMERA_PERMISSON_RESULT) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Application will not run without camera servises",
                        Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == REQUEST_WRITE_PERMISSON_RESULT) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Yes permission", Toast.LENGTH_SHORT).show()
                */
/*   createPhotoFolder()*//*

            }
        }

    }

*/
/*    private fun createPhotoFolder() {
        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        photoFolder = File(file, "BelhuntPhotos")
        if (!photoFolder.exists()) {
            photoFolder.mkdirs()
        }
    }*//*


    */
/*   @Throws(IOException::class)
       private fun createPhotoFilename(): File {

           val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
           val prepend = "photo_" + timeStamp + "_"
           val file = File.createTempFile(prepend, ".jpg", photoFolder)
           photofileName = file.absolutePath
           return file


       }*//*


    private fun checkWriteStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                try {
                    */
/* createPhotoFilename()*//*


                } catch (e: Exception) { Crashlytics.logException(e)
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App needs to be able to save photos", Toast.LENGTH_LONG)
                }
                */
/*else{

                }*//*

                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_PERMISSION)

            }
        } else {


        }
    }

//Unlock FOCUS TODO


    private fun captureStillImage() {


        try {
            val captureStillBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureStillBuilder.addTarget(imageReader.surface)


            //TODO
            val rotation = windowManager.defaultDisplay.rotation

            captureStillBuilder.set(CaptureRequest.JPEG_ORIENTATION,
                    ORIENTATIONS.get(rotation))

            //make photo here - in callback
            val captureCallback = object : CameraCaptureSession.CaptureCallback() {

                override fun onCaptureStarted(session: CameraCaptureSession?, request: CaptureRequest?, timestamp: Long, frameNumber: Long) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber)


                    try {


                    } catch (e: IOException) {
                        Timber.d("file exception ${e.message}")
                    }
                }

                override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?, result: TotalCaptureResult?) {
                    super.onCaptureCompleted(session, request, result)

                    Toast.makeText(applicationContext, "Photo made", Toast.LENGTH_SHORT).show()
                    unlockFocus()

                }
            }
//null - we in background already
            previewCaptureSession.capture(captureStillBuilder.build(),
                    captureCallback, null)

        } catch (e: CameraAccessException) { Crashlytics.logException(e) Crashlytics.logException(e)
            Timber.e("camera access error $e")
        }

    }


    private fun lockFocus() {
        try {
            captureState = STATE_WAIT_LOCK
            captureRequesBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START)
            previewCaptureSession.capture(captureRequesBuilder.build(), sessionCaptureCallback, backgroundHadler);
        } catch (e: CameraAccessException) { Crashlytics.logException(e) Crashlytics.logException(e)

        }

    }

    private fun unlockFocus() {
        try {
            captureState = STATE_PREVIEW
            captureRequesBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CaptureRequest.CONTROL_AF_TRIGGER_CANCEL)
            previewCaptureSession.capture(captureRequesBuilder.build(),
                    sessionCaptureCallback, backgroundHadler)
        } catch (e: CameraAccessException) { Crashlytics.logException(e) Crashlytics.logException(e)

        }

    }


    private fun takePhoto(view: View) {


        lockFocus()

    }

    private fun setupCamera(width: Int, height: Int) {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {

            //читаем настрйоки камеры
            for (cameraIdin in cameraManager.cameraIdList) {
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraIdin)

                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }

                val map: StreamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                val largers = map.getOutputSizes(ImageFormat.JPEG)

                val largersImagetSize = largers.maxBy {
                    it.height * it.width
                }
                Timber.d("++ largest image $largersImagetSize")

                if (largersImagetSize != null) {
                    imageReader = ImageReader.newInstance(largersImagetSize.width,
                            largersImagetSize.height,
                            ImageFormat.JPEG,
                            1)
                }


                imageReader.setOnImageAvailableListener(
                        onImageAvailibleListener,
                        backgroundHadler
                )


            }
        } catch (e: CameraAccessException) { Crashlytics.logException(e) Crashlytics.logException(e)
            e.printStackTrace()
        }

    }


}
*/
