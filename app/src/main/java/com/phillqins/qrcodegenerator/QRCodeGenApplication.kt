package com.phillqins.qrcodegenerator

import android.app.Application
import com.phillqins.qrcodegenerator.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class QRCodeGenApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@QRCodeGenApplication)
            modules(appModules)
        }

    }
}