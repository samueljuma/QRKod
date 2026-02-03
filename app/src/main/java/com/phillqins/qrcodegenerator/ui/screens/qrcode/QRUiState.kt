package com.phillqins.qrcodegenerator.ui.screens.qrcode

import androidx.compose.foundation.text.input.TextFieldState

data class QRUiState(
    val qrContentState: TextFieldState = TextFieldState(),
    val isGenerating: Boolean = false
)
