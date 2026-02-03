package com.phillqins.qrcodegenerator.domain.usecase

import android.graphics.Bitmap
import com.phillqins.qrcodegenerator.domain.model.DownloadResult

/**
 * Use case interface for downloading QR code images.
 * Orchestrates the download operation by coordinating permissions and storage services.
 */
interface DownloadQRCodeUseCase {
    
    /**
     * Executes the QR code download operation.
     * 
     * @param bitmap The QR code bitmap to save
     * @param filename Optional custom filename (if null, a unique filename will be generated)
     * @return DownloadResult indicating success/failure and file information
     */
    suspend fun execute(bitmap: Bitmap, filename: String? = null): DownloadResult
}