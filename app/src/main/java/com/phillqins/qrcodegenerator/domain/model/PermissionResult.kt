package com.phillqins.qrcodegenerator.domain.model

/**
 * Represents the result of a permission request operation.
 * 
 * @param granted Whether the permission was granted
 * @param shouldShowRationale Whether the app should show rationale for the permission request
 */
data class PermissionResult(
    val granted: Boolean,
    val shouldShowRationale: Boolean = false
)