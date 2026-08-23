package com.example.data.model

import android.net.Uri

data class CameraPhoto(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val dateTaken: Long,
    val sizeBytes: Long,
    val width: Int,
    val height: Int,
    val mimeType: String,
    val relativePath: String? = null,
    val bucketDisplayName: String? = null,
    val isSample: Boolean = false
) {
    val formattedSize: String
        get() {
            val kb = sizeBytes / 1024.0
            val mb = kb / 1024.0
            return if (mb >= 1.0) {
                String.format("%.1f MB", mb)
            } else {
                String.format("%.0f KB", kb)
            }
        }

    val resolutionText: String
        get() = if (width > 0 && height > 0) "${width}×${height}" else "HD Photo"

    /**
     * Checks if this photo is an already enhanced / remastered image produced by AI.
     */
    val isEnhancedImage: Boolean
        get() {
            val name = displayName.uppercase()
            val path = relativePath?.lowercase() ?: ""
            val bucket = bucketDisplayName?.lowercase() ?: ""
            val uriStr = uri.toString().lowercase()

            return name.startsWith("AI_ENHANCED_") ||
                    name.startsWith("AI_") ||
                    name.contains("_ENHANCED_") ||
                    name.contains("REMASTERED") ||
                    path.contains("camera_ai") ||
                    path.contains("pictures/camera_ai") ||
                    bucket.contains("camera_ai") ||
                    uriStr.contains("camera_ai")
        }

    /**
     * Strictly verifies whether this photo is captured from the native camera path (e.g. DCIM/, DCIM/Camera, etc.)
     * and is NOT an already enhanced/processed image.
     */
    val isNativeCameraPath: Boolean
        get() {
            if (isSample) return true
            // Strictly exclude any already enhanced or processed photos
            if (isEnhancedImage) return false

            val path = relativePath?.lowercase() ?: ""
            val bucket = bucketDisplayName?.lowercase() ?: ""
            val uriStr = uri.toString().lowercase()

            // Must strictly be inside DCIM directory or native camera bucket
            val hasDcimPath = path.startsWith("dcim") || path.contains("/dcim/") || path.contains("dcim/")
            val hasDcimBucket = bucket.contains("camera") || bucket.contains("dcim") || bucket == "100media" || bucket == "100andro"
            val hasDcimUri = uriStr.contains("dcim")

            val isExcludedFolder = path.contains("pictures") || path.contains("download") ||
                    path.contains("screenshot") || path.contains("whatsapp") || path.contains("telegram")

            return (hasDcimPath || hasDcimBucket || hasDcimUri) && !isExcludedFolder
        }

    val storageLocationLabel: String
        get() {
            return when {
                isSample -> "DCIM/Camera (Demo)"
                !relativePath.isNullOrBlank() -> relativePath.trimEnd('/')
                !bucketDisplayName.isNullOrBlank() -> "DCIM/${bucketDisplayName}"
                else -> "DCIM/Camera"
            }
        }

    val signature: String
        get() = "${uri}_${displayName}_${sizeBytes}_${dateTaken}"
}
