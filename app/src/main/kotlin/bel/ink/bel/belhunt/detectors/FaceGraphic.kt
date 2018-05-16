package bel.ink.bel.belhunt.detectors

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import bel.ink.bel.belhunt.camViews.GraphicOverlay
import com.google.android.gms.vision.face.Face


class FaceGraphic(overlay: GraphicOverlay<*>) : GraphicOverlay.Graphic(overlay) {

    private var faceId: Int = 0

    private val FACE_POSITION_RADIUS = 10.0f
    private val ID_TEXT_SIZE = 40.0f
    private val ID_Y_OFFSET = 50.0f
    private val ID_X_OFFSET = -50.0f
    private val BOX_STROKE_WIDTH = 5.0f

    private var mFacePositionPaint: Paint = Paint()
    private var mIdPaint: Paint = Paint()
    private var mBoxPaint: Paint = Paint()

    private var mFace: Face? = null

    init {

        mIdPaint.textSize = ID_TEXT_SIZE
        mBoxPaint.color = Color.WHITE
        mBoxPaint.style = Paint.Style.STROKE
        mBoxPaint.strokeWidth = BOX_STROKE_WIDTH
    }

    fun setId(id: Int) {
        faceId = id
    }

    fun updateFace(face: Face) {
        mFace = face
        //POSTInvalidates - not good
        //postInvalidate()
    }

    fun goneFace() {
        mFace = null
    }

    override fun draw(canvas: Canvas) {
        val face = mFace
        if (face == null) {
            return
        }
        val x = translateX(face.position.x + face.width / 2)
        val y = translateY(face.position.y + face.height / 2)
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint)
        canvas.drawText("face: $faceId", x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint)
        canvas.drawText("smile: " + String.format("%.1f", face.isSmilingProbability), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint)

        val yOffset = scaleY(face.height / 2.0f)
        val top = y - yOffset
        val bottom = y + yOffset
        canvas.drawCircle(x, y, (bottom - top) / 2F, mBoxPaint)

    }
}