package com.phillqins.qrcodegenerator.data.storage

import android.content.Context
import android.graphics.Bitmap
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class StorageServiceImplTest {

    private lateinit var context: Context
    private lateinit var storageService: StorageServiceImpl

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        storageService = StorageServiceImpl(context)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `generateUniqueFilename should return filename with timestamp format`() {
        // When
        val filename = storageService.generateUniqueFilename("TestQR")
        
        // Then
        assertTrue("Filename should start with prefix", filename.startsWith("TestQR_"))
        assertTrue("Filename should contain timestamp", 
            filename.matches(Regex("TestQR_\\d{8}_\\d{6}(_\\d{3})?")))
    }

    @Test
    fun `generateUniqueFilename should use default prefix when none provided`() {
        // When
        val filename = storageService.generateUniqueFilename()
        
        // Then
        assertTrue("Filename should start with default prefix", filename.startsWith("QRCode_"))
        assertTrue("Filename should contain timestamp", 
            filename.matches(Regex("QRCode_\\d{8}_\\d{6}(_\\d{3})?")))
    }

    @Test
    fun `generateUniqueFilename should generate different filenames for consecutive calls`() {
        // When
        val filename1 = storageService.generateUniqueFilename()
        Thread.sleep(1000) // Ensure different timestamp
        val filename2 = storageService.generateUniqueFilename()
        
        // Then
        assertNotEquals("Consecutive calls should generate different filenames", filename1, filename2)
    }

    @Test
    fun `generateUniqueFilename should handle rapid successive calls with collision avoidance`() {
        // When - Make multiple rapid calls within the same second
        val filenames = mutableSetOf<String>()
        repeat(5) {
            filenames.add(storageService.generateUniqueFilename())
        }
        
        // Then
        assertEquals("All filenames should be unique", 5, filenames.size)
        
        // Verify counter format for subsequent calls
        val filenameList = filenames.toList().sorted()
        if (filenameList.size > 1) {
            // First filename might not have counter, subsequent ones should
            for (i in 1 until filenameList.size) {
                assertTrue("Subsequent filenames should have counter suffix", 
                    filenameList[i].matches(Regex("QRCode_\\d{8}_\\d{6}_\\d{3}")))
            }
        }
    }

    @Test
    fun `generateUniqueFilename should handle custom prefix correctly`() {
        // Given
        val customPrefix = "MyCustomQR"
        
        // When
        val filename = storageService.generateUniqueFilename(customPrefix)
        
        // Then
        assertTrue("Filename should start with custom prefix", filename.startsWith("${customPrefix}_"))
        assertTrue("Filename should contain timestamp", 
            filename.matches(Regex("${customPrefix}_\\d{8}_\\d{6}(_\\d{3})?")))
    }

    @Test
    fun `generateUniqueFilename should handle empty prefix correctly`() {
        // Given
        val emptyPrefix = ""
        
        // When
        val filename = storageService.generateUniqueFilename(emptyPrefix)
        
        // Then
        assertTrue("Filename should start with underscore for empty prefix", filename.startsWith("_"))
        assertTrue("Filename should contain timestamp", 
            filename.matches(Regex("_\\d{8}_\\d{6}(_\\d{3})?")))
    }

    @Test
    fun `generateUniqueFilename should handle special characters in prefix`() {
        // Given
        val specialPrefix = "QR-Code_Test"
        
        // When
        val filename = storageService.generateUniqueFilename(specialPrefix)
        
        // Then
        assertTrue("Filename should start with special prefix", filename.startsWith("${specialPrefix}_"))
        assertTrue("Filename should contain timestamp", 
            filename.matches(Regex("QR-Code_Test_\\d{8}_\\d{6}(_\\d{3})?")))
    }
}