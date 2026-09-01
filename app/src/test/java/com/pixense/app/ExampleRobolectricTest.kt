package com.pixense.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.pixense.app.data.repository.EnhancementQuotaManager
import com.pixense.app.data.repository.EntitlementType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Pixense – AI Camera", appName)
  }

  @Test
  fun `quota manager operates exclusively via rewarded entitlements`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val quotaManager = EnhancementQuotaManager.getInstance(context)

    // Clear any previous state
    val prefs = context.getSharedPreferences("pixense_quota_prefs", Context.MODE_PRIVATE)
    prefs.edit().clear().apply()
    quotaManager.updateQuotaStateFlow()

    // Initially no entitlement should be available
    assertFalse(quotaManager.hasAvailableEntitlement())
    assertNull(quotaManager.consumeEntitlement())

    // Add pending rewarded enhancement
    quotaManager.addPendingRewardedEnhancement()
    assertTrue(quotaManager.hasAvailableEntitlement())
    assertEquals(1, quotaManager.quotaState.value.pendingRewardedCount)

    // Consume entitlement
    val consumed = quotaManager.consumeEntitlement()
    assertEquals(EntitlementType.REWARDED, consumed)
    assertFalse(quotaManager.hasAvailableEntitlement())
    assertEquals(0, quotaManager.quotaState.value.pendingRewardedCount)

    // Refund entitlement
    quotaManager.refundRewardedEnhancement()
    assertTrue(quotaManager.hasAvailableEntitlement())
    assertEquals(1, quotaManager.quotaState.value.pendingRewardedCount)
  }
}

