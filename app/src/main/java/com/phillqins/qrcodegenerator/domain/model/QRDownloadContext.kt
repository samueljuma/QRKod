package com.phillqins.qrcodegenerator.domain.model

import android.graphics.Bitmap

/**
 * Context information for a QR code download operation.
 * 
 * @param bitmap The QR code bitmap to be saved
 * @param filename The desired filename for the saved file
 * @param timestamp The timestamp when the download was initiated
 * @param directory The target directory for saving (relative to Pictures)
 */
data class QRDownloadContext(
    val bitmap: Bitmap,
    val filename: String,
    val timestamp: Long = System.currentTimeMillis(),
    val directory: String = "QRCodes"
)