package com.phillqins.qrcodegenerator.data.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.phillqins.qrcodegenerator.domain.model.PermissionResult
import com.phillqins.qrcodegenerator.domain.repository.PermissionHandler
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Implementation of PermissionHandler that manages Android storage permissions
 * across different API levels with proper compatibility handling.
 * 
 * API Level Compatibility:
 * - API 21-22: No runtime permissions required
 * - API 23-28: WRITE_EXTERNAL_STORAGE permission required
 * - API 29+: Scoped storage, no legacy permissions needed
 */
class PermissionHandlerImpl(
    private val context: Context
) : PermissionHandler {

    companion object {
        // API levels for different permission strategies
        private const val API_LEVEL_RUNTIME_PERMISSIONS = 23
        private const val API_LEVEL_SCOPED_STORAGE = 29
    }

    /**
     * Checks if the app currently has the required storage permissions.
     * 
     * @return true if all required permissions are granted, false otherwise
     */
    override fun hasStoragePermission(): Boolean {
        return when {
            // API 29+ uses scoped storage, no legacy permissions needed
            Build.VERSION.SDK_INT >= API_LEVEL_SCOPED_STORAGE -> true
            
            // API 23-28 requires WRITE_EXTERNAL_STORAGE permission
            Build.VERSION.SDK_INT >= API_LEVEL_RUNTIME_PERMISSIONS -> {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
            
            // API 21-22 doesn't require runtime permissions
            else -> true
        }
    }

    /**
     * Determines if the app should request storage permissions based on the current API level.
     * 
     * @return true if permission request is needed, false otherwise
     */
    override fun shouldRequestPermission(): Boolean {
        return when {
            // API 29+ uses scoped storage, no permission request needed
            Build.VERSION.SDK_INT >= API_LEVEL_SCOPED_STORAGE -> false
            
            // API 23-28 may need permission request if not already granted
            Build.VERSION.SDK_INT >= API_LEVEL_RUNTIME_PERMISSIONS -> !hasStoragePermission()

            
            // API 21-22 doesn't require runtime permissions
            else -> false
        }
    }

    /**
     * Gets the list of permissions required for the current API level.
     * 
     * @return Array of permission strings required for storage operations
     */
    override fun getRequiredPermissions(): Array<String> {
        return when {
            // API 29+ uses scoped storage, no legacy permissions needed
            Build.VERSION.SDK_INT >= API_LEVEL_SCOPED_STORAGE -> emptyArray()
            
            // API 23-28 requires WRITE_EXTERNAL_STORAGE permission
            Build.VERSION.SDK_INT >= API_LEVEL_RUNTIME_PERMISSIONS -> {
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            
            // API 21-22 doesn't require runtime permissions
            else -> emptyArray()
        }
    }

    /**
     * Requests the necessary storage permissions from the user.
     * This is a simplified implementation that returns the current permission state.
     * In a real implementation, this would integrate with the Activity Result API
     * or use a permission request library like Accompanist Permissions.
     * 
     * @param activity The activity context for requesting permissions
     * @return PermissionResult indicating whether permissions were granted
     */
    override suspend fun requestPermissions(activity: Activity): PermissionResult {
        // If no permissions are required for this API level, return granted
        if (!shouldRequestPermission()) {
            return PermissionResult(granted = true, shouldShowRationale = false)
        }

        val requiredPermissions = getRequiredPermissions()
        if (requiredPermissions.isEmpty()) {
            return PermissionResult(granted = true, shouldShowRationale = false)
        }

        // Check if we should show rationale for any of the permissions
        val shouldShowRationale = requiredPermissions.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }

        // If permissions are already granted, return success
        if (hasStoragePermission()) {
            return PermissionResult(granted = true, shouldShowRationale = false)
        }

        // For this implementation, we'll return the current permission state
        // In a production app, this would trigger the actual permission request
        // using Activity Result API or a permission handling library
        return PermissionResult(
            granted = hasStoragePermission(),
            shouldShowRationale = shouldShowRationale
        )
    }
}