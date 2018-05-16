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
package bel.ink.bel.belhunt.camViews

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.google.android.gms.vision.CameraSource

class GraphicOverlay<T : GraphicOverlay.Graphic>(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val lockGraphic = Object()
    private var previewWidthIn: Int = 0

    var widthScaleFactor = 1.0f
    private var previewHeightIn: Int = 0

    var heightScaleFactor = 1.0f
    private var facing = CameraSource.CAMERA_FACING_BACK
    private val graphicsSet = hashSetOf<T>()

    val graphics: List<T>
        get() = synchronized(lockGraphic) {
            return graphicsSet.toList()
        }

    abstract class Graphic(private val overlay: GraphicOverlay<*>) {


        abstract fun draw(canvas: Canvas)
        fun scaleX(horizontal: Float) = horizontal * overlay.widthScaleFactor
        fun scaleY(vertical: Float) = vertical * overlay.heightScaleFactor
        fun translateX(x: Float) = if (overlay.facing == CameraSource.CAMERA_FACING_FRONT) {
            overlay.width - scaleX(x)
        } else scaleX(x)

        fun translateY(y: Float) = scaleY(y)
        //dont use it manually
        fun postInvalidate() {
            overlay.postInvalidate()
        }
    }

    fun clear() {
        synchronized(lockGraphic) {
            graphicsSet.clear()
        }
        postInvalidate()
    }

    fun add(graphic: T) {
        synchronized(lockGraphic) {
            graphicsSet.add(graphic)
        }
        postInvalidate()
    }


    fun remove(graphic: T) {
        synchronized(lockGraphic) {
            graphicsSet.remove(graphic)
        }
        postInvalidate()
    }


    fun setCameraInfo(previewWidth: Int, previewHeight: Int, facing: Int) {
        synchronized(lockGraphic) {
            previewWidthIn = previewWidth
            previewHeightIn = previewHeight
            this.facing = facing
        }
        postInvalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lockGraphic) {
            if (previewWidthIn != 0 && previewHeightIn != 0) {
                widthScaleFactor = canvas.width.toFloat() / previewWidthIn.toFloat()
                heightScaleFactor = canvas.height.toFloat() / previewHeightIn.toFloat()
            }

            graphicsSet.forEach {
                it.draw(canvas)
            }
        }
    }
}
