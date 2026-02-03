package com.phillqins.qrcodegenerator.di

import com.phillqins.qrcodegenerator.data.permission.PermissionHandlerImpl
import com.phillqins.qrcodegenerator.data.storage.StorageServiceImpl
import com.phillqins.qrcodegenerator.data.usecase.DownloadQRCodeUseCaseImpl
import com.phillqins.qrcodegenerator.domain.repository.PermissionHandler
import com.phillqins.qrcodegenerator.domain.repository.StorageService
import com.phillqins.qrcodegenerator.domain.usecase.DownloadQRCodeUseCase
import com.phillqins.qrcodegenerator.ui.screens.qrcode.QrCodeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModules = module {
    // Permission handling
    single<PermissionHandler> { PermissionHandlerImpl(androidContext()) }
    
    // Storage service
    single<StorageService> { StorageServiceImpl(androidContext()) }
    
    // Use cases
    single<DownloadQRCodeUseCase> { 
        DownloadQRCodeUseCaseImpl(
            storageService = get(),
            permissionHandler = get(),
            activity = null // Activity will be handled differently for permission requests
        )
    }
    
    // ViewModels
    viewModelOf(::QrCodeViewModel)
}