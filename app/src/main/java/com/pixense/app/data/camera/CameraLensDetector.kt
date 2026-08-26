package com.pixense.app.data.camera

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import androidx.camera.core.CameraSelector
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Represents a discrete optical or hardware-supported camera lens preset.
 *
 * @property ratio The zoom ratio to apply to CameraControl.setZoomRatio()
 * @property label Formatted display label (e.g., "0.6x", "1x", "2x", "3x", "5x")
 * @property shortLabel Compact display label (e.g., "0.6", "1", "2", "3")
 * @property lensName Descriptive name for accessibility and HUD (e.g., "Ultra Wide", "Wide", "Telephoto")
 * @property isUltraWide True if this is an ultra-wide angle lens (< 1.0x)
 * @property isMain True if this is the default 1.0x wide-angle main lens
 * @property isTelephoto True if this is a telephoto / zoom lens (> 1.0x)
 * @property isOptical True if detected as an optical physical lens from hardware
 */
data class CameraLensPreset(
    val ratio: Float,
    val label: String,
    val shortLabel: String,
    val lensName: String,
    val isUltraWide: Boolean = false,
    val isMain: Boolean = false,
    val isTelephoto: Boolean = false,
    val isOptical: Boolean = true
)

object CameraLensDetector {
    private const val TAG = "CameraLensDetector"

    /**
     * Detects hardware lens capabilities and builds discrete lens presets (e.g. 0.6x, 1x, 2x, 3x, 5x)
     * based on physical camera hardware and CameraX ZoomState.
     */
    fun detectAvailableLenses(
        context: Context,
        lensFacing: Int,
        minZoomRatio: Float,
        maxZoomRatio: Float
    ): List<CameraLensPreset> {
        val detectedRatios = mutableListOf<Float>()
        val opticalRatios = mutableSetOf<Float>()

        // 1. Query physical cameras & focal lengths from Camera2 CameraManager
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            if (cameraManager != null) {
                val targetFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                    CameraCharacteristics.LENS_FACING_FRONT
                } else {
                    CameraCharacteristics.LENS_FACING_BACK
                }

                val matchingCameraIds = cameraManager.cameraIdList.filter { id ->
                    try {
                        val chars = cameraManager.getCameraCharacteristics(id)
                        chars.get(CameraCharacteristics.LENS_FACING) == targetFacing
                    } catch (_: Exception) {
                        false
                    }
                }

                // Check for physical focal lengths
                val focalLengths = mutableListOf<Float>()
                var hardwareMinZoom = minZoomRatio
                var hardwareMaxZoom = maxZoomRatio

                for (id in matchingCameraIds) {
                    try {
                        val chars = cameraManager.getCameraCharacteristics(id)

                        // Hardware zoom ratio range (API 30+)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            chars.get(CameraCharacteristics.CONTROL_ZOOM_RATIO_RANGE)?.let { range ->
                                hardwareMinZoom = minOf(hardwareMinZoom, range.lower)
                                hardwareMaxZoom = maxOf(hardwareMaxZoom, range.upper)
                            }
                        }

                        // Collect primary focal lengths
                        chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)?.forEach { f ->
                            if (f > 0f) focalLengths.add(f)
                        }

                        // Query logical multi-camera physical cameras (API 28+)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val physicalIds = chars.physicalCameraIds
                            for (physId in physicalIds) {
                                try {
                                    val physChars = cameraManager.getCameraCharacteristics(physId)
                                    physChars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)?.forEach { pf ->
                                        if (pf > 0f) focalLengths.add(pf)
                                    }
                                } catch (_: Exception) {}
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error querying characteristics for camera $id", e)
                    }
                }

                // Calculate optical zoom ratios relative to the base wide lens
                if (focalLengths.isNotEmpty()) {
                    val distinctFocals = focalLengths.distinct().sorted()
                    // Base wide focal length is typically around 4.0 - 6.0mm (standard ~24-28mm eq)
                    val baseFocal = distinctFocals.find { it in 3.5f..7.0f } ?: distinctFocals.firstOrNull()
                    if (baseFocal != null && baseFocal > 0f) {
                        for (f in distinctFocals) {
                            val rawRatio = f / baseFocal
                            val roundedRatio = when {
                                rawRatio < 0.45f -> ((rawRatio * 10).roundToInt() / 10f).coerceAtLeast(0.4f)
                                rawRatio in 0.45f..0.54f -> 0.5f
                                rawRatio in 0.55f..0.65f -> 0.6f
                                rawRatio in 0.66f..0.75f -> 0.7f
                                rawRatio in 0.76f..0.92f -> 0.8f
                                rawRatio in 0.93f..1.15f -> 1.0f
                                rawRatio in 1.8f..2.2f -> 2.0f
                                rawRatio in 2.7f..3.3f -> 3.0f
                                rawRatio in 4.5f..5.5f -> 5.0f
                                rawRatio in 9.0f..11.0f -> 10.0f
                                else -> ((rawRatio * 10).roundToInt() / 10f)
                            }
                            if (roundedRatio in minZoomRatio..maxZoomRatio) {
                                detectedRatios.add(roundedRatio)
                                opticalRatios.add(roundedRatio)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed detecting camera hardware lenses", e)
        }

        // 2. Add ultra-wide lens if minZoomRatio < 0.95f
        if (minZoomRatio < 0.95f) {
            val uwRatio = when {
                minZoomRatio <= 0.54f -> 0.5f
                minZoomRatio in 0.55f..0.64f -> 0.6f
                minZoomRatio in 0.65f..0.74f -> 0.7f
                minZoomRatio in 0.75f..0.85f -> 0.8f
                else -> ((minZoomRatio * 10).roundToInt() / 10f).coerceAtLeast(minZoomRatio)
            }
            val finalUw = maxOf(minZoomRatio, uwRatio)
            if (!detectedRatios.any { abs(it - finalUw) < 0.08f }) {
                detectedRatios.add(finalUw)
                opticalRatios.add(finalUw)
            }
        }

        // 3. Always ensure 1.0x (Main Wide Lens) is present if within range
        if (1.0f in minZoomRatio..maxZoomRatio) {
            if (!detectedRatios.any { abs(it - 1.0f) < 0.08f }) {
                detectedRatios.add(1.0f)
                opticalRatios.add(1.0f)
            }
        }

        // 4. Ensure standard telephoto presets based on maxZoomRatio if not already covered
        if (maxZoomRatio >= 2.0f && !detectedRatios.any { abs(it - 2.0f) < 0.15f }) {
            detectedRatios.add(2.0f)
        }

        if (maxZoomRatio >= 5.0f && !detectedRatios.any { abs(it - 5.0f) < 0.25f } && !detectedRatios.any { abs(it - 3.0f) < 0.25f }) {
            if (maxZoomRatio >= 5.0f) {
                detectedRatios.add(5.0f)
            }
        } else if (maxZoomRatio in 3.0f..4.9f && !detectedRatios.any { abs(it - 3.0f) < 0.25f }) {
            detectedRatios.add(3.0f)
        }

        // 5. Build clean, sorted, deduplicated CameraLensPreset list
        val distinctSortedRatios = detectedRatios
            .filter { it in minZoomRatio..maxZoomRatio }
            .distinctBy { (it * 10).roundToInt() }
            .sorted()

        // Fallback safety: at least 1.0x must be returned
        val finalRatios = if (distinctSortedRatios.isEmpty()) {
            listOf(minZoomRatio.coerceIn(minZoomRatio, maxZoomRatio))
        } else {
            distinctSortedRatios
        }

        return finalRatios.map { ratio ->
            val isUw = ratio < 0.95f
            val isMain = abs(ratio - 1.0f) < 0.08f
            val isTele = ratio > 1.08f

            val label = formatLensLabel(ratio)
            val shortLabel = formatShortLabel(ratio)
            val lensName = when {
                isUw -> "Ultra Wide"
                isMain -> "Wide"
                ratio >= 5.0f -> "Super Telephoto"
                isTele -> "Telephoto"
                else -> "Standard"
            }

            CameraLensPreset(
                ratio = ratio,
                label = label,
                shortLabel = shortLabel,
                lensName = lensName,
                isUltraWide = isUw,
                isMain = isMain,
                isTelephoto = isTele,
                isOptical = opticalRatios.any { abs(it - ratio) < 0.08f } || isMain
            )
        }
    }

    /**
     * Formats a zoom ratio into standard camera label: e.g. "0.6x", "1x", "2x", "3x", "5x".
     */
    fun formatLensLabel(ratio: Float): String {
        return if (ratio < 1.0f) {
            String.format(Locale.US, "%.1fx", ratio)
        } else if (abs(ratio - ratio.roundToInt()) < 0.05f) {
            "${ratio.roundToInt()}x"
        } else {
            String.format(Locale.US, "%.1fx", ratio)
        }
    }

    /**
     * Formats a zoom ratio into a short compact label: e.g. ".6", "1", "2", "3".
     */
    fun formatShortLabel(ratio: Float): String {
        return if (ratio < 1.0f) {
            String.format(Locale.US, "%.1f", ratio).removePrefix("0")
        } else if (abs(ratio - ratio.roundToInt()) < 0.05f) {
            "${ratio.roundToInt()}"
        } else {
            String.format(Locale.US, "%.1f", ratio)
        }
    }
}
