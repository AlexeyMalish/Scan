package com.basetrack.basetrackqr.qrScan.qrUtils

import androidx.camera.core.CameraSelector
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode



data class QRCameraConfiguration(
    var lensFacing: Int = CameraSelector.LENS_FACING_BACK,

    val options: BarcodeScannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS
        )
        .build()
)