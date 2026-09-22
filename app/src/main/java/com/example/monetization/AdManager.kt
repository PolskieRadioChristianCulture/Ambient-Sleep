package com.example.monetization

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

object AdManager {
    private const val TAG = "AdManager"

    // Standard AdMob Test IDs (guaranteed to load and prevent crashes during review)
    private const val TEST_APP_OPEN_ID = "ca-app-pub-3940256099942544/9257395921"
    private const val TEST_REWARDED_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/5354046379"

    var appOpenAd: AppOpenAd? = null
        private set
    var rewardedInterstitialAd: RewardedInterstitialAd? = null
        private set

    private var isInitializing = false
    private var isAdLoading = false

    fun initialize(context: Context) {
        if (isInitializing) return
        isInitializing = true
        Log.d(TAG, "Initializing Google Mobile Ads SDK...")
        MobileAds.initialize(context) { status ->
            Log.d(TAG, "MobileAds initialization complete. Status: $status")
            // Prefetch ads
            loadAppOpenAd(context) {}
            loadRewardedInterstitial(context)
        }
    }

    fun loadAppOpenAd(context: Context, onLoadedOrFailed: () -> Unit) {
        if (appOpenAd != null) {
            onLoadedOrFailed()
            return
        }

        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            TEST_APP_OPEN_ID,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    Log.d(TAG, "App Open Ad loaded successfully.")
                    onLoadedOrFailed()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(TAG, "App Open Ad failed to load: ${error.message}")
                    appOpenAd = null
                    onLoadedOrFailed()
                }
            }
        )
    }

    fun showAppOpenAd(activity: Activity, onAdDismissed: () -> Unit) {
        val ad = appOpenAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "App Open Ad dismissed.")
                    appOpenAd = null
                    // Prefetch for next startup or resume
                    loadAppOpenAd(activity.applicationContext) {}
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    Log.e(TAG, "App Open Ad failed to show: ${error.message}")
                    appOpenAd = null
                    onAdDismissed()
                }
            }
            ad.show(activity)
        } else {
            Log.d(TAG, "App Open Ad not ready, skipping.")
            onAdDismissed()
        }
    }

    fun loadRewardedInterstitial(context: Context) {
        if (rewardedInterstitialAd != null || isAdLoading) return
        isAdLoading = true

        val request = AdRequest.Builder().build()
        RewardedInterstitialAd.load(
            context,
            TEST_REWARDED_INTERSTITIAL_ID,
            request,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                    isAdLoading = false
                    Log.d(TAG, "Rewarded Interstitial Ad loaded.")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(TAG, "Rewarded Interstitial failed to load: ${error.message}")
                    rewardedInterstitialAd = null
                    isAdLoading = false
                }
            }
        )
    }

    fun showRewardedInterstitial(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onDismissedOrError: () -> Unit
    ) {
        val ad = rewardedInterstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Rewarded Interstitial dismissed.")
                    rewardedInterstitialAd = null
                    loadRewardedInterstitial(activity.applicationContext) // Reload
                    onDismissedOrError()
                }

                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    Log.e(TAG, "Rewarded Interstitial failed to show: ${error.message}")
                    rewardedInterstitialAd = null
                    loadRewardedInterstitial(activity.applicationContext) // Reload
                    onDismissedOrError()
                }
            }

            ad.show(activity, OnUserEarnedRewardListener { rewardItem ->
                Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned()
            })
        } else {
            Log.w(TAG, "Rewarded Interstitial was not ready, loading and triggering mock unlock fallback.")
            loadRewardedInterstitial(activity.applicationContext)
            // Fallback so offline/delayed-ad user experience doesn't break
            onRewardEarned()
        }
    }
}
