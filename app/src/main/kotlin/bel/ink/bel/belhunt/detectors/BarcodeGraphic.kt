/*
package bel.ink.bel.belhunt.detectors

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import bel.ink.bel.belhunt.camViews.GraphicOverlay
import com.google.android.gms.vision.barcode.Barcode

class BarcodeGraphic(val overlay: GraphicOverlay<*>) : GraphicOverlay.Graphic(overlay) {

    var id: Int = 0
    lateinit var barcode: Barcode

    private val rectPaint: Paint by lazy {
        Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 5.0f
        }
    }

    private val textPaint: Paint by lazy {
        Paint().apply {
            color = Color.WHITE
            textSize = 55.0f

        }
    }

    fun updateItem(barcode: Barcode) {
        this.barcode = barcode
        //postInvalidate()
    }

    override fun draw(canvas: Canvas) {
        val rect = RectF(barcode.boundingBox)
        rect.left = translateX(rect.left)
        rect.top = translateY(rect.top)
        rect.right = translateX(rect.right)
        rect.bottom = translateY(rect.bottom)
        canvas.drawRect(rect, rectPaint)
        canvas.drawText(barcode.rawValue, rect.left, rect.bottom, textPaint)
    }
}
*/
