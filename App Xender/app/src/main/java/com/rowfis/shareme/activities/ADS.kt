package com.rowfis.shareme.activities

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.facebook.ads.*
import com.rowfis.shareme.R

class ADS : AppCompatActivity() {
    private var nativeAd: NativeAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a_d_s)
        AudienceNetworkAds.initialize(this)
        nativeAd = NativeAd(this, "YPUR ID")
        nativeAd!!.setAdListener(object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {}
            override fun onError(ad: Ad, adError: AdError) {}
            override fun onAdLoaded(ad: Ad) {
                val adview =
                    NativeAdView.render(this@ADS, nativeAd)
                val nativeContraller = findViewById<LinearLayout>(R.id.adViewNative)
                nativeContraller.addView(
                    adview,
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400)
                )
            }

            override fun onAdClicked(ad: Ad) {}
            override fun onLoggingImpression(ad: Ad) {}
        })
        nativeAd!!.loadAd()
    }
}