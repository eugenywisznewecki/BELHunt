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
 *//*

package bel.ink.bel.belhunt.detectors

import android.content.Context
import android.support.annotation.UiThread
import bel.ink.bel.belhunt.camViews.GraphicOverlay
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.barcode.Barcode

class BarcodeGraphicTracker(private val overlay: GraphicOverlay<BarcodeGraphic>, private val graphic: BarcodeGraphic,
                            context: Context) : Tracker<Barcode>() {


    private var barcodeUpdateListener: BarcodeUpdateListener? = null


    interface BarcodeUpdateListener {
        @UiThread
        fun onBarcodeDetected(barcode: Barcode)
    }

    init {
        if (context is BarcodeUpdateListener) {
            this.barcodeUpdateListener = context
        } else {
            throw RuntimeException("implement BarcodeUpdateListener")
        }
    }

    override fun onNewItem(id: Int, item: Barcode) {
        graphic.id = id
        barcodeUpdateListener!!.onBarcodeDetected(item)
    }

    override fun onUpdate(detectionResults: Detector.Detections<Barcode>?, item: Barcode) {
        overlay.add(graphic)
        graphic.updateItem(item)
    }


    override fun onMissing(detectionResults: Detector.Detections<Barcode>?) {
        overlay.remove(graphic)
    }


    override fun onDone() {
        overlay.remove(graphic)
    }
}
*/
