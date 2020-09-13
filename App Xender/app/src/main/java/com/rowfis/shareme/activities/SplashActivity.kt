package com.rowfis.shareme.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.facebook.ads.*
import com.rowfis.shareme.R
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.activity_start.*
import java.util.*


class SplashActivity:AppCompatActivity() {

    private var interstitialAd: InterstitialAd? = null
    private var nativead: NativeAd? = null
    private val TAG = MainActivity::class.java.simpleName

    private val REQUIRED_DANGEROUS_PERMISSIONS: List<String> = ArrayList()
    private val SHOWING_CONSENT_DIALOG_KEY = "ShowingConsentDialog"

    private var isRunning = false
    private var nativeAd: NativeAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        isRunning = true
        AudienceNetworkAds.initialize(this)
        nativeAd = NativeAd(this, getString(R.string.native_ads_fb))
        nativeAd!!.setAdListener(object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {}
            override fun onError(ad: Ad, adError: AdError) {}
            override fun onAdLoaded(ad: Ad) {
                val adview =
                    NativeAdView.render(this@SplashActivity, nativeAd)
                val nativeContraller = findViewById<LinearLayout>(R.id.adViewNative)
                nativeContraller.addView(
                    adview,
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800)
                )
            }

            override fun onAdClicked(ad: Ad) {

            }
            override fun onLoggingImpression(ad: Ad) {

            }
        })
        nativeAd!!.loadAd()



        showAds()

        btnSendFile.setOnClickListener {



            Handler().postDelayed({

                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }, 500)


        }



    }

    private fun showAds() {
        // Instantiate an InterstitialAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        interstitialAd = InterstitialAd(this, getString(R.string.interstitial_ads_fb))
        interstitialAd!!.setAdListener(object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.")
            }

            override fun onInterstitialDismissed(ad: Ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.")
            }

            override fun onError(ad: Ad, adError: AdError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.errorMessage)
            }

            override fun onAdLoaded(ad: Ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!")
                // Show the ad
                interstitialAd!!.show()
            }

            override fun onAdClicked(ad: Ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!")
            }

            override fun onLoggingImpression(ad: Ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!")
            }
        })
        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd!!.loadAd()
    }

    override fun onStart() {
        super.onStart()
        isRunning=true
    }
    override fun onStop() {
        super.onStop()
        isRunning=false
    }



}