/*
package bel.ink.bel.belhunt.cameraSources


import android.annotation.TargetApi
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.Build
import android.os.SystemClock
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import bel.ink.bel.belhunt.cameraSources.actionInterfaces.AutoFocusCallback
import bel.ink.bel.belhunt.cameraSources.actionInterfaces.AutoFocusMoveCallback
import bel.ink.bel.belhunt.cameraSources.actionInterfaces.PictureCallback
import bel.ink.bel.belhunt.cameraSources.actionInterfaces.ShutCallback
import bel.ink.bel.belhunt.utilits.getScreenRatio
import com.crashlytics.android.Crashlytics
import com.google.android.gms.common.images.Size
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import msq.inok.bel.belhunt.util.MAX_PX
import timber.log.Timber
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*


@Suppress("DEPRECATION")
class Camera1Api
(
        private val contextIn: Context,
        private val detector: Detector<*>,
        internal var fps: Float = 30F,
        internal var focusMode: String = Camera.Parameters.FOCUS_MODE_AUTO,
        internal var flashMode: String = Camera.Parameters.FLASH_MODE_AUTO,
        internal var facing: Int,
        internal var mRequestedPreviewWidth: Int = 1024,
        internal var mRequestedPreviewHeight: Int = 768

) : ICameraApi {

    private lateinit var camera: Camera
    private val cameraLock by lazy { Object() }
    private val frameProcessor by lazy { FrameProcessingRunnable(detector) }
    private val RATIOTOLERANCE = 0.2
    private val MAXRATIOTOLERANCE = 0.3
    private val MaxDIFFERENCEFORPHOTO = 1.4


    private var rotation: Int = 0
    var previewSize: Size? = null // размер превью сайза из нынешней камеры
        private set
    var pictureSize: Size? = null   // размер картинки который сейчас юзаем
        private set
    private lateinit var previewSurfaceHolder: SurfaceHolder
    private lateinit var processingThread: Thread  //поток для запуска фреймa
    private val bytesToByteBuffer = hashMapOf<ByteArray, ByteBuffer>()

    companion object {
        val CAMERA_BACK = CameraInfo.CAMERA_FACING_BACK
        val CAMERA_FRONT = CameraInfo.CAMERA_FACING_FRONT
    }

    init {
        when {
            (mRequestedPreviewWidth <= 0 || mRequestedPreviewWidth > MAX_PX
                    || mRequestedPreviewHeight <= 0 || mRequestedPreviewHeight > MAX_PX) -> {
                throw IllegalArgumentException("Invalid size: " + mRequestedPreviewWidth + "x" + mRequestedPreviewHeight)
                Crashlytics.log("Invalid size: " + mRequestedPreviewWidth + "x" + mRequestedPreviewHeight)
            }

            (facing != CameraInfo.CAMERA_FACING_BACK && facing != CameraInfo.CAMERA_FACING_FRONT) -> {
                throw IllegalArgumentException("No such a camera: $facing")
                Crashlytics.log("No such a camera: $facing")
            }
        }
    }


    private fun getIdForRequestedCamera(facing: Int): Int {
        val cameraInfo = CameraInfo()
        for (cameraID in 0..Camera.getNumberOfCameras()) {
            Camera.getCameraInfo(cameraID, cameraInfo)
            if (cameraInfo.facing == facing) {
                return cameraID
            }
        }
        return -1
    }


    fun getCountBestImageSize(camera: Camera): Camera.Size {
        val supportedPictureSizes = camera.parameters.supportedPictureSizes
        val appropriateSizes = supportedPictureSizes.filter { it.width / it.height.toFloat() < MaxDIFFERENCEFORPHOTO }
        val bestPictureSize = appropriateSizes.maxBy { it.height }!!
        return bestPictureSize
    }

    //TODO
    fun getBestAspectPreviewSize(camera: Camera, context: Context): Camera.Size {

        val allSupporterPreviewSizes = camera.parameters.supportedPreviewSizes
        val targetRatio = getScreenRatio(context)
        var bestSize: Camera.Size = allSupporterPreviewSizes[0]

        val previewSizeMap = mutableMapOf<Double, MutableList<Camera.Size>>()

        allSupporterPreviewSizes.forEach {
            val ratio = it.width.toFloat() / it.height
            val difference = Math.abs(ratio - targetRatio).toDouble()
            if (difference < RATIOTOLERANCE) {
                if (previewSizeMap.keys.contains(difference)) {
                    previewSizeMap[difference]?.add(it)
                } else {
                    val newList = ArrayList<Camera.Size>()
                    newList.add(it)
                    previewSizeMap[difference] = newList
                }
            }
        }

        if (previewSizeMap.isEmpty()) {
            for (size in allSupporterPreviewSizes) {
                val ratio = size.width.toFloat() / size.height
                val diff = Math.abs(ratio - targetRatio).toDouble()
                if (diff < MAXRATIOTOLERANCE) {
                    if (previewSizeMap.keys.contains(diff)) {
                        //add the value to the list
                        previewSizeMap[diff]?.add(size)
                    } else {
                        val newList = ArrayList<Camera.Size>()
                        newList.add(size)
                        previewSizeMap[diff] = newList
                    }
                }
            }
        }

        for ((_, value) in previewSizeMap) {
            val entries = value as List<*>
            for (i in entries.indices) {
                val s = entries[i] as Camera.Size
                if (s.height <= 1080 && s.width <= 1920) {
                    bestSize = s

                }
            }
        }
        return bestSize
    }

    private fun quarter(d: ByteBuffer, imageWidth: Int, imageHeight: Int): ByteBuffer {
        val data = d.array()
        val yuv = ByteArray(imageWidth / 4 * imageHeight / 4 * 3 / 2)
        // halve yuma
        var i = 0
        run {
            var y = 0
            while (y < imageHeight) {
                var x = 0
                while (x < imageWidth) {
                    yuv[i] = data[y * imageWidth + x]
                    i++
                    x += 4
                }
                y += 4
            }
        }
        // halve U and V color components
        var y = 0
        while (y < imageHeight / 2) {
            var x = 0
            while (x < imageWidth) {
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth + x]
                i++
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth + (x + 1)]
                i++
                x += 8
            }
            y += 4
        }
        return ByteBuffer.wrap(yuv)
    }

    override fun release() {
        synchronized(cameraLock) {

            stop()
            frameProcessor.release()
        }
    }


    @Throws(IOException::class)
    override fun start(surfaceHolder: SurfaceHolder): Camera1Api {
        synchronized(cameraLock) {
            try {
                previewSurfaceHolder = surfaceHolder
                val requestedCameraId = getIdForRequestedCamera(facing)
                if (requestedCameraId == -1) throw RuntimeException("Could not find requested camera.")

                val camera = Camera.open(requestedCameraId)
                val imageSize = getCountBestImageSize(camera)
                val prewDisplaySize = getBestAspectPreviewSize(camera, contextIn)

                this.previewSize = Size(prewDisplaySize.width, prewDisplaySize.height)
                this.pictureSize = Size(imageSize.width, imageSize.height)
                val previewFpsRanges = selectPreviewFpsRange(camera, fps)

                val parameters = camera.parameters
                parameters.setPictureSize(imageSize.width, imageSize.height)
                parameters.setPreviewSize(prewDisplaySize.width, prewDisplaySize.height)
                previewFpsRanges?.let {
                    parameters.setPreviewFpsRange(it[Camera.Parameters.PREVIEW_FPS_MIN_INDEX], it[Camera.Parameters.PREVIEW_FPS_MAX_INDEX])
                }
                parameters.previewFormat = ImageFormat.NV21

                setRotation(camera, parameters, requestedCameraId)

                if (parameters.supportedFocusModes.contains(focusMode)) {
                    parameters.focusMode = focusMode
                } else {
                    Timber.d("Camera focus mode: $focusMode is not supported ")
                }
                focusMode = parameters.focusMode
                parameters.supportedFlashModes?.let {
                    if (parameters.supportedFlashModes.contains(flashMode)) {
                        parameters.flashMode = flashMode
                    } else {
                        Timber.d("Camera flash mode: $flashMode is not supported on this device.")
                    }
                }
                flashMode = parameters.flashMode
                camera.parameters = parameters
                camera.setPreviewCallbackWithBuffer(CameraPreviewCallback())
                camera.addCallbackBuffer(createPreviewBuffer(this.previewSize!!))
                camera.addCallbackBuffer(createPreviewBuffer(this.previewSize!!))
                camera.addCallbackBuffer(createPreviewBuffer(this.previewSize!!))
                camera.addCallbackBuffer(createPreviewBuffer(this.previewSize!!))
                this.camera = camera
                this.camera.setPreviewDisplay(previewSurfaceHolder)
                this.camera.startPreview()
                processingThread = Thread(frameProcessor)
                frameProcessor.setActive(true)
                processingThread.start()
            } catch (e: RuntimeException) {
                e.printStackTrace()
                return this
            }
        }
        return this
    }

    override fun stop() {
        synchronized(cameraLock) {
            frameProcessor.setActive(false)
            if (this::processingThread.isInitialized) {
                try {
                    processingThread.join()
                } catch (e: InterruptedException) {
                    Timber.d("thread interrupted on release.")
                }
            }

            bytesToByteBuffer.clear()

            //TODO here
            if (this::camera.isInitialized) {
                camera.stopPreview()
                camera.setPreviewCallbackWithBuffer(null)
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        camera.setPreviewTexture(null)

                    } else {
                        camera.setPreviewDisplay(null)
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                    Timber.d("Failed to clear camera preview: $e")
                }
                camera.release()
            }
        }
    }

    override fun doZoom(scale: Float): Int {
        synchronized(cameraLock) {
            if (!this::camera.isInitialized) {
                return 0
            }
            var currentZoom = 0
            val maxZoom: Int
            val parameters = camera.parameters
            if (!parameters.isZoomSupported) {
                Timber.d("Zoom is not supported")
                return currentZoom
            }
            maxZoom = parameters.maxZoom
            currentZoom = parameters.zoom + 1
            val newZoom: Float
            if (scale > 1) {
                newZoom = currentZoom + scale * (maxZoom / 10)
            } else {
                newZoom = currentZoom * scale
            }
            currentZoom = Math.round(newZoom) - 1
            if (currentZoom < 0) {
                currentZoom = 0
            } else if (currentZoom > maxZoom) {
                currentZoom = maxZoom
            }
            parameters.zoom = currentZoom
            camera.parameters = parameters
            return currentZoom
        }
    }

    override fun capturePicture(shut: ShutCallback, jpeg: PictureCallback) {
        Timber.d("TRYING TO TAKE PICTURE")
        synchronized(cameraLock) {
            if (this::camera.isInitialized) {
                setFlash(flashMode)
                val startCallback = PictureStartCallback()
                startCallback.delegate = shut
                val doneCallback = PictureDoneCallback()
                doneCallback.delegate = jpeg
                camera.takePicture(startCallback, null, null, doneCallback)
            }
        }
    }

    override fun setFocus(mode: String): Boolean {
        synchronized(cameraLock) {
            if (this::camera.isInitialized) {
                val parameters = camera.parameters
                if (parameters.supportedFocusModes.contains(mode)) {
                    parameters.focusMode = mode
                    camera.parameters = parameters
                    focusMode = mode
                    return true
                }
            }
            return false
        }
    }

    override fun setFlash(mode: String): Boolean {
        synchronized(cameraLock) {
            if (this::camera.isInitialized) {
                val parameters = camera.parameters
                val supportedFlashModes = parameters.supportedFlashModes
                if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size == 1 && supportedFlashModes[0] == Camera.Parameters.FLASH_MODE_OFF) {
                    return false
                }
                if (supportedFlashModes.contains(mode)) {
                    parameters.flashMode = mode
                    camera.parameters = parameters
                    flashMode = mode
                    return true
                }
            }
            return false
        }
    }

    override fun autoFocus(autoFocus: AutoFocusCallback) {
        synchronized(cameraLock) {
            if (this::camera.isInitialized) {
                var autoFocusCallback: CameraAutoFocusCallback? = null
                if (autoFocus != null) {
                    autoFocusCallback = CameraAutoFocusCallback()
                    autoFocusCallback.mDelegate = autoFocus
                }
                camera.autoFocus(autoFocusCallback)
            }
        }
    }

    fun cancelAutoFocus() {
        synchronized(cameraLock) {
            if (this::camera.isInitialized) {
                camera.cancelAutoFocus()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun setAutoFocusMoveCallback(cb: AutoFocusMoveCallback?): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return false
        }

        synchronized(cameraLock) {
            if (this::camera.isInitialized) {
                var autoFocusMoveCallback: CameraAutoFocusMoveCallback? = null
                if (cb != null) {
                    autoFocusMoveCallback = CameraAutoFocusMoveCallback()
                    autoFocusMoveCallback.mDelegate = cb
                }
                camera.setAutoFocusMoveCallback(autoFocusMoveCallback)
            }
        }

        return true
    }

    private inner class PictureStartCallback : Camera.ShutterCallback {
        var delegate: ShutCallback? = null

        override fun onShutter() {
            delegate?.onShutter()
        }
    }

    private inner class PictureDoneCallback : Camera.PictureCallback {
        lateinit var delegate: PictureCallback

        override fun onPictureTaken(data: ByteArray, camera: Camera) {
            if (this::delegate.isInitialized) {
                val picture = BitmapFactory.decodeByteArray(data, 0, data.size)
                delegate.onCaptureImage(picture)
            }

            synchronized(cameraLock) {
                if (this@Camera1Api::camera.isInitialized) {
                    this@Camera1Api.camera.startPreview()
                }
            }
        }
    }

    private inner class CameraAutoFocusCallback : Camera.AutoFocusCallback {
        var mDelegate: AutoFocusCallback? = null

        override fun onAutoFocus(success: Boolean, camera: Camera) {
            mDelegate?.onAutoFocus(success)
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private inner class CameraAutoFocusMoveCallback : Camera.AutoFocusMoveCallback {
        var mDelegate: AutoFocusMoveCallback? = null

        override fun onAutoFocusMoving(start: Boolean, camera: Camera) {
            mDelegate?.onAutoFocusMoving(start)
        }
    }

    private fun selectPreviewFpsRange(camera: Camera, desiredPreviewFps: Float): IntArray? {

        val desiredPreviewFpsScaled = (desiredPreviewFps * 1000.0f).toInt()

        var selectedFpsRange: IntArray? = null
        var minDiff = Integer.MAX_VALUE
        val previewFpsRangeList = camera.parameters.supportedPreviewFpsRange

        previewFpsRangeList.forEach {
            val deltaMin = desiredPreviewFpsScaled - it[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
            val deltaMax = desiredPreviewFpsScaled - it[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
            val diff = Math.abs(deltaMin) + Math.abs(deltaMax)
            if (diff < minDiff) {
                selectedFpsRange = it
                minDiff = diff
            }
        }

        return selectedFpsRange
    }

    //считаем и ставим наклон ++
    private fun setRotation(camera: Camera, parameters: Camera.Parameters, cameraId: Int) {
        val windowManager = contextIn.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var degrees = 0
        when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
            else -> Timber.d("?? rotation ")
        }

        val cameraInfo = CameraInfo()
        Camera.getCameraInfo(cameraId, cameraInfo)

        val angle: Int
        val displayAngle: Int
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            angle = (cameraInfo.orientation + degrees) % 360
            displayAngle = (360 - angle) % 360 // compensate for it being mirrored
        } else {  // back-facing
            angle = (cameraInfo.orientation - degrees + 360) % 360
            displayAngle = angle
        }
        rotation = angle / 90

        camera.setDisplayOrientation(displayAngle)
        parameters.setRotation(angle)
    }


    private fun createPreviewBuffer(previewSize: Size): ByteArray {
        val bitsPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.NV21)
        val sizeInBits = (previewSize.height * previewSize.width * bitsPerPixel).toLong()
        val bufferSize = Math.ceil(sizeInBits / 8.0).toInt() + 1

        val byteArray = ByteArray(bufferSize)
        val buffer = ByteBuffer.wrap(byteArray)
        if (!buffer.hasArray() || buffer.array() != byteArray) {
            // I don't think that this will ever happen.  But if it does, then we wouldn't be
            // passing the preview content to the underlying detector later.
            throw IllegalStateException("Failed to create valid buffer for camera source.")
        }
        bytesToByteBuffer[byteArray] = buffer
        return byteArray
    }


    private inner class CameraPreviewCallback : Camera.PreviewCallback {
        override fun onPreviewFrame(data: ByteArray, camera: Camera) {
            frameProcessor.setNextFrame(data, camera)
        }
    }

    private inner class FrameProcessingRunnable internal constructor(private var frameDetector: Detector<*>) : Runnable {
        private val mStartTimeMillis = SystemClock.elapsedRealtime()

        private val frameLock = Object()
        private var isActiveFrame = true

        private var mPendingTimeMillis: Long = 0
        private var mPendingFrameId = 0
        private lateinit var pendingFrameData: ByteBuffer


        fun release() {
            frameDetector.release()
        }

        fun setActive(active: Boolean) {
            synchronized(frameLock) {
                isActiveFrame = active
                frameLock.notifyAll()
            }
        }

        fun setNextFrame(data: ByteArray, camera: Camera) {
            synchronized(frameLock) {
                if (this::pendingFrameData.isInitialized) {
                    camera.addCallbackBuffer(pendingFrameData.array())
                    pendingFrameData.clear()
                }
                if (!bytesToByteBuffer.containsKey(data)) {
                    Timber.d("Skipping frame")
                    return
                }
                mPendingTimeMillis = SystemClock.elapsedRealtime() - mStartTimeMillis
                mPendingFrameId++
                bytesToByteBuffer[data]?.let { pendingFrameData = it }
                frameLock.notifyAll()
            }
        }

        override fun run() {
            var outputFrame: Frame? = null
            var data: ByteBuffer? = null

            while (true) {
                synchronized(frameLock) {
                    while (isActiveFrame && !this::pendingFrameData.isInitialized) {
                        try {
                            frameLock.wait()
                        } catch (e: InterruptedException) {
                            Timber.d("Frame terminated $e")
                            Crashlytics.log(e.message)
                            return
                        }
                    }

                    if (!isActiveFrame) {
                        return
                    }

                    //уменьшаем размер превью
                    val previewW = previewSize!!.width
                    val previewH = previewSize!!.height

                    outputFrame = Frame.Builder()
                            .setImageData(quarter(pendingFrameData, previewW, previewH), previewW / 4, previewH / 4, ImageFormat.NV21)
                            .setId(mPendingFrameId)
                            .setTimestampMillis(mPendingTimeMillis)
                            .setRotation(rotation)
                            .build()

                    data = pendingFrameData
                    pendingFrameData.clear()
                }

                try {
                    frameDetector.receiveFrame(outputFrame)
                } catch (t: Throwable) {
                    Timber.d("Exception thrown from receiver. ${t.message}")
                    Crashlytics.log(t.message)
                } finally {
                    camera.addCallbackBuffer(data?.array())
                }
            }
        }
    }
}
*/
