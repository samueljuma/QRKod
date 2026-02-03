package com.phillqins.qrcodegenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.phillqins.qrcodegenerator.ui.screens.qrcode.QRCodeScreen
import com.phillqins.qrcodegenerator.ui.theme.QRCodeGeneratorTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        setContent {
            QRCodeGeneratorTheme {
                QRCodeScreen(
                    viewModel = koinViewModel()
                )
            }
        }
    }
}
