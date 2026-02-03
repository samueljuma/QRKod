package com.phillqins.qrcodegenerator.ui.screens.qrcode

sealed interface QREvent {
    data class ShowToast(val message: String): QREvent
}
