package com.phillqins.qrcodegenerator.data.usecase

import android.app.Activity
import android.graphics.Bitmap
import com.phillqins.qrcodegenerator.domain.model.DownloadError
import com.phillqins.qrcodegenerator.domain.model.DownloadResult
import com.phillqins.qrcodegenerator.domain.model.StorageError
import com.phillqins.qrcodegenerator.domain.repository.PermissionHandler
import com.phillqins.qrcodegenerator.domain.repository.StorageService
import com.phillqins.qrcodegenerator.domain.usecase.DownloadQRCodeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of DownloadQRCodeUseCase that orchestrates QR code download operations.
 * 
 * This use case coordinates between the PermissionHandler and StorageService to:
 * 1. Check and request necessary storage permissions
 * 2. Generate unique filenames if not provided
 * 3. Save the QR code bitmap to device storage
 * 4. Handle comprehensive error scenarios with appropriate recovery strategies
 * 
 * The implementation follows the requirements for:
 * - Permission management across different Android API levels (Requirements 2.1, 2.2, 2.5)
 * - Comprehensive error handling and user feedback (Requirements 1.2, 1.3, 3.3, 3.4, 3.5)
 * - File storage with unique naming (Requirements 1.1, 1.4, 5.1, 5.5)
 * - MVVM architecture integration (Requirements 4.1)
 */
class DownloadQRCodeUseCaseImpl(
    private val storageService: StorageService,
    private val permissionHandler: PermissionHandler,
    private val activity: Activity? = null
) : DownloadQRCodeUseCase {

    /**
     * Executes the QR code download operation with comprehensive error handling.
     * 
     * The operation follows this flow:
     * 1. Validate input parameters
     * 2. Check and request storage permissions if needed
     * 3. Generate unique filename if not provided
     * 4. Save bitmap to storage using StorageService
     * 5. Map storage errors to download errors for consistent error handling
     * 
     * @param bitmap The QR code bitmap to save
     * @param filename Optional custom filename (if null, a unique filename will be generated)
     * @return DownloadResult indicating success/failure and file information
     */
    override suspend fun execute(bitmap: Bitmap, filename: String?): DownloadResult = 
        withContext(Dispatchers.IO) {
            try {
                // Step 1: Validate input parameters
                if (bitmap.isRecycled) {
                    return@withContext DownloadResult(
                        success = false,
                        error = DownloadError.UnknownError("Bitmap is recycled and cannot be saved")
                    )
                }

                // Step 2: Handle permissions based on API level and current permission state
                val permissionResult = handlePermissions()
                if (!permissionResult.success) {
                    return@withContext permissionResult
                }

                // Step 3: Generate filename if not provided
                val finalFilename = filename ?: storageService.generateUniqueFilename()

                // Step 4: Attempt to save the bitmap to storage
                val storageResult = storageService.saveBitmapToGallery(
                    bitmap = bitmap,
                    filename = finalFilename
                )

                // Step 5: Map storage result to download result
                mapStorageResultToDownloadResult(storageResult)

            } catch (e: Exception) {
                // Catch any unexpected exceptions and provide meaningful error
                DownloadResult(
                    success = false,
                    error = DownloadError.UnknownError("Unexpected error during download: ${e.message}")
                )
            }
        }

    /**
     * Handles permission checking and requesting based on the current API level.
     * 
     * This method implements the permission flow consistency requirement (2.4):
     * - For API 29+: No permissions needed (scoped storage)
     * - For API 23-28: Check and request WRITE_EXTERNAL_STORAGE if needed
     * - For API 21-22: No runtime permissions required
     * 
     * @return DownloadResult indicating permission status
     */
    private suspend fun handlePermissions(): DownloadResult {
        // Check if permissions are needed for current API level
        if (!permissionHandler.shouldRequestPermission()) {
            // No permission request needed (API 29+ or API 21-22)
            return DownloadResult(success = true)
        }

        // Check if we already have the required permissions
        if (permissionHandler.hasStoragePermission()) {
            return DownloadResult(success = true)
        }

        // Request permissions if activity is available
        if (activity == null) {
            return DownloadResult(
                success = false,
                error = DownloadError.PermissionDenied
            )
        }

        // Request the required permissions
        val permissionResult = permissionHandler.requestPermissions(activity)
        
        return if (permissionResult.granted) {
            DownloadResult(success = true)
        } else {
            DownloadResult(
                success = false,
                error = DownloadError.PermissionDenied
            )
        }
    }

    /**
     * Maps StorageResult to DownloadResult with appropriate error translation.
     * 
     * This method implements comprehensive error handling (Requirements 1.3, 3.3, 3.4, 3.5)
     * by translating storage-specific errors to user-friendly download errors.
     * 
     * @param storageResult The result from the storage operation
     * @return DownloadResult with mapped errors and file information
     */
    private fun mapStorageResultToDownloadResult(storageResult: com.phillqins.qrcodegenerator.domain.model.StorageResult): DownloadResult {
        return if (storageResult.success) {
            DownloadResult(
                success = true,
                filePath = storageResult.filePath
            )
        } else {
            val downloadError = when (storageResult.error) {
                is StorageError.InsufficientSpace -> DownloadError.InsufficientStorage
                is StorageError.DirectoryCreationFailed -> DownloadError.FileSystemError
                is StorageError.FileWriteFailed -> DownloadError.FileSystemError
                is StorageError.SystemError -> {
                    // Check if the system error is permission-related
                    val exception = storageResult.error.exception
                    if (exception is SecurityException) {
                        DownloadError.PermissionDenied
                    } else {
                        DownloadError.UnknownError(
                            exception.message ?: "System error occurred during storage operation"
                        )
                    }
                }
                null -> DownloadError.UnknownError("Unknown storage error occurred")
            }
            
            DownloadResult(
                success = false,
                error = downloadError
            )
        }
    }
}