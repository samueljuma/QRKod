package com.phillqins.qrcodegenerator.ui.screens.qrcode

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phillqins.qrcodegenerator.generateQRCode
import com.phillqins.qrcodegenerator.ui.screens.components.QRGenTextField
import com.phillqins.qrcodegenerator.ui.theme.QRCodeGeneratorTheme
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun QRCodeScreen(
    viewModel: QrCodeViewModel
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "QR Code Generator")
        QRGenTextField(
            state = state.qrContentState,
            hint = "Enter text to generate QR code",
        )
        QRCodeImage(
            size = 600,
            content = state.qrContentState.text.toString(),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

    }
}

@Composable
fun QRCodeImage(
    content: String,
    size: Int = 200,
    modifier: Modifier
) {
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var debouncedContent by remember { mutableStateOf("") }

    LaunchedEffect(content) {
        delay(500) // debounce
        debouncedContent = content
    }

    LaunchedEffect(debouncedContent, size) {
        val safeContent = debouncedContent.trim()

        qrCodeBitmap = if (safeContent.isNotEmpty()) {
            generateQRCode(safeContent, size)
        } else {
            null
        }
    }

    AnimatedContent(
        targetState = qrCodeBitmap,
        transitionSpec = {
            fadeIn(tween(220)) + scaleIn(initialScale = 0.92f) togetherWith
                    fadeOut(tween(150)) + scaleOut(targetScale = 1.05f)
        },
        label = "QR Animation"
    ) { bitmap ->
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = modifier.size(400.dp)
            )
        }
    }
}

@Preview
@Composable
private fun QRCodeScreenPreview() {
    QRCodeGeneratorTheme {
        QRCodeScreen(
            viewModel = koinViewModel()
        )
    }

}