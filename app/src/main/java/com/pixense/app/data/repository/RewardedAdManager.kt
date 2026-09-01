package com.pixense.app.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.pixense.app.BuildConfig
import com.pixense.app.data.analytics.PixenseAnalytics
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RewardedAdManager private constructor(private val context: Context) {

    private var rewardedAd: RewardedAd? = null

    private val _isAdLoaded = MutableStateFlow(false)
    val isAdLoaded: StateFlow<Boolean> = _isAdLoaded.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val pendingLoadListeners = mutableListOf<Pair<(() -> Unit)?, ((String) -> Unit)?>>()

    // Official Google Test Rewarded Ad Unit ID
    // https://developers.google.com/admob/android/test-ads#demo-units
    private val testAdUnitId = "ca-app-pub-3940256099942544/5224354917"
    
    // Production Ad Unit ID (configurable, e.g. from String resources or BuildConfig)
    private val prodAdUnitId = "ca-app-pub-1464253620326405/4111138926"

    private val adUnitId: String
        get() = if (BuildConfig.DEBUG) testAdUnitId else prodAdUnitId

    init {
        // Initialize Mobile Ads SDK
        MobileAds.initialize(context) { status ->
            Log.d(TAG, "Mobile Ads SDK Initialized: ${status.adapterStatusMap}")
            // Load the first ad proactively
            loadAd()
        }
        if (BuildConfig.DEBUG) {
            val configuration = RequestConfiguration.Builder().setTestDeviceIds(
                listOf(
                    "310EED0071ED1F1ABEBE19909BC7DE85",
                    "71E88520A9C3C9069EEA413417D8524A"
                )
            ).build()
            MobileAds.setRequestConfiguration(configuration)
        }
    }

    fun isAdLoaded(): Boolean {
        return rewardedAd != null
    }

    fun loadAd(onLoaded: (() -> Unit)? = null, onFailed: ((String) -> Unit)? = null) {
        if (rewardedAd != null) {
            _isAdLoaded.value = true
            _isLoading.value = false
            onLoaded?.invoke()
            return
        }

        synchronized(pendingLoadListeners) {
            if (onLoaded != null || onFailed != null) {
                pendingLoadListeners.add(Pair(onLoaded, onFailed))
            }
        }

        if (_isLoading.value) return
        _isLoading.value = true
        PixenseAnalytics.logEvent("rewarded_ad_requested")

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "Ad failed to load: ${adError.message}")
                rewardedAd = null
                _isLoading.value = false
                _isAdLoaded.value = false
                PixenseAnalytics.logEvent("rewarded_ad_load_failed", mapOf("error" to adError.message))

                val listeners = synchronized(pendingLoadListeners) {
                    val list = ArrayList(pendingLoadListeners)
                    pendingLoadListeners.clear()
                    list
                }
                listeners.forEach { it.second?.invoke(adError.message) }
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(TAG, "Ad loaded successfully.")
                rewardedAd = ad
                _isLoading.value = false
                _isAdLoaded.value = true
                PixenseAnalytics.logEvent("rewarded_ad_loaded")

                val listeners = synchronized(pendingLoadListeners) {
                    val list = ArrayList(pendingLoadListeners)
                    pendingLoadListeners.clear()
                    list
                }
                listeners.forEach { it.first?.invoke() }
            }
        })
    }

    fun showAd(activity: Activity, onRewardEarned: () -> Unit, onFailure: (String) -> Unit) {
        val currentAd = rewardedAd
        if (currentAd != null) {
            presentLoadedAd(currentAd, activity, onRewardEarned, onFailure)
        } else {
            // Ad not preloaded yet; load it now and present immediately once ready for continuous flow
            loadAd(
                onLoaded = {
                    val ad = rewardedAd
                    if (ad != null) {
                        presentLoadedAd(ad, activity, onRewardEarned, onFailure)
                    } else {
                        onFailure("Ad could not be prepared. Please try again.")
                    }
                },
                onFailed = { errorMsg ->
                    onFailure("Failed to load ad: $errorMsg")
                }
            )
        }
    }

    private fun presentLoadedAd(
        ad: RewardedAd,
        activity: Activity,
        onRewardEarned: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        rewardedAd = null
        _isAdLoaded.value = false
        PixenseAnalytics.logEvent("rewarded_ad_shown")

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d(TAG, "Ad clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed.")
                rewardedAd = null
                _isAdLoaded.value = false
                PixenseAnalytics.logEvent("rewarded_ad_dismissed")
                // Preload the next rewarded ad immediately after dismissal
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Ad failed to show: ${adError.message}")
                rewardedAd = null
                _isAdLoaded.value = false
                PixenseAnalytics.logEvent("rewarded_ad_show_failed", mapOf("error" to adError.message))
                onFailure("Failed to show ad: ${adError.message}")
                // Try preloading next ad
                loadAd()
            }

            override fun onAdImpression() {
                Log.d(TAG, "Ad impression logged.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed full screen content.")
            }
        }

        ad.show(activity) { rewardItem ->
            Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
            PixenseAnalytics.logEvent("rewarded_ad_completed")
            onRewardEarned()
        }
    }

    companion object {
        private const val TAG = "RewardedAdManager"

        @Volatile
        private var instance: RewardedAdManager? = null

        fun getInstance(context: Context): RewardedAdManager {
            return instance ?: synchronized(this) {
                instance ?: RewardedAdManager(context).also { instance = it }
            }
        }
    }
}
