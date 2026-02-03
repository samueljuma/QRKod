package com.phillqins.qrcodegenerator.data.usecase

import android.graphics.Bitmap
import com.phillqins.qrcodegenerator.domain.model.ShareError
import com.phillqins.qrcodegenerator.domain.model.ShareResult
import com.phillqins.qrcodegenerator.domain.repository.ShareService
import com.phillqins.qrcodegenerator.domain.usecase.ShareQRCodeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of ShareQRCodeUseCase that orchestrates QR code sharing operations.
 * 
 * This use case coordinates with the ShareService to:
 * 1. Validate input parameters
 * 2. Create temporary files for sharing
 * 3. Launch the system share dialog
 * 4. Handle comprehensive error scenarios
 */
class ShareQRCodeUseCaseImpl(
    private val shareService: ShareService
) : ShareQRCodeUseCase {

    /**
     * Executes the QR code share operation with comprehensive error handling.
     * 
     * The operation follows this flow:
     * 1. Validate input parameters
     * 2. Delegate to ShareService for file creation and intent launching
     * 3. Handle any errors that occur during the process
     * 
     * @param bitmap The QR code bitmap to share
     * @param title Optional title for the share dialog
     * @param filename Optional custom filename (if null, a unique filename will be generated)
     * @return ShareResult indicating success/failure and any error information
     */
    override suspend fun execute(
        bitmap: Bitmap,
        title: String,
        filename: String?
    ): ShareResult = withContext(Dispatchers.IO) {
        try {
            // Step 1: Validate input parameters
            if (bitmap.isRecycled) {
                return@withContext ShareResult(
                    success = false,
                    error = ShareError.UnknownError("Bitmap is recycled and cannot be shared")
                )
            }

            // Step 2: Delegate to ShareService
            shareService.shareQRCode(bitmap, title, filename)

        } catch (e: Exception) {
            // Catch any unexpected exceptions and provide meaningful error
            ShareResult(
                success = false,
                error = ShareError.UnknownError("Unexpected error during share: ${e.message}")
            )
        }
    }
}