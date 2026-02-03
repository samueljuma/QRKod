package com.phillqins.qrcodegenerator.domain.repository

import android.app.Activity
import com.phillqins.qrcodegenerator.domain.model.PermissionResult

/**
 * Service interface for managing Android storage permissions across different API levels.
 * Handles the complexity of different permission requirements for different Android versions.
 */
interface PermissionHandler {
    
    /**
     * Checks if the app currently has the required storage permissions.
     * 
     * @return true if all required permissions are granted, false otherwise
     */
    fun hasStoragePermission(): Boolean
    
    /**
     * Determines if the app should request storage permissions based on the current API level.
     * 
     * @return true if permission request is needed, false otherwise
     */
    fun shouldRequestPermission(): Boolean
    
    /**
     * Gets the list of permissions required for the current API level.
     * 
     * @return Array of permission strings required for storage operations
     */
    fun getRequiredPermissions(): Array<String>
    
    /**
     * Requests the necessary storage permissions from the user.
     * 
     * @param activity The activity context for requesting permissions
     * @return PermissionResult indicating whether permissions were granted
     */
    suspend fun requestPermissions(activity: Activity): PermissionResult
}