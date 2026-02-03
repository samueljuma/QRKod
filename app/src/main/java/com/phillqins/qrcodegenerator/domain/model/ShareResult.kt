package com.phillqins.qrcodegenerator.domain.model

/**
 * Represents the result of a QR code share operation.
 * 
 * @param success Whether the share operation was successful
 * @param error The error that occurred during sharing (null if successful)
 */
data class ShareResult(
    val success: Boolean,
    val error: ShareError? = null
)

/**
 * Sealed class representing different types of share errors.
 */
sealed class ShareError {
    /**
     * No sharing apps are available on the device.
     */
    object NoSharingAppsAvailable : ShareError()
    
    /**
     * Failed to create temporary file for sharing.
     */
    object TempFileCreationFailed : ShareError()
    
    /**
     * User cancelled the share operation.
     */
    object UserCancelled : ShareError()
    
    /**
     * An unknown error occurred.
     * 
     * @param message Descriptive error message
     */
    data class UnknownError(val message: String) : ShareError()
}