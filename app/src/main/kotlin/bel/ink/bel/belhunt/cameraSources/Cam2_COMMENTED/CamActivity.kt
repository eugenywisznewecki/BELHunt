/*
package bel.ink.bel.belhunt.camera1Sample

import android.graphics.Matrix
import android.graphics.RectF
import android.hardware.Camera
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Display
import android.view.Surface
import android.view.SurfaceHolder
import bel.ink.bel.belhunt.R
import kotlinx.android.synthetic.main.activity_cam.*
import timber.log.Timber
import java.io.IOException
import android.R.attr.orientation
import android.hardware.Camera.CameraInfo



//тут проверяю работу камеры one
//

class CamActivity : AppCompatActivity() {

    private var holderCallBack = object : SurfaceHolder.Callback {
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            camera.stopPreview()
            //setCameraDisplayOrientation(CAMERA_ID)
            try {
                camera.setPreviewDisplay(holder)
                camera.startPreview()
            } catch (e: Exception) { Crashlytics.logException(e)
                Timber.e(e.message)
            }

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {

        }

        override fun surfaceCreated(holder: SurfaceHolder) {

            try {
                camera.setPreviewDisplay(holder)
                camera.startPreview()

            } catch (e: IOException) {
                Timber.e(e.message)
            }


        }
    }
    private lateinit var camera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cam)


        val holder = surfaceView.holder
        holder.addCallback(holderCallBack)

    }

    override fun onResume() {
        super.onResume()
        camera = Camera.open(0)
        setPreviewSize(false)


    }

    override fun onPause() {
        super.onPause(

        )

        if (this::camera.isInitialized)
            camera.release()
    }


    fun setPreviewSize(fullScreen: Boolean) {

        // получаем размеры экрана
        val display = windowManager.defaultDisplay
        val widthIsMax = display.width > display.height

        // определяем размеры превью камеры
        val size = camera.parameters.previewSize

        val rectDisplay = RectF()
        val rectPreview = RectF()

        // RectF экрана, соотвествует размерам экрана
        rectDisplay.set(0f, 0f, display.width.toFloat(), display.height.toFloat())

        // RectF первью
        if (widthIsMax) {
            // превью в горизонтальной ориентации
            rectPreview.set(0f, 0f, size.width.toFloat(), size.height.toFloat())
        } else {
            // превью в вертикальной ориентации
            rectPreview.set(0f, 0f, size.height.toFloat(), size.width.toFloat())
        }

        val matrix = Matrix()
        // подготовка матрицы преобразования
        if (!fullScreen) {
            // если превью будет "втиснут" в экран (второй вариант из урока)
            matrix.setRectToRect(rectPreview, rectDisplay,
                    Matrix.ScaleToFit.START)
        } else {
            // если экран будет "втиснут" в превью (третий вариант из урока)
            matrix.setRectToRect(rectDisplay, rectPreview,
                    Matrix.ScaleToFit.START)
            matrix.invert(matrix)
        }
        // преобразование
        matrix.mapRect(rectPreview)

        // установка размеров surface из получившегося преобразования
        surfaceView.getLayoutParams().height = rectPreview.bottom.toInt()
        surfaceView.getLayoutParams().width = rectPreview.right.toInt()
    }

    fun setCameraDisplayOrientation(cameraId: Int) {
        // определяем насколько повернут экран от нормального положения
        val rotation = windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result = 0

        // получаем инфо по камере cameraId
        val info = CameraInfo()
        Camera.getCameraInfo(cameraId, info)

        // задняя камера
        if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
            result = 360 - degrees + info.orientation
        } else
        // передняя камера
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                result = 360 - degrees - info.orientation
                result += 360
            }
        result = result % 360
        camera.setDisplayOrientation(result)
    }

    fun checkCharacteristics(){

        val parameners = camera.parameters


    }


}
*/
