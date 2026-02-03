package com.phillqins.qrcodegenerator.ui.screens.qrcode

import android.graphics.Bitmap
import androidx.compose.foundation.text.input.TextFieldState

data class QRUiState(
    val qrContentState: TextFieldState = TextFieldState(),
    val isGenerating: Boolean = false,
    val downloadState: DownloadState = DownloadState.Idle,
    val downloadMessage: String? = null,
    val currentBitmap: Bitmap? = null
)

/**
 * Represents the different states of a download operation.
 */
sealed class DownloadState {
    /**
     * No download operation is currently active.
     */
    object Idle : DownloadState()
    
    /**
     * A download operation is currently in progress.
     */
    object InProgress : DownloadState()
    
    /**
     * The download operation completed successfully.
     */
    object Success : DownloadState()
    
    /**
     * The download operation failed with an error.
     */
    object Error : DownloadState()
}
