// Quick test to verify filename generation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

class FilenameGenerator {
    companion object {
        private const val FILENAME_DATE_FORMAT = "yyyyMMdd_HHmmss"
        private val filenameCounter = AtomicInteger(0)
        @Volatile
        private var lastTimestamp = 0L
    }

    fun generateUniqueFilename(prefix: String = "QRCode"): String {
        val currentTime = System.currentTimeMillis()
        val timestamp = SimpleDateFormat(FILENAME_DATE_FORMAT, Locale.getDefault())
            .format(Date(currentTime))
        
        synchronized(this) {
            val currentSecond = currentTime / 1000
            val lastSecond = lastTimestamp / 1000
            
            if (currentSecond != lastSecond) {
                filenameCounter.set(0)
                lastTimestamp = currentTime
                return "${prefix}_$timestamp"
            }
            
            val counter = filenameCounter.incrementAndGet()
            lastTimestamp = currentTime
            
            return "${prefix}_${timestamp}_${String.format("%03d", counter)}"
        }
    }
}

fun main() {
    val generator = FilenameGenerator()
    
    println("Testing filename generation:")
    
    // Test single call
    println("Single call: ${generator.generateUniqueFilename()}")
    
    // Test rapid successive calls
    println("\nRapid successive calls:")
    repeat(5) {
        println("Call $it: ${generator.generateUniqueFilename()}")
    }
    
    // Test with custom prefix
    println("\nWith custom prefix:")
    println("Custom: ${generator.generateUniqueFilename("MyQR")}")
    
    // Wait a second and test again
    Thread.sleep(1000)
    println("\nAfter 1 second delay:")
    println("Delayed: ${generator.generateUniqueFilename()}")
}