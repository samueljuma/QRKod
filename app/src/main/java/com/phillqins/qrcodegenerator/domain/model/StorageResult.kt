package com.phillqins.qrcodegenerator.domain.model

import android.net.Uri

/**
 * Represents the result of a storage operation.
 * 
 * @param success Whether the storage operation was successful
 * @param uri The content URI of the saved file (null if failed)
 * @param filePath The file path where the file was saved (null if failed)
 * @param error The error that occurred during storage (null if successful)
 */
data class StorageResult(
    val success: Boolean,
    val uri: Uri? = null,
    val filePath: String? = null,
    val error: StorageError? = null
)

/**
 * Sealed class representing different types of storage errors.
 */
sealed class StorageError {
    /**
     * Failed to create the required directory structure.
     */
    object DirectoryCreationFailed : StorageError()
    
    /**
     * Failed to write the file to storage.
     */
    object FileWriteFailed : StorageError()
    
    /**
     * Insufficient space available on the device.
     */
    object InsufficientSpace : StorageError()
    
    /**
     * System-level error occurred.
     * 
     * @param exception The underlying system exception
     */
    data class SystemError(val exception: Exception) : StorageError()
}