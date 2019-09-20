package com.lory.library.uil.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.lory.library.ui.asynctask.AsyncCallBack
import com.lory.library.ui.callback.OnBaseActivityListener
import com.lory.library.ui.controller.AppPermissionController
import com.lory.library.ui.ui.activity.MKRAppcompatActivity
import com.lory.library.ui.utils.MKRDialogUtil
import com.lory.library.ui.utils.Tracer
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.R
import com.lory.library.uil.dto.DTOAlbumData
import com.lory.library.uil.dto.Model
import com.lory.library.uil.provider.FragmentProvider
import com.lory.library.uil.provider.UILTaskProvider
import com.lory.library.uil.ui.fragment.FragmentGalleryAlbum
import com.lory.library.uil.utils.JsonUtil

class GalleryActivity : MKRAppcompatActivity(), OnBaseActivityListener, AppPermissionController.OnAppPermissionControllerListener, View.OnClickListener {

    companion object {
        private const val TAG: String = "GalleryActivity"
        const val EXTRA_IMAGE_COUNT = "EXTRA_IMAGE_COUNT"
        const val EXTRA_IS_COUNT_FIXED = "EXTRA_IS_COUNT_FIXED"
        const val EXTRA_IMAGE_DATA = "EXTRA_IMAGE_DATA"
    }

    private var dtoAlbumDataList: ArrayList<DTOAlbumData> = ArrayList()
    private var uilTaskProvider = UILTaskProvider()
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
        setSupportActionBar(findViewById<Toolbar>(R.id.activity_gallery_toolbar))
        onBaseActivitySetToolbar(layoutInflater.inflate(R.layout.toolbar, null))
        uilTaskProvider.attachProvider()
        findViewById<View>(R.id.toolbar_imageView_imageView_ok).setOnClickListener(this)
        val model = Model.getInstance()
        model.reset()
        model.maxPicCount = intent?.getIntExtra(EXTRA_IMAGE_COUNT, 1) ?: 1
        model.isMaxPicCountFixed = intent?.getBooleanExtra(EXTRA_IS_COUNT_FIXED, false) ?: false
    }

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_gallery
    }

    override fun getDefaultFragmentContainerId(): Int {
        return R.id.activity_gallery_container
    }

    override fun getRequiredPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
    }

    override fun init(intent: Intent?) {
        MKRDialogUtil.showLoadingDialog(this, "LOAD MOBILE GALLERY. Plz wait....")
        uilTaskProvider.fetchGalleryInfoList(this, asyncCallBackFetchGalleryInfo)
    }

    override fun finishActivity() {
        finish()
    }

    override fun onDestroy() {
        uilTaskProvider.detachProvider()
        super.onDestroy()
    }

    override fun onBaseActivitySetScreenTitle(title: String) {
        Tracer.debug(TAG, "onBaseActivitySetScreenTitle: ")
        findViewById<TextView>(R.id.activity_gallery_textiew_message).text = "USER SELECT $title IMAGES"
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
                val size = model.selectedImageInfoList.size

                if (model.isMaxPicCountFixed && size != model.maxPicCount) {
                    Toast.makeText(this, "USER SHOULD SELECT ${model.maxPicCount} IMAGES", Toast.LENGTH_LONG).show()
                    return
                }
                if (size == 0) {
                    Toast.makeText(this, "USER SHOULD SELECT AT-LEAST 1 IMAGE", Toast.LENGTH_LONG).show()
                    return
                }
                if (size > model.maxPicCount) {
                    Toast.makeText(this, "USER SHOULD SELECT AT-MOST ${model.maxPicCount} IMAGES", Toast.LENGTH_LONG).show()
                    return
                }
                // SEND RESULT TO CALLER
                val newDtoList: ArrayList<ImageInfo> = ArrayList<ImageInfo>()
                val selectedDtoList = model.selectedImageInfoList
                for (imageInfo in selectedDtoList) {
                    newDtoList.add(ImageInfo.resizeImage(imageInfo, -1F))
                }
                val data = JsonUtil.toStringTokenType<ArrayList<ImageInfo>>(newDtoList, false)
                val intent = Intent()
                intent.putExtra(EXTRA_IMAGE_DATA, data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    /**
     * Method to show the Album Fragment
     */
    private fun showAlbumFragment() {
        Tracer.debug(TAG, "showAlbumFragment : ")
        val model = Model.getInstance()
        onBaseActivitySetScreenTitle(" : ${model.selectedImageInfoList.size}/${model.maxPicCount}")
        val tag = FragmentProvider.TAG.GALLERY_ALBUM
        val fragment = FragmentProvider.getFragment(tag)
        val bundle = Bundle()
        bundle.putString(FragmentGalleryAlbum.EXTRA_ALBUM_LIST, JsonUtil.toStringTokenType<ArrayList<DTOAlbumData>>(dtoAlbumDataList, true))
        onBaseActivityAddFragment(fragment, bundle, true, tag)
    }
}
