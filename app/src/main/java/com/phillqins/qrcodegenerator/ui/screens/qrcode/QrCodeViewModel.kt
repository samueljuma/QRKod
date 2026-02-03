package com.phillqins.qrcodegenerator.ui.screens.qrcode

import android.graphics.Bitmap
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phillqins.qrcodegenerator.domain.model.DownloadError
import com.phillqins.qrcodegenerator.domain.usecase.DownloadQRCodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class QrCodeViewModel(
    private val downloadQRCodeUseCase: DownloadQRCodeUseCase
): ViewModel() {
    private val _state = MutableStateFlow(QRUiState())
    val state = _state.asStateFlow()

    init {
        snapshotFlow { _state.value.qrContentState}
            .onEach { textFieldState ->
                _state.value = _state.value.copy(
                    qrContentState = textFieldState,
                    isGenerating = textFieldState.text.isNotBlank()
                )
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: QRAction){
        when(action){
            QRAction.OnDownloadClick -> {
                handleDownloadClick()
            }
            QRAction.OnShareClick -> {

            }
            QRAction.OnDismissDownloadMessage -> {
                clearDownloadState()
            }
            is QRAction.OnBitmapGenerated -> {
                updateQRCodeBitmap(action.bitmap)
            }
        }
    }

    /**
     * Handles the download button click action.
     * Initiates the QR code download process if a bitmap is available.
     */
    private fun handleDownloadClick() {
        val currentBitmap = _state.value.currentBitmap
        
        if (currentBitmap == null) {
            updateDownloadState(
                downloadState = DownloadState.Error,
                message = "No QR code available to download. Please generate a QR code first."
            )
            return
        }
        
        // Start download process
        updateDownloadState(
            downloadState = DownloadState.InProgress,
            message = "Downloading QR code..."
        )
        
        viewModelScope.launch {
            try {
                val result = downloadQRCodeUseCase.execute(currentBitmap)
                
                if (result.success) {
                    updateDownloadState(
                        downloadState = DownloadState.Success,
                        message = "QR code saved successfully${result.filePath?.let { " to $it" } ?: ""}"
                    )
                } else {
                    val errorMessage = when (result.error) {
                        is DownloadError.PermissionDenied -> 
                            "Storage permission is required to save QR codes. Please grant permission and try again."
                        is DownloadError.InsufficientStorage -> 
                            "Insufficient storage space. Please free up some space and try again."
                        is DownloadError.FileSystemError -> 
                            "Unable to save file. Please check your device storage and try again."
                        is DownloadError.UnknownError -> 
                            "Download failed: ${result.error.message}"
                        null -> "Download failed due to an unknown error."
                    }
                    
                    updateDownloadState(
                        downloadState = DownloadState.Error,
                        message = errorMessage
                    )
                }
            } catch (e: Exception) {
                updateDownloadState(
                    downloadState = DownloadState.Error,
                    message = "Unexpected error occurred during download: ${e.message}"
                )
            }
        }
    }

    /**
     * Updates the QR code bitmap in the state.
     * This should be called when a new QR code is generated.
     * 
     * @param bitmap The generated QR code bitmap
     */
    fun updateQRCodeBitmap(bitmap: Bitmap?) {
        _state.value = _state.value.copy(currentBitmap = bitmap)
    }

    /**
     * Updates the download state and message.
     * 
     * @param downloadState The new download state
     * @param message The message to display to the user
     */
    private fun updateDownloadState(downloadState: DownloadState, message: String? = null) {
        _state.value = _state.value.copy(
            downloadState = downloadState,
            downloadMessage = message
        )
    }

    /**
     * Clears the download state and message.
     * This can be called to reset the download feedback UI.
     */
    fun clearDownloadState() {
        updateDownloadState(DownloadState.Idle, null)
    }

}
