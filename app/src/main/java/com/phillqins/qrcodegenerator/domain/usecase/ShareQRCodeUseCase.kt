package com.phillqins.qrcodegenerator.domain.usecase

import android.graphics.Bitmap
import com.phillqins.qrcodegenerator.domain.model.ShareResult

/**
 * Use case interface for sharing QR code images.
 * Orchestrates the share operation by coordinating with the ShareService.
 */
interface ShareQRCodeUseCase {
    
    /**
     * Executes the QR code share operation.
     * 
     * @param bitmap The QR code bitmap to share
     * @param title Optional title for the share dialog
     * @param filename Optional custom filename (if null, a unique filename will be generated)
     * @return ShareResult indicating success/failure and any error information
     */
    suspend fun execute(
        bitmap: Bitmap, 
        title: String = "Share QR Code",
        filename: String? = null
    ): ShareResult
}