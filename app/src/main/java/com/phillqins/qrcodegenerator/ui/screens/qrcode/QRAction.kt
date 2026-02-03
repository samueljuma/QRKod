package com.phillqins.qrcodegenerator.ui.screens.qrcode

sealed interface QRAction {
    data object OnDownloadClick: QRAction
    data object OnShareClick: QRAction
}
