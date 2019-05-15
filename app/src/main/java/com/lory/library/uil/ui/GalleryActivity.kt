package com.lory.library.uil.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.ui.callback.OnBaseActivityListener
import com.lory.library.ui.callback.OnBaseFragmentListener
import com.lory.library.ui.controller.AppPermissionController
import com.lory.library.ui.utils.MKRDialogUtil
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.R
import com.lory.library.uil.dto.DTOAlbumData
import com.lory.library.uil.dto.Model
import com.lory.library.uil.provider.AsyncTaskProvider
import com.lory.library.uil.provider.FragmentProvider
import com.lory.library.uil.ui.fragment.FragmentGalleryAlbum
import com.lory.library.uil.utils.JsonUtil
import com.lory.library.uil.utils.Tracer

class GalleryActivity : AppCompatActivity(), OnBaseActivityListener, AppPermissionController.OnAppPermissionControllerListener {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".GalleryActivity"
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
        asyncTaskProvider.attachProvider()
        appPermissionController = AppPermissionController(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), this
        )
        Model.getInstance().selectedImageDataList.clear()
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
            returnResponse()
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
        val fragmentTransaction = supportFragmentManager.beginTransaction()
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
        Tracer.debug(TAG, "onBaseActivitySetToolbar: ");
    }

    private fun loadGalleryInfoList() {
        Tracer.debug(TAG, "loadGalleryInfoList : ")
        MKRDialogUtil.showLoadingDialog(this, "LOAD MOBILE GALLERY")
        AsyncTaskProvider().fetchGalleryInfoList(this, asyncCallBackFetchGalleryInfo)
    }

    /**
     * Method to send the result
     */
    private fun returnResponse() {
        Tracer.debug(TAG, "returnResponse : ")
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
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
