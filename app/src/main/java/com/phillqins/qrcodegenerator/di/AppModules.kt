package com.phillqins.qrcodegenerator.di

import com.phillqins.qrcodegenerator.data.permission.PermissionHandlerImpl
import com.phillqins.qrcodegenerator.data.share.ShareServiceImpl
import com.phillqins.qrcodegenerator.data.storage.StorageServiceImpl
import com.phillqins.qrcodegenerator.data.usecase.DownloadQRCodeUseCaseImpl
import com.phillqins.qrcodegenerator.data.usecase.ShareQRCodeUseCaseImpl
import com.phillqins.qrcodegenerator.domain.repository.PermissionHandler
import com.phillqins.qrcodegenerator.domain.repository.ShareService
import com.phillqins.qrcodegenerator.domain.repository.StorageService
import com.phillqins.qrcodegenerator.domain.usecase.DownloadQRCodeUseCase
import com.phillqins.qrcodegenerator.domain.usecase.ShareQRCodeUseCase
import com.phillqins.qrcodegenerator.ui.screens.onboarding.OnboardingViewModel
import com.phillqins.qrcodegenerator.ui.screens.qrcode.QrCodeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModules = module {
    // Permission handling
    single<PermissionHandler> { PermissionHandlerImpl(androidContext()) }
    
    // Storage service
    single<StorageService> { StorageServiceImpl(androidContext()) }
    
    // Share service
    single<ShareService> { ShareServiceImpl(androidContext(), get()) }
    
    // Use cases
    single<DownloadQRCodeUseCase> { 
        DownloadQRCodeUseCaseImpl(
            storageService = get(),
            permissionHandler = get(),
            activity = null // Activity will be handled differently for permission requests
        )
    }
    
    single<ShareQRCodeUseCase> { 
        ShareQRCodeUseCaseImpl(shareService = get())
    }
    
    // ViewModels
    viewModelOf(::QrCodeViewModel)
    viewModelOf(::OnboardingViewModel)
}