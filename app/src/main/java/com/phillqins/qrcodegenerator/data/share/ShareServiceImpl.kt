package com.phillqins.qrcodegenerator.data.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.phillqins.qrcodegenerator.domain.model.ShareError
import com.phillqins.qrcodegenerator.domain.model.ShareResult
import com.phillqins.qrcodegenerator.domain.repository.ShareService
import com.phillqins.qrcodegenerator.domain.repository.StorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Implementation of ShareService that handles QR code sharing through Android's share intent system.
 * Creates temporary files in the app's cache directory and uses FileProvider for secure file sharing.
 */
class ShareServiceImpl(
    private val context: Context,
    private val storageService: StorageService
) : ShareService {

    companion object {
        private const val TEMP_DIR_NAME = "shared_qr_codes"
        private const val FILE_PROVIDER_AUTHORITY = "com.phillqins.qrcodegenerator.fileprovider"
        private const val COMPRESSION_QUALITY = 90
    }

    override suspend fun shareQRCode(
        bitmap: Bitmap,
        title: String,
        filename: String?
    ): ShareResult = withContext(Dispatchers.IO) {
        try {
            // Generate filename if not provided
            val finalFilename = filename ?: storageService.generateUniqueFilename("QRCode_Share")
            
            // Create temporary file
            val tempFile = createTempFile(bitmap, finalFilename)
                ?: return@withContext ShareResult(
                    success = false,
                    error = ShareError.TempFileCreationFailed
                )

            // Create content URI using FileProvider
            val contentUri = try {
                FileProvider.getUriForFile(
                    context,
                    FILE_PROVIDER_AUTHORITY,
                    tempFile
                )
            } catch (e: IllegalArgumentException) {
                return@withContext ShareResult(
                    success = false,
                    error = ShareError.UnknownError("Failed to create content URI: ${e.message}")
                )
            }

            // Create share intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "QR Code shared from QRKod")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Create chooser intent
            val chooserIntent = Intent.createChooser(shareIntent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Check if there are apps that can handle the share intent
            val packageManager = context.packageManager
            if (chooserIntent.resolveActivity(packageManager) == null) {
                return@withContext ShareResult(
                    success = false,
                    error = ShareError.NoSharingAppsAvailable
                )
            }

            // Launch the share intent
            try {
                context.startActivity(chooserIntent)
                ShareResult(success = true)
            } catch (e: Exception) {
                ShareResult(
                    success = false,
                    error = ShareError.UnknownError("Failed to launch share intent: ${e.message}")
                )
            }

        } catch (e: Exception) {
            ShareResult(
                success = false,
                error = ShareError.UnknownError("Unexpected error during share: ${e.message}")
            )
        }
    }

    /**
     * Creates a temporary file in the app's cache directory for sharing.
     * 
     * @param bitmap The bitmap to save to the temporary file
     * @param filename The filename for the temporary file
     * @return The created File object, or null if creation failed
     */
    private suspend fun createTempFile(bitmap: Bitmap, filename: String): File? = 
        withContext(Dispatchers.IO) {
            try {
                // Create temp directory if it doesn't exist
                val tempDir = File(context.cacheDir, TEMP_DIR_NAME)
                if (!tempDir.exists() && !tempDir.mkdirs()) {
                    return@withContext null
                }

                // Clean up old temp files (older than 1 hour)
                cleanupOldTempFiles(tempDir)

                // Create the temp file
                val tempFile = File(tempDir, "$filename.png")
                
                // Write bitmap to file
                FileOutputStream(tempFile).use { outputStream ->
                    val success = bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, outputStream)
                    if (!success) {
                        tempFile.delete()
                        return@withContext null
                    }
                }

                tempFile
            } catch (e: IOException) {
                null
            } catch (e: SecurityException) {
                null
            } catch (e: Exception) {
                null
            }
        }

    /**
     * Cleans up temporary files older than 1 hour to prevent cache bloat.
     * 
     * @param tempDir The temporary directory to clean
     */
    private fun cleanupOldTempFiles(tempDir: File) {
        try {
            val currentTime = System.currentTimeMillis()
            val oneHourInMillis = 60 * 60 * 1000L // 1 hour

            tempDir.listFiles()?.forEach { file ->
                if (file.isFile && (currentTime - file.lastModified()) > oneHourInMillis) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            // Ignore cleanup failures - not critical for functionality
        }
    }
}