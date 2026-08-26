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

class RewardedAdManager private constructor(private val context: Context) {

    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

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
        if (BuildConfig.DEBUG){
            val configuration = RequestConfiguration.Builder().setTestDeviceIds(
                listOf(
                    "310EED0071ED1F1ABEBE19909BC7DE85",
                    "71E88520A9C3C9069EEA413417D8524A"
                )
            ).build()
            MobileAds.setRequestConfiguration(configuration);
        }
    }

    fun isAdLoaded(): Boolean {
        return rewardedAd != null
    }

    fun loadAd() {
        if (isLoading || rewardedAd != null) return
        isLoading = true
        PixenseAnalytics.logEvent("rewarded_ad_requested")

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "Ad failed to load: ${adError.message}")
                rewardedAd = null
                isLoading = false
                PixenseAnalytics.logEvent("rewarded_ad_load_failed", mapOf("error" to adError.message))
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(TAG, "Ad loaded successfully.")
                rewardedAd = ad
                isLoading = false
                PixenseAnalytics.logEvent("rewarded_ad_loaded")
            }
        })
    }

    fun showAd(activity: Activity, onRewardEarned: () -> Unit, onFailure: (String) -> Unit) {
        val ad = rewardedAd
        if (ad == null) {
            Log.w(TAG, "Ad was not ready when showAd was called.")
            onFailure("Ad not ready yet. Please try again in a moment.")
            loadAd() // Proactively try to load again
            return
        }

        PixenseAnalytics.logEvent("rewarded_ad_shown")

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d(TAG, "Ad clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed.")
                rewardedAd = null
                PixenseAnalytics.logEvent("rewarded_ad_dismissed")
                // Preload the next rewarded ad immediately after dismissal
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Ad failed to show: ${adError.message}")
                rewardedAd = null
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
