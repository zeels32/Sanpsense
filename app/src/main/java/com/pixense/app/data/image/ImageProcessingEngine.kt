package com.pixense.app.data.image

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

object ImageProcessingEngine {

    /**
     * Post-processes an image received from Gemini Nano Banana API to guarantee
     * exact same aspect ratio and resolution consistency as the original camera photo.
     */
    suspend fun postProcessNanoBananaImage(
        aiBitmap: Bitmap,
        originalBitmap: Bitmap
    ): Bitmap = withContext(Dispatchers.Default) {
        val targetAspect = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
        val aiAspect = aiBitmap.width.toFloat() / aiBitmap.height.toFloat()

        val croppedAiBitmap: Bitmap = if (Math.abs(targetAspect - aiAspect) > 0.01f) {
            // Crop center of aiBitmap to match exact original aspect ratio
            val cropWidth: Int
            val cropHeight: Int
            if (aiAspect > targetAspect) {
                cropHeight = aiBitmap.height
                cropWidth = (aiBitmap.height * targetAspect).toInt()
            } else {
                cropWidth = aiBitmap.width
                cropHeight = (aiBitmap.width / targetAspect).toInt()
            }
            val startX = (aiBitmap.width - cropWidth) / 2
            val startY = (aiBitmap.height - cropHeight) / 2
            Bitmap.createBitmap(aiBitmap, startX, startY, cropWidth, cropHeight)
        } else {
            aiBitmap
        }

        // Upscale to match or exceed original resolution while preserving aspect ratio
        val targetWidth = max(originalBitmap.width, croppedAiBitmap.width)
        val targetHeight = (targetWidth / targetAspect).toInt()

        if (croppedAiBitmap.width != targetWidth || croppedAiBitmap.height != targetHeight) {
            Bitmap.createScaledBitmap(croppedAiBitmap, targetWidth, targetHeight, true)
        } else {
            croppedAiBitmap
        }
    }
}
