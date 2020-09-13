package com.rowfis.shareme.activities;

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider

import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.rowfis.shareme.BuildConfig
import com.rowfis.shareme.R
import com.rowfis.shareme.adapter.GridAppsAdapter
import com.rowfis.shareme.adapter.IntentChooserAdapter
import com.rowfis.shareme.model.AppInfo
import com.rowfis.shareme.util.Util.shareThisApp
import com.rowfis.shareme.util.Utility.*
import com.facebook.ads.*
import kotlinx.android.synthetic.main.content_main.*


import java.io.File
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener, OnQueryTextListener {


    val CONTENT_STREAM = "android.intent.extra.STREAM"
    private val CONTENT_PACKAGE = "android.intent.extra.INSTALLER_PACKAGE_NAME"
    private val REQUEST_CODE = 100
    private lateinit var adapter: GridAppsAdapter
    private lateinit var appListTask: AsyncTask<Void, Void, ArrayList<AppInfo>>
    private var btn_send_apps: LinearLayout? = null
    private lateinit var mGridApps: GridView
    // private lateinit var selectedFileCount: TextView
    private lateinit var tvNoInstalledApps: TextView
    // private lateinit var tvSendFiles: TextView

    private lateinit var loadingContainer: RelativeLayout
    private var mFilter: SearchView? = null
    private lateinit var apps: ArrayList<AppInfo>
    private lateinit var fab: FloatingActionButton
    private lateinit var bottomBar: BottomAppBar
    private lateinit var fabButton: FloatingActionButton
    private lateinit var mCoordinatorLayout: CoordinatorLayout
    private lateinit var mNavigationView: NavigationView
    private lateinit var bottomDrawer: View
    private lateinit var bottomBehaviour: BottomSheetBehavior<View>

    private var interstitialAd: InterstitialAd? = null
    private val TAG = MainActivity::class.java.simpleName


    lateinit var mAdView : AdView


    @SuppressLint("StaticFieldLeak")
    private inner class AppListTask(activity: Activity) :
        AsyncTask<Void, Void, ArrayList<AppInfo>>() {


        override fun doInBackground(vararg voids: Void?): ArrayList<AppInfo>? {
            return getInstalledApps(this@MainActivity, 1)


        }


        override fun onPreExecute() {
            super.onPreExecute()
            mGridApps.visibility = View.INVISIBLE
            //  tv_no_installed_apps.setVisibility(View.INVISIBLE);
            //  tv_no_installed_apps.setVisibility(View.INVISIBLE);
            loadingContainer.visibility = View.VISIBLE


        }

        override fun onPostExecute(apps: ArrayList<AppInfo>) {
            super.onPostExecute(apps)

            loadingContainer.visibility = View.INVISIBLE
            if (apps.size <= 0) { // tv_no_installed_apps.setVisibility(View.VISIBLE);


            }
            adapter.list = apps
            mGridApps.visibility = View.VISIBLE


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_send)
        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this)


        // User interface
        mCoordinatorLayout = findViewById(R.id.coordinator_layout)
        bottomBar = findViewById(R.id.bar)
        fabButton = findViewById(R.id.fab)
        mNavigationView = findViewById(R.id.navigation_view)

        // recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
        //  fab = findViewById(R.id.floatButton)
        btn_send_apps = findViewById(R.id.btn_send_files)
        mGridApps = findViewById(R.id.grid_apps)
        loadingContainer = findViewById(R.id.loading_container)

        mAdView = AdView(this, getString(R.string.banner_ads_fb), AdSize.BANNER_HEIGHT_50)
        // Find the Ad Container
        val adContainer = findViewById<View>(R.id.adView) as LinearLayout
        // Add the ad view to your activity layout
        adContainer.addView(mAdView)
        // Request an ad
        mAdView!!.loadAd()



        // selectedFileCount = findViewById(R.id.selected_file_count)
        // tvNoInstalledApps = findViewById(R.id.tv_no_installed_apps)
        //val adContainer = findViewById<LinearLayout>(R.id.banner_container)
        //tvSendFiles = findViewById(R.id.tv_send_files)
        initGrid()
        getShareMultiFileIntent()

        setUpBottomDrawer()
        updateMenuItems()
        showAd()




    }


    private fun showAd() {
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




    /** Dismiss the BottomSheet when clicking outside of its boundaries */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            // If our bottom sheet is expanded
            if (bottomBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {
                // Get its layout
                val outRect = Rect()
                mNavigationView.getGlobalVisibleRect(outRect)
                // If we are touching outside of the rect, dismiss it
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt()))
                    bottomBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    /** Function for setting up all the bottom area */
    private fun setUpBottomDrawer() {
        bottomDrawer = mCoordinatorLayout.findViewById(R.id.bottom_drawer)
        bottomBehaviour = BottomSheetBehavior.from(bottomDrawer)
        bottomBehaviour.state = BottomSheetBehavior.STATE_HIDDEN

        bottomBehaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    ObjectAnimator.ofFloat(mNavigationView, "elevation", 0f)
                        .setDuration(500)
                        .start()
                } else {
                    ObjectAnimator.ofFloat(mNavigationView, "elevation", 16f)
                        .setDuration(250)
                        .start()
                }
            }
        })

        // When clicking on the menu bottom
        bottomBar.setNavigationOnClickListener {
            bottomBehaviour.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        // Bottom sheet
        mNavigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_rate_us -> {

                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + this.packageName)
                        )
                    )
                    showAd()

                    true
                }
                R.id.action_share_app -> {


                    shareThisApp(this)
                    showAd()
                    true
                }
                else -> false
            }
        }

        // Extra actions on the right for interacting with cards
        bottomBar.setOnMenuItemClickListener { i ->
            when (i.itemId) {
                R.id.delete -> {
                    val builder = MaterialAlertDialogBuilder(this@MainActivity)
                    // Set positive button
                    builder.setTitle(R.string.delete_dialog_title)
                    builder.setPositiveButton(R.string.delete) { _, _ ->

                        // Update menu

                    }
                    // Set negative button
                    builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.cancel()
                    }
                    // Show the dialog
                    builder.show()
                    true
                }
                R.id.share -> {

                    true
                }
                else -> {
                    false
                }
            }
        }


        // Set FAB button behaviour
        fabButton.setOnClickListener {
            // Here we need to understand whether we are recording or not.
            // If we are not recording we can send the intent for recording
            // otherwise we will try to stop the recording.
            if (isAnySelected()) {
                getShareMultiFileIntent()?.let { it1 -> showIntentDialog(it1) }
            } else {

                Toast.makeText(this, "Select Apps to share", Toast.LENGTH_SHORT).show()


            }
            showAd()
        }


    }


    private fun deselectAll() {
        val it: Iterator<AppInfo> = adapter.originalList.iterator()
        while (it.hasNext()) {
            it.next().isChecked = false
        }
        adapter.notifyDataSetChanged()
        setSelectedCount()
    }


    private fun onRefresh() {

        if (appListTask.status != AsyncTask.Status.RUNNING) {
            hideSoftKey(this)
            //this.mFilter.onActionViewCollapsed()
            deselectAll()
            appListTask = AppListTask(this).execute(*arrayOfNulls<Void>(0))
        }
    }


    private fun getShareMultiFileIntent(): Intent? {
        if (adapter.originalList == null || adapter.originalList.size == 0) {
            return null
        }
        val iterator: Iterator<AppInfo> = adapter.originalList.iterator()
        val files = java.util.ArrayList<Uri>()
        val names = java.util.ArrayList<String>()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (!(item == null || TextUtils.isEmpty(item.apkPath) || !item.isChecked)) {
                val file = File(item.apkPath)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val contentUri = FileProvider.getUriForFile(
                        applicationContext,
                        BuildConfig.CONTENT_PROVIDER_AUTHORITY,
                        file
                    )
                    files.add(contentUri)
                    names.add(item.packageName)
                } else {
                    val uri = Uri.fromFile(file)
                    files.add(uri)
                    names.add(item.packageName)
                }
            }
        }
        val intent = Intent("android.intent.action.SEND_MULTIPLE")
        intent.putExtra("android.intent.extra.TITLE", getString(R.string.share_apps_title))
        intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.share_apps_subject))
        intent.putParcelableArrayListExtra(CONTENT_STREAM, files)
        intent.putStringArrayListExtra(CONTENT_PACKAGE, names)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //intent.setData(uris, type)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        intent.type = "*/*"
        return intent
    }

    private fun performSearch(filter: String) {
        adapter.search(filter)
    }


    private fun isAnySelected(): Boolean {
        val it: Iterator<AppInfo> = adapter.originalList.iterator()
        while (it.hasNext()) {
            if (it.next().isChecked) {
                return true
            }
        }
        return false
    }

    private fun showIntentDialog(intent: Intent) {
        val view = LayoutInflater.from(this).inflate(R.layout.intent_chooser, null, false)
        val lv = view.findViewById<ListView>(R.id.lv_intent_chooser)
        val intentAdapter =
            IntentChooserAdapter(this, getQueryIntentActivities(this, intent))
        lv.adapter = intentAdapter
        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val alertDialog = builder.create()
        lv.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view1: View?, position: Int, id: Long ->
                alertDialog.dismiss()
                val pckNAME: String
                val appInfo = intentAdapter.getItem(position) as AppInfo
                pckNAME = appInfo.packageName
                //pckNAME = (IntentChooserAdapter)intentAdapter.getItem(position);
                val i = getShareMultiFileIntent()
                if (isApkInstalled(applicationContext, pckNAME)) {
                    i!!.setPackage(pckNAME)
                    adapter.resetItems()
                    setSelectedCount()
                    startActivity(i)
                } else {
                    Toast.makeText(applicationContext, "App not exist", Toast.LENGTH_SHORT).show()
                    intentAdapter.remove(position)
                }
            }
        alertDialog.show()
    }


    @SuppressLint("RestrictedApi")
    private fun setSelectedCount() {
        var total: Long = 0
        val it: Iterator<AppInfo> = adapter.originalList.iterator()
        while (it.hasNext()) {
            if (it.next().isChecked) {
                total++
            }
        }
        if (total == 0L) {
            //fab.visibility = View.GONE
            return
        } else {
            //fab.visibility = View.VISIBLE
        }
        //        btn_send_apps.setVisibility(View.VISIBLE);
//        btn_send_apps.setBackgroundColor(getResources().getColor(R.color.active_buttons_color));
//        tv_send_files.setText(getResources().getString(R.string.send_apk));
//        selected_file_count.setVisibility(View.VISIBLE);
//        selected_file_count.setText("(" + total + ")");
    }

    private fun initGrid() {
        apps = java.util.ArrayList()
        appListTask = AppListTask(this).execute(*arrayOfNulls<Void>(0))
        adapter = GridAppsAdapter(apps, applicationContext)
        mGridApps.adapter = adapter
        mGridApps.onItemClickListener =
            OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                val appInfo = adapter.getItem(position) as AppInfo
                appInfo.isChecked = !(adapter.getItem(position) as AppInfo).isChecked
                adapter.notifyDataSetChanged()
                setSelectedCount()
            }
        mGridApps.onItemLongClickListener =
            OnItemLongClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                showPopupMenu(view!!, position)
                false
            }
        // btn_send_apps.setOnClickListener(this);
        // fab.setOnClickListener(this)
    }

    private fun showPopupMenu(view: View, position: Int) {
        val popup = PopupMenu(this, view)
        val inflater = popup.menuInflater
        val appI = adapter.getItem(position) as AppInfo
        val pck = appI.packageName
        if (pck == packageName) {
            inflater.inflate(R.menu.menu_apk, popup.menu)
        } else {
            inflater.inflate(R.menu.menu_generic_apk, popup.menu)
        }
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_send_apk -> sendApk(this@MainActivity, appI)
                R.id.action_send_link -> sendLink(this@MainActivity, appI)
                R.id.action_app_info -> appInfo(this@MainActivity, appI)
                R.id.action_uninstall -> uninstallApp(this@MainActivity, appI)
                else -> return@setOnMenuItemClickListener false
            }
            false
        }
        popup.show()
    }


    private fun updateMenuItems() {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        // val searchItem= bottomBar.menu.findItem(R.id.app_bar_search)


        val refreshItem = bottomBar.menu.findItem(R.id.app_bar_refresh)
        refreshItem!!.setOnMenuItemClickListener {
            onRefresh()

            true
        }

//        if(searchItem!=null){
//            searchItem.setOnMenuItemClickListener {
//
//
//                mFilter = searchItem.actionView as SearchView
//                val searchEditText = mFilter!!.findViewById<View>(R.id.search_src_text) as EditText
//                //            searchEditText.setTextColor(getResources().getColor(R.color.colorAccent));
////            searchEditText.setHintTextColor(getResources().getColor(R.color.gws_color_material_amber_100));
//                mFilter!!.findViewById<View>(R.id.search_close_btn).setOnClickListener { v: View? ->
//                    searchEditText.setText("")
//                    mFilter!!.onActionViewCollapsed()
//                }
//                mFilter!!.setOnCloseListener { true }
//                mFilter!!.setOnSearchClickListener { v: View? -> }
//
//                if (mFilter!=null){
//                    mFilter!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))
//                    mFilter!!.isSubmitButtonEnabled = true
//                    mFilter!!.setOnQueryTextListener(this)
//
//                }
//
//
//
//                true }

//        }

//        else{
//
//        }


        refreshItem.setOnMenuItemClickListener {

            onRefresh()


            true
        }
//        deleteAction.isVisible = mVideoAdapter.selectedItems.size >= 1
//        shareAction.isVisible = mVideoAdapter.selectedItems.size >= 1


    }

    override fun onClick(v: View?) {


//        when (v!!.id) {
//            R.id.floatButton -> if (isAnySelected()) {
//                showIntentDialog(getShareMultiFileIntent()!!)
//                showAd()
//                break
//            } else {
//                Toast.makeText(applicationContext, getString(R.string.choose_apps_to_send), Toast.LENGTH_SHORT).show()
//                showAd()
//                break
//            }
//            else -> {
//            }
//        }


    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        hideSoftKey(this)
        performSearch(query!!)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        performSearch(newText!!)

        return true
    }


    override fun onDestroy() {
        if (appListTask.status == AsyncTask.Status.RUNNING) {
            appListTask.cancel(true)
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (isAnySelected()) {
            deselectAll()
        } else {
            super.onBackPressed()
        }
        showAd()
    }


}