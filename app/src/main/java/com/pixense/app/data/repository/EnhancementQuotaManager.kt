package com.pixense.app.data.repository

import android.content.Context
import com.pixense.app.data.analytics.PixenseAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class EntitlementType {
    FREE,
    REWARDED
}

data class QuotaState(
    val freeRemaining: Int,
    val rewardedUsedToday: Int,
    val pendingRewardedCount: Int,
    val dailyRewardedLimitReached: Boolean
)

object QuotaConfig {
    const val FREE_ENHANCEMENTS_PER_DAY = 0
    const val MAX_REWARDED_ENHANCEMENTS_PER_DAY = 5
}

class EnhancementQuotaManager private constructor(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences("pixense_quota_prefs", Context.MODE_PRIVATE)

    private val _quotaState = MutableStateFlow(readQuotaStateFromPrefs())
    val quotaState: StateFlow<QuotaState> = _quotaState.asStateFlow()

    init {
        resetQuotaIfNewDay()
    }

    @Synchronized
    private fun resetQuotaIfNewDay(): Boolean {
        val todayStr = getCurrentDateString()
        val lastDateStr = prefs.getString("key_last_quota_date", "")
        if (todayStr != lastDateStr) {
            prefs.edit().apply {
                putString("key_last_quota_date", todayStr)
                putInt("key_free_enhancements_used_today", 0)
                putInt("key_rewarded_enhancements_used_today", 0)
                apply()
            }
            updateQuotaStateFlow()
            return true
        }
        return false
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    @Synchronized
    private fun readQuotaStateFromPrefs(): QuotaState {
        val todayStr = getCurrentDateString()
        val lastDateStr = prefs.getString("key_last_quota_date", "")

        val isNewDay = todayStr != lastDateStr
        val freeUsed = if (isNewDay) 0 else prefs.getInt("key_free_enhancements_used_today", 0)
        val rewardedUsed = if (isNewDay) 0 else prefs.getInt("key_rewarded_enhancements_used_today", 0)
        val pendingCount = prefs.getInt("key_pending_rewarded_enhancement_count", 0)

        val freeRemaining = (QuotaConfig.FREE_ENHANCEMENTS_PER_DAY - freeUsed).coerceAtLeast(0)
        val limitReached = rewardedUsed >= QuotaConfig.MAX_REWARDED_ENHANCEMENTS_PER_DAY

        return QuotaState(
            freeRemaining = freeRemaining,
            rewardedUsedToday = rewardedUsed,
            pendingRewardedCount = pendingCount,
            dailyRewardedLimitReached = limitReached
        )
    }

    @Synchronized
    fun updateQuotaStateFlow() {
        _quotaState.value = readQuotaStateFromPrefs()
    }

    @Synchronized
    fun hasAvailableEntitlement(): Boolean {
        resetQuotaIfNewDay()
        val state = readQuotaStateFromPrefs()
        return state.freeRemaining > 0 || state.pendingRewardedCount > 0
    }

    @Synchronized
    fun consumeEntitlement(): EntitlementType? {
        resetQuotaIfNewDay()
        val state = readQuotaStateFromPrefs()

        if (state.freeRemaining > 0) {
            val freeUsed = prefs.getInt("key_free_enhancements_used_today", 0)
            prefs.edit().putInt("key_free_enhancements_used_today", freeUsed + 1).apply()
            updateQuotaStateFlow()
            
            // Check if this was the last free one, log quota exhausted event
            if (state.freeRemaining == 1) {
                PixenseAnalytics.logEvent("daily_free_quota_exhausted")
            }
            return EntitlementType.FREE
        }

        if (state.pendingRewardedCount > 0) {
            prefs.edit().putInt("key_pending_rewarded_enhancement_count", state.pendingRewardedCount - 1).apply()
            updateQuotaStateFlow()
            return EntitlementType.REWARDED
        }

        return null
    }

    @Synchronized
    fun addPendingRewardedEnhancement() {
        resetQuotaIfNewDay()
        val state = readQuotaStateFromPrefs()
        
        if (state.rewardedUsedToday >= QuotaConfig.MAX_REWARDED_ENHANCEMENTS_PER_DAY) {
            PixenseAnalytics.logEvent("daily_rewarded_limit_reached")
            return
        }

        prefs.edit().apply {
            putInt("key_pending_rewarded_enhancement_count", state.pendingRewardedCount + 1)
            putInt("key_rewarded_enhancements_used_today", state.rewardedUsedToday + 1)
            apply()
        }
        updateQuotaStateFlow()
        PixenseAnalytics.logEvent("rewarded_enhancement_granted")
    }

    @Synchronized
    fun refundRewardedEnhancement() {
        val currentPending = prefs.getInt("key_pending_rewarded_enhancement_count", 0)
        prefs.edit().putInt("key_pending_rewarded_enhancement_count", currentPending + 1).apply()
        updateQuotaStateFlow()
    }

    companion object {
        @Volatile
        private var instance: EnhancementQuotaManager? = null

        fun getInstance(context: Context): EnhancementQuotaManager {
            return instance ?: synchronized(this) {
                instance ?: EnhancementQuotaManager(context).also { instance = it }
            }
        }
    }
}
