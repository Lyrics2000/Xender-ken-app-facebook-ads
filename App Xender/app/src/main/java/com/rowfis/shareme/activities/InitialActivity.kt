package com.rowfis.shareme.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
/**
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
*/
import com.rowfis.shareme.R
import kotlinx.android.synthetic.main.activity_start.*



class InitialActivity :AppCompatActivity(){

    private val TAG: String = InitialActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)



        btnSendFiles.setOnClickListener {
            Handler().postDelayed({

                startActivity(Intent(this@InitialActivity, MainActivity::class.java))
            }, 2000)


        }


    }


}