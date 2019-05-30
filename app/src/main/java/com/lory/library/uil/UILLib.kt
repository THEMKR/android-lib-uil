package com.lory.library.uil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.ImageView
import com.lory.library.uil.controller.ImageLoader
import com.lory.library.uil.dto.ImageData
import com.lory.library.uil.provider.UILTaskProvider
import com.lory.library.uil.ui.GalleryActivity
import com.lory.library.uil.utils.Constants
import com.lory.library.uil.utils.JsonUtil
import com.lory.library.uil.utils.Tracer

class UILLib {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".UILLib"
        private var instance: UILLib? = null

        /**
         * Method to get the Instance
         */
        fun getInstance(context: Context): UILLib {
            if (instance == null) {
                instance = UILLib(context.applicationContext)
            }
            return instance!!
        }

        /**
         * Method to launchGallery the Gallery Activity for result
         * @param activity
         * @param requestCode Code to be return in onActivityRresult
         * @param maxImageCount Number of Image to be requested
         * @param isCountFixed  If TRUE then forced user to pick this much of Image. If FALSE then user my choose less image too
         */
        fun launchGallery(activity: Activity, requestCode: Int, maxImageCount: Int, isCountFixed: Boolean) {
            Tracer.debug(TAG, "launchGallery : Request Code = $requestCode : Image Count = $maxImageCount")
            val intent = Intent(activity, GalleryActivity::class.java)
            intent.putExtra(GalleryActivity.EXTRA_IMAGE_COUNT, maxImageCount)
            intent.putExtra(GalleryActivity.EXTRA_IS_COUNT_FIXED, isCountFixed)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            activity.startActivityForResult(intent, requestCode)
        }

        /**
         * Method to parse the response Intent
         * @param data Intent received in the onActivityResult of caller gallery
         */
        fun parseGalleryResponse(data: Intent?): ArrayList<ImageData> {
            Tracer.debug(TAG, "parseGalleryResponse : ")
            val data = data?.getStringExtra(GalleryActivity.EXTRA_IMAGE_DATA) ?: "[]"
            val dtoImageLocationList = JsonUtil.toObjectTokenType<ArrayList<ImageData>>(data, false)
            return dtoImageLocationList
        }
    }

    private val context: Context
    private val uilTaskProvider = UILTaskProvider()
    private val imageLoader: ImageLoader

    /**
     * Constructor
     * @param context
     */
    constructor(context: Context) {
        this.context = context
        this.imageLoader = ImageLoader.getInstance(context)
    }

    /**
     * Method to attach the Lib to return the Resonse in callback
     */
    fun attach() {
        uilTaskProvider.attachProvider()
    }

    /**
     * Method to detach the Lib to block the Resonse in callback
     */
    fun detach() {
        uilTaskProvider.detachProvider()
    }

    /**
     * Method to load Image from the URL
     * @param imageData Data of the image
     * @param callback To Received the Bitmap Info [imageData.path = url]
     */
    fun loadImage(imageData: ImageData, callback: ImageLoader.OnImageLoaded) {
        imageLoader.loadImage(imageData, callback)
    }
}