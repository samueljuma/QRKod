package com.phillqins.qrcodegenerator

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun generateQRCode(content: String, size: Int): Bitmap? {
    return withContext(Dispatchers.Default) {
        try {
            if (content.isBlank()) return@withContext null

            val bitMatrix = QRCodeWriter()
                .encode(content, BarcodeFormat.QR_CODE, size, size)

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            val pixels = IntArray(size * size)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    pixels[y * size + x] =
                        if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }

            bitmap.setPixels(pixels, 0, size, 0, 0, size, size)

            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }
}
