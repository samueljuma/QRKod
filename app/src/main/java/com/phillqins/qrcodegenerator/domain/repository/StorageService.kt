package com.phillqins.qrcodegenerator.domain.repository

import android.graphics.Bitmap
import com.phillqins.qrcodegenerator.domain.model.StorageResult

/**
 * Service interface for handling storage operations related to QR code files.
 * Provides platform-specific storage operations with API level compatibility.
 */
interface StorageService {
    
    /**
     * Saves a bitmap to the device's gallery/pictures directory.
     * 
     * @param bitmap The bitmap to save
     * @param filename The desired filename (without extension)
     * @param directory The subdirectory within Pictures (default: "QRCodes")
     * @return StorageResult indicating success/failure and file information
     */
    suspend fun saveBitmapToGallery(
        bitmap: Bitmap,
        filename: String,
        directory: String = "QRCodes"
    ): StorageResult
    
    /**
     * Generates a unique filename with timestamp to prevent collisions.
     * 
     * @param prefix The prefix for the filename (default: "QRCode")
     * @return A unique filename in format "prefix_YYYYMMDD_HHMMSS"
     */
    fun generateUniqueFilename(prefix: String = "QRCode"): String
}