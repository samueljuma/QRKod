package com.phillqins.qrcodegenerator.data.storage

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.phillqins.qrcodegenerator.domain.model.StorageError
import com.phillqins.qrcodegenerator.domain.model.StorageResult
import com.phillqins.qrcodegenerator.domain.repository.StorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implementation of StorageService that handles QR code bitmap storage
 * with MediaStore integration for Android 10+ and fallback file operations
 * for older Android versions.
 */
class StorageServiceImpl(
    private val context: Context
) : StorageService {

    companion object {
        private const val IMAGE_MIME_TYPE = "image/png"
        private const val COMPRESSION_QUALITY = 90
        private const val FILENAME_DATE_FORMAT = "yyyyMMdd_HHmmss"
        
        // Counter for collision avoidance within the same millisecond
        private val filenameCounter = AtomicInteger(0)
        
        // Track last timestamp to reset counter when time changes
        @Volatile
        private var lastTimestamp = 0L
    }

    override suspend fun saveBitmapToGallery(
        bitmap: Bitmap,
        filename: String,
        directory: String
    ): StorageResult = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore API for Android 10+ (scoped storage)
                saveUsingMediaStore(bitmap, filename, directory)
            } else {
                // Use traditional file operations for older versions
                saveUsingFileSystem(bitmap, filename, directory)
            }
        } catch (e: Exception) {
            StorageResult(
                success = false,
                error = StorageError.SystemError(e)
            )
        }
    }

    override fun generateUniqueFilename(prefix: String): String {
        val currentTime = System.currentTimeMillis()
        val timestamp = SimpleDateFormat(FILENAME_DATE_FORMAT, Locale.getDefault())
            .format(Date(currentTime))
        
        // Synchronize access to counter and timestamp tracking
        synchronized(this) {
            // Reset counter if we've moved to a new second
            val currentSecond = currentTime / 1000
            val lastSecond = lastTimestamp / 1000
            
            if (currentSecond != lastSecond) {
                filenameCounter.set(0)
                lastTimestamp = currentTime
                return "${prefix}_$timestamp"
            }
            
            // Same second - increment counter for uniqueness
            val counter = filenameCounter.incrementAndGet()
            lastTimestamp = currentTime
            
            // Add counter suffix for collision avoidance
            return "${prefix}_${timestamp}_${String.format("%03d", counter)}"
        }
    }

    /**
     * Saves bitmap using MediaStore API for Android 10+ (API 29+)
     * This method uses scoped storage and doesn't require WRITE_EXTERNAL_STORAGE permission
     */
    private suspend fun saveUsingMediaStore(
        bitmap: Bitmap,
        filename: String,
        directory: String
    ): StorageResult = withContext(Dispatchers.IO) {
        try {
            val resolver: ContentResolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename.png")
                put(MediaStore.MediaColumns.MIME_TYPE, IMAGE_MIME_TYPE)
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/$directory")
            }

            // Insert the image into MediaStore
            val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            
            if (uri == null) {
                return@withContext StorageResult(
                    success = false,
                    error = StorageError.FileWriteFailed
                )
            }

            // Write the bitmap to the URI
            resolver.openOutputStream(uri)?.use { outputStream ->
                val success = bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, outputStream)
                if (!success) {
                    // Clean up the created entry if compression failed
                    resolver.delete(uri, null, null)
                    return@withContext StorageResult(
                        success = false,
                        error = StorageError.FileWriteFailed
                    )
                }
            } ?: return@withContext StorageResult(
                success = false,
                error = StorageError.FileWriteFailed
            )

            // Get the actual file path for the result
            val filePath = getFilePathFromUri(uri)

            StorageResult(
                success = true,
                uri = uri,
                filePath = filePath
            )
        } catch (e: SecurityException) {
            StorageResult(
                success = false,
                error = StorageError.SystemError(e)
            )
        } catch (e: IOException) {
            StorageResult(
                success = false,
                error = StorageError.FileWriteFailed
            )
        } catch (e: Exception) {
            StorageResult(
                success = false,
                error = StorageError.SystemError(e)
            )
        }
    }

    /**
     * Saves bitmap using traditional file system operations for Android versions below API 29
     * This method requires WRITE_EXTERNAL_STORAGE permission
     */
    private suspend fun saveUsingFileSystem(
        bitmap: Bitmap,
        filename: String,
        directory: String
    ): StorageResult = withContext(Dispatchers.IO) {
        try {
            // Try Pictures directory first, then fallback to Downloads
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            
            val targetDir = File(picturesDir, directory)
            val fallbackDir = File(downloadsDir, directory)
            
            val result = saveToDirectory(bitmap, filename, targetDir) 
                ?: saveToDirectory(bitmap, filename, fallbackDir)
            
            result ?: StorageResult(
                success = false,
                error = StorageError.DirectoryCreationFailed
            )
        } catch (e: IOException) {
            StorageResult(
                success = false,
                error = StorageError.FileWriteFailed
            )
        } catch (e: SecurityException) {
            StorageResult(
                success = false,
                error = StorageError.SystemError(e)
            )
        } catch (e: Exception) {
            StorageResult(
                success = false,
                error = StorageError.SystemError(e)
            )
        }
    }

    /**
     * Attempts to save bitmap to a specific directory
     * Returns null if the operation fails
     */
    private suspend fun saveToDirectory(
        bitmap: Bitmap,
        filename: String,
        directory: File
    ): StorageResult? = withContext(Dispatchers.IO) {
        try {
            // Check if external storage is available and writable
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                return@withContext null
            }

            // Create directory if it doesn't exist
            if (!directory.exists() && !directory.mkdirs()) {
                return@withContext null
            }

            // Check available space (require at least 1MB)
            val availableSpace = directory.freeSpace
            if (availableSpace < 1024 * 1024) {
                return@withContext StorageResult(
                    success = false,
                    error = StorageError.InsufficientSpace
                )
            }

            val file = File(directory, "$filename.png")
            
            // Write bitmap to file
            FileOutputStream(file).use { outputStream ->
                val success = bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, outputStream)
                if (!success) {
                    // Clean up the file if compression failed
                    file.delete()
                    return@withContext null
                }
            }

            // Notify MediaStore about the new file for gallery visibility
            notifyMediaStore(file)

            StorageResult(
                success = true,
                uri = Uri.fromFile(file),
                filePath = file.absolutePath
            )
        } catch (e: IOException) {
            null
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Notifies MediaStore about a new file so it appears in gallery apps
     */
    private fun notifyMediaStore(file: File) {
        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DATA, file.absolutePath)
                put(MediaStore.Images.Media.MIME_TYPE, IMAGE_MIME_TYPE)
                put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            }
            
            context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        } catch (e: Exception) {
            // Ignore MediaStore notification failures - file is still saved
            // This is a best-effort operation for gallery visibility
        }
    }

    /**
     * Attempts to get the file path from a MediaStore URI
     * Returns null if the path cannot be determined
     */
    private fun getFilePathFromUri(uri: Uri): String? {
        return try {
            context.contentResolver.query(
                uri,
                arrayOf(MediaStore.Images.Media.DATA),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    if (columnIndex >= 0) {
                        cursor.getString(columnIndex)
                    } else null
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }
}