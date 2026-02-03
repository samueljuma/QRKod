package com.phillqins.qrcodegenerator.di

import android.content.Context
import com.phillqins.qrcodegenerator.data.storage.StorageServiceImpl
import com.phillqins.qrcodegenerator.domain.repository.StorageService
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class StorageServiceIntegrationTest : KoinTest {

    private val storageService: StorageService by inject()
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        
        startKoin {
            androidContext(mockContext)
            modules(appModules)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `StorageService should be properly injected from DI container`() {
        // Then
        assertNotNull("StorageService should be injected", storageService)
        assertTrue("StorageService should be instance of StorageServiceImpl", 
                  storageService is StorageServiceImpl)
    }

    @Test
    fun `StorageService should generate unique filenames`() {
        // When
        val filename1 = storageService.generateUniqueFilename("Test")
        Thread.sleep(1000) // Ensure different timestamp
        val filename2 = storageService.generateUniqueFilename("Test")
        
        // Then
        assertNotEquals("Generated filenames should be unique", filename1, filename2)
        assertTrue("Filename should start with prefix", filename1.startsWith("Test_"))
        assertTrue("Filename should start with prefix", filename2.startsWith("Test_"))
    }

    @Test
    fun `StorageService should handle default filename generation`() {
        // When
        val filename = storageService.generateUniqueFilename()
        
        // Then
        assertTrue("Default filename should start with QRCode", filename.startsWith("QRCode_"))
        assertTrue("Filename should match timestamp pattern", 
                  filename.matches(Regex("QRCode_\\d{8}_\\d{6}")))
    }
}