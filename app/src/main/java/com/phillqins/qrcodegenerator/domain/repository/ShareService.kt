package com.phillqins.qrcodegenerator.domain.repository

import android.graphics.Bitmap
import com.phillqins.qrcodegenerator.domain.model.ShareResult

/**
 * Service interface for handling QR code sharing operations.
 * Provides functionality to share QR code bitmaps through Android's share intent system.
 */
interface ShareService {
    
    /**
     * Shares a QR code bitmap through Android's share intent.
     * Creates a temporary file and launches the system share dialog.
     * 
     * @param bitmap The QR code bitmap to share
     * @param title Optional title for the share dialog (default: "Share QR Code")
     * @param filename Optional filename for the shared file (if null, a unique filename will be generated)
     * @return ShareResult indicating success/failure and any error information
     */
    suspend fun shareQRCode(
        bitmap: Bitmap,
        title: String = "Share QR Code",
        filename: String? = null
    ): ShareResult
}