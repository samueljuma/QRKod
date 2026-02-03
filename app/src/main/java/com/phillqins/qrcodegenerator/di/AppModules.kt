package com.phillqins.qrcodegenerator.di

import com.phillqins.qrcodegenerator.ui.screens.qrcode.QrCodeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModules = module {
    viewModelOf(::QrCodeViewModel)
}