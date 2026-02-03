package com.phillqins.qrcodegenerator.domain.model

import android.net.Uri

/**
 * Metadata information for a saved QR code file.
 * 
 * @param filename The name of the saved file
 * @param filePath The full path where the file was saved
 * @param uri The content URI of the saved file (for scoped storage)
 * @param size The size of the saved file in bytes
 * @param mimeType The MIME type of the saved file
 * @param createdAt The timestamp when the file was created
 */
data class SavedFileMetadata(
    val filename: String,
    val filePath: String,
    val uri: Uri?,
    val size: Long,
    val mimeType: String = "image/png",
    val createdAt: Long = System.currentTimeMillis()
)