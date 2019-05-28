package com.lory.library.uil.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.ui.callback.OnBaseActivityListener
import com.lory.library.ui.callback.OnBaseFragmentListener
import com.lory.library.ui.controller.AppPermissionController
import com.lory.library.ui.utils.MKRDialogUtil
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.R
import com.lory.library.uil.dto.DTOAlbumData
import com.lory.library.uil.dto.ImageData
import com.lory.library.uil.dto.Model
import com.lory.library.uil.provider.AsyncTaskProvider
import com.lory.library.uil.provider.FragmentProvider
import com.lory.library.uil.ui.fragment.FragmentGalleryAlbum
import com.lory.library.uil.utils.JsonUtil
import com.lory.library.uil.utils.Tracer

class GalleryActivity : AppCompatActivity(), OnBaseActivityListener, AppPermissionController.OnAppPermissionControllerListener, View.OnClickListener {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".GalleryActivity"
        private const val EXTRA_IMAGE_COUNT = "EXTRA_IMAGE_COUNT"
        private const val EXTRA_IS_COUNT_FIXED = "EXTRA_IS_COUNT_FIXED"
        internal const val EXTRA_IMAGE_DATA = "EXTRA_IMAGE_DATA"

        /**
         * Method to launch the Gallery Activity for result
         * @param activity
         * @param requestCode Code to be return in onActivityRresult
         * @param maxImageCount Number of Image to be requested
         * @param isCountFixed  If TRUE then forced user to pick this much of Image. If FALSE then user my choose less image too
         */
        fun launch(activity: Activity, requestCode: Int, maxImageCount: Int, isCountFixed: Boolean) {
            Tracer.debug(TAG, "launch : Request Code = $requestCode : Image Count = $maxImageCount")
            val intent = Intent(activity, GalleryActivity::class.java)
            intent.putExtra(EXTRA_IMAGE_COUNT, maxImageCount)
            intent.putExtra(EXTRA_IS_COUNT_FIXED, isCountFixed)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            activity.startActivityForResult(intent, requestCode)
        }

        /**
         * Method to parse the response Intent
         * @param data
         */
        fun parseResponseIntent(data: Intent?): ArrayList<ImageData> {
            Tracer.debug(TAG, "parseResponseIntent : ")
            val data = data?.getStringExtra(EXTRA_IMAGE_DATA) ?: "[]"
            val dtoImageLocationList = JsonUtil.toObjectTokenType<ArrayList<ImageData>>(data, false)
            return dtoImageLocationList
        }
    }

    private var dtoAlbumDataList: ArrayList<DTOAlbumData> = ArrayList()
    private var appPermissionController: AppPermissionController? = null
    private var asyncTaskProvider = AsyncTaskProvider()
    private var asyncCallBackFetchGalleryInfo = object : AsyncCallBack<ArrayList<DTOAlbumData>, Any> {
        override fun onProgress(progress: Any?) {
            MKRDialogUtil.dismissLoadingDialog()
        }

        override fun onSuccess(mkr: ArrayList<DTOAlbumData>?) {
            MKRDialogUtil.dismissLoadingDialog()
            dtoAlbumDataList = mkr ?: ArrayList()
            showAlbumFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        setSupportActionBar(findViewById<Toolbar>(R.id.activity_gallery_toolbar))
        onBaseActivitySetToolbar(layoutInflater.inflate(R.layout.toolbar, null))
        asyncTaskProvider.attachProvider()
        appPermissionController = AppPermissionController(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), this
        )
        findViewById<View>(R.id.toolbar_imageView_imageView_ok).setOnClickListener(this)
        val model = Model.getInstance()
        model.reset()
        model.maxPicCount = intent?.getIntExtra(EXTRA_IMAGE_COUNT, 1) ?: 1
        model.isMaxPicCountFixed = intent?.getBooleanExtra(EXTRA_IS_COUNT_FIXED, false) ?: false

        findViewById<TextView>(R.id.activity_gallery_textiew_message).text = if (model.isMaxPicCountFixed) {
            "USER SHOULD SELECT ${model.maxPicCount} IMAGES"
        } else {
            "USER SHOULD SELECT AT-MOST ${model.maxPicCount} IMAGES"
        }

        if (appPermissionController?.isHaveAllRequiredPermission() == true) {
            loadGalleryInfoList()
        } else {
            appPermissionController?.requestPermission()
        }
    }

    override fun onBackPressed() {
        Tracer.debug(TAG, "onBackPressed: ");
        var fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.activity_gallery_container)
        if (fragment != null && fragment is OnBaseFragmentListener && (fragment as OnBaseFragmentListener).onBackPressed()) {
            return
        }
        super.onBackPressed()
        fragment = supportFragmentManager.findFragmentById(R.id.activity_gallery_container)
        if (fragment != null && fragment is OnBaseFragmentListener) {
            (fragment as OnBaseFragmentListener).onPopFromBackStack()
        }
        if (supportFragmentManager.backStackEntryCount <= 0) {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        appPermissionController?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        asyncTaskProvider.detachProvider()
        super.onDestroy()
    }

    override fun onAppPermissionControllerListenerHaveAllRequiredPermission() {
        Tracer.debug(TAG, "onAppPermissionControllerListenerHaveAllRequiredPermission : ")
        loadGalleryInfoList()
    }

    override fun onBaseActivitySetScreenTitle(title: String) {
        Tracer.debug(TAG, "onBaseActivitySetScreenTitle: ")
    }

    override fun onBaseActivityReplaceFragment(fragment: Fragment, bundle: Bundle?, tag: String) {
        Tracer.debug(TAG, "onBaseActivityReplaceFragment: ")
        onBaseActivityReplaceFragment(R.id.activity_gallery_container, fragment, bundle, tag)
    }

    override fun onBaseActivityReplaceFragment(containerId: Int, fragment: Fragment, bundle: Bundle?, tag: String) {
        Tracer.debug(TAG, "onBaseActivityReplaceFragment: ")
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, fragment, tag)
        if (bundle != null) {
            fragment!!.arguments = bundle
        }
        fragmentTransaction.commit()
    }

    override fun onBaseActivityAddFragment(fragment: Fragment, bundle: Bundle?, isAddToBackStack: Boolean, tag: String) {
        Tracer.debug(TAG, "onBaseActivityAddFragment: ")
        onBaseActivityAddFragment(R.id.activity_gallery_container, fragment, bundle, isAddToBackStack, tag)
    }

    override fun onBaseActivityAddFragment(containerId: Int, fragment: Fragment, bundle: Bundle?, isAddToBackStack: Boolean, tag: String) {
        Tracer.debug(TAG, "onBaseActivityAddFragment: ")
        val findFragmentByTag = supportFragmentManager.findFragmentByTag(tag)
        if (findFragmentByTag == null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(containerId, fragment, tag)
            if (isAddToBackStack) {
                fragmentTransaction.addToBackStack(tag)
            }
            if (bundle != null) {
                fragment.arguments = bundle
            }
            fragmentTransaction.commit()
        } else {
            while (true) {
                var fragment: Fragment = supportFragmentManager.findFragmentById(R.id.activity_gallery_container)
                    ?: break
                if ((fragment.tag ?: "").equals(tag)) {
                    findFragmentByTag.arguments = bundle
                    if (fragment is OnBaseFragmentListener) {
                        fragment.onPopFromBackStack()
                    }
                    break
                }
                supportFragmentManager.popBackStackImmediate()
            }
        }
    }

    override fun onBaseActivitySetToolbar(toolbarLayout: View) {
        Tracer.debug(TAG, "onBaseActivitySetToolbar: $toolbarLayout")
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.activity_gallery_toolbar)
        toolbar.visibility = View.VISIBLE
        toolbar.removeAllViews()
        toolbar.addView(toolbarLayout, Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT))
    }

    override fun onClick(v: View?) {
        when (v?.id ?: -1) {
            R.id.toolbar_imageView_imageView_ok -> {
                val model = Model.getInstance()
                val size = model.selectedImageDataList.size
                if (model.isMaxPicCountFixed && size != model.maxPicCount) {
                    Toast.makeText(this, "USER SHOULD SELECT ${model.maxPicCount} IMAGES. CURRENT SELECTION IS $size", Toast.LENGTH_LONG).show()
                    return
                }
                if (size == 0) {
                    Toast.makeText(this, "USER SHOULD SELECT AT-LEAST 1 IMAGE", Toast.LENGTH_LONG).show()
                    return
                }
                if (size > model.maxPicCount) {
                    Toast.makeText(this, "USER SHOULDN'T SELECT MORE THEN ${model.maxPicCount} IMAGE", Toast.LENGTH_LONG).show()
                    return
                }
                // SEND RESULT TO CALLER
                val data = JsonUtil.toStringTokenType<ArrayList<ImageData>>(model.selectedImageDataList, false)
                val intent = Intent()
                intent.putExtra(EXTRA_IMAGE_DATA, data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun loadGalleryInfoList() {
        Tracer.debug(TAG, "loadGalleryInfoList : ")
        MKRDialogUtil.showLoadingDialog(this, "LOAD MOBILE GALLERY")
        asyncTaskProvider.fetchGalleryInfoList(this, asyncCallBackFetchGalleryInfo)
    }

    /**
     * Method to show the Album Fragment
     */
    private fun showAlbumFragment() {
        Tracer.debug(TAG, "showAlbumFragment : ")
        val tag = FragmentProvider.TAG.GALLERY_ALBUM
        val fragment = FragmentProvider.getFragment(tag)
        val bundle = Bundle()
        bundle.putString(FragmentGalleryAlbum.EXTRA_ALBUM_LIST, JsonUtil.toStringTokenType<ArrayList<DTOAlbumData>>(dtoAlbumDataList, true))
        onBaseActivityAddFragment(fragment, bundle, true, tag)
    }
}
