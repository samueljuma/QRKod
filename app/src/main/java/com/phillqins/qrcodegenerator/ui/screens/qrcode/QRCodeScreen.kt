package com.phillqins.qrcodegenerator.ui.screens.qrcode

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phillqins.qrcodegenerator.R
import com.phillqins.qrcodegenerator.generateQRCode
import com.phillqins.qrcodegenerator.ui.screens.components.QRGenTextField
import com.phillqins.qrcodegenerator.ui.theme.QRCodeGeneratorTheme
import kotlinx.coroutines.delay

@Composable
fun QRCodeScreenRoot(
    viewModel: QrCodeViewModel
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    QRCodeScreen(
        state = state,
        onAction = viewModel::onAction
    )
}
@Composable
fun QRCodeScreen(
    state: QRUiState,
    onAction: (QRAction) -> Unit
){

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.qrkod),
            contentDescription = "QR Code Icon",
            modifier = Modifier.size(60.dp)
                .align(Alignment.CenterHorizontally)
        )
        QRGenTextField(
            state = state.qrContentState,
            hint = "Enter text to generate QR code",
        )
        QRCodeSection(
            qrCodeSize = 600,
            qrString = state.qrContentState.text.toString(),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            content = {
                DownloadShareBtns(
                    onDownloadClick = {onAction(QRAction.OnDownloadClick)},
                    onShareClick = {onAction(QRAction.OnShareClick)}
                )
            }
        )

    }
}

@Composable
fun DownloadShareBtns(
    onDownloadClick: () -> Unit,
    onShareClick: () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        TextButton(
            onClick = onDownloadClick,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Download")
        }
        TextButton(
            onClick = onShareClick,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Share")
        }

    }
}

@Composable
fun QRCodeSection(
    qrString: String,
    qrCodeSize: Int = 200,
    modifier: Modifier,
    content: (@Composable () -> Unit)? = null
) {
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var debouncedContent by remember { mutableStateOf("") }

    LaunchedEffect(qrString) {
        delay(500) // debounce
        debouncedContent = qrString
    }

    LaunchedEffect(debouncedContent, qrCodeSize) {
        val safeContent = debouncedContent.trim()

        qrCodeBitmap = if (safeContent.isNotEmpty()) {
            generateQRCode(safeContent, qrCodeSize)
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
            Column {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = modifier.size(400.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                content?.invoke()
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QRCodeScreenPreview() {
    QRCodeGeneratorTheme {
        QRCodeScreen(
            state = QRUiState(
                qrContentState = TextFieldState(initialText = "Juma")
            ),
            onAction = {}
        )
    }

}