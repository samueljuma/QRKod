package com.phillqins.qrcodegenerator.ui.screens.qrcode

import android.graphics.Bitmap

sealed interface QRAction {
    data object OnDownloadClick: QRAction
    data object OnShareClick: QRAction
    data object OnDismissDownloadMessage: QRAction
    data class OnBitmapGenerated(val bitmap: Bitmap?): QRAction
}
