package com.phillqins.qrcodegenerator.ui.screens.qrcode

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class QrCodeViewModel: ViewModel() {
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

            }
            QRAction.OnShareClick -> {

            }
        }
    }

}
