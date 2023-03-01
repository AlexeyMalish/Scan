package com.basetrack.basetrackqr.qrScan.qrUtils

import com.google.mlkit.vision.barcode.common.Barcode



interface QRReaderListener {
    fun onRead(barcode: Barcode, barcodes: List<Barcode>)
    fun onError(exception: Exception)
}