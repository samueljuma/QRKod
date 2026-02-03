package com.phillqins.qrcodegenerator.domain.model

/**
 * Represents the result of a QR code download operation.
 * 
 * @param success Whether the download operation was successful
 * @param filePath The path where the file was saved (null if failed)
 * @param error The error that occurred during download (null if successful)
 */
data class DownloadResult(
    val success: Boolean,
    val filePath: String? = null,
    val error: DownloadError? = null
)

/**
 * Sealed class representing different types of download errors.
 */
sealed class DownloadError {
    /**
     * Storage permission was denied by the user.
     */
    object PermissionDenied : DownloadError()
    
    /**
     * Insufficient storage space available on the device.
     */
    object InsufficientStorage : DownloadError()
    
    /**
     * General file system error occurred during the operation.
     */
    object FileSystemError : DownloadError()
    
    /**
     * An unknown error occurred.
     * 
     * @param message Descriptive error message
     */
    data class UnknownError(val message: String) : DownloadError()
}