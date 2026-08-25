package com.example

import android.content.Context
import androidx.camera.core.CameraSelector
import com.example.data.camera.CameraLensDetector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testLensLabelFormatting() {
    assertEquals("0.6x", CameraLensDetector.formatLensLabel(0.6f))
    assertEquals("0.5x", CameraLensDetector.formatLensLabel(0.5f))
    assertEquals("1x", CameraLensDetector.formatLensLabel(1.0f))
    assertEquals("2x", CameraLensDetector.formatLensLabel(2.0f))
    assertEquals("3x", CameraLensDetector.formatLensLabel(3.0f))
    assertEquals("5x", CameraLensDetector.formatLensLabel(5.0f))
  }

  @Test
  fun testShortLabelFormatting() {
    assertEquals(".6", CameraLensDetector.formatShortLabel(0.6f))
    assertEquals(".5", CameraLensDetector.formatShortLabel(0.5f))
    assertEquals("1", CameraLensDetector.formatShortLabel(1.0f))
    assertEquals("2", CameraLensDetector.formatShortLabel(2.0f))
  }

  /*@Test
  fun testDetectAvailableLensesWithUltraWide() {
    val context = mock(Context::class.java)
    val lenses = CameraLensDetector.detectAvailableLenses(
      context = context,
      lensFacing = CameraSelector.LENS_FACING_BACK,
      minZoomRatio = 0.6f,
      maxZoomRatio = 8.0f
    )

    assertTrue(lenses.any { it.label == "0.6x" && it.isUltraWide })
    assertTrue(lenses.any { it.label == "1x" && it.isMain })
    assertTrue(lenses.any { it.label == "2x" && it.isTelephoto })
  }*/
}

