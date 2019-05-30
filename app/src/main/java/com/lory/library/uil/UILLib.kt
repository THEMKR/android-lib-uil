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
     * @param imageView
     * @param url Image URL
     * @param desiredDimension [<OL><LI>-1 : ORIGINAL SIZE</LI><LI>1.0 : 100% of device width in pixel</LI></OL>]
     * @param loaderPlaceHolder ID of Loader Placer
     * @param errorPlaceHolder ID of Error Placer
     */
    fun loadImageFromUrl(imageView: ImageView, url: String, desiredDimension: Float, loaderPlaceHolder: Int, errorPlaceHolder: Int) {
        imageView.setImageResource(loaderPlaceHolder)
        val requestImageData = ImageData()
        requestImageData.path = url
        requestImageData.storageType = Constants.STORAGE_TYPE.URL.value
        requestImageData.dimensionPer = desiredDimension
        imageLoader.loadImage(requestImageData, object : ImageLoader.OnImageLoaded {
            override fun onImageLoaded(bitmap: Bitmap?, imageData: ImageData) {
                if (bitmap != null && !bitmap.isRecycled && requestImageData.equals(imageData)) {
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.setImageResource(errorPlaceHolder)
                }
            }
        })
    }

    /**
     * Method to load Image from the URL
     * @param url  Image Sd-Card Path
     * @param desiredDimansion [<OL><LI>-1 : ORIGINAL SIZE</LI><LI>1.0 : 100% of device in pixel</LI></OL>]
     * @param callback To Received the Bitmap load from server [imageData.path = url]
     */
    fun loadImageFromUrl(url: String, desiredDimansion: Float, callback: ImageLoader.OnImageLoaded) {
        val imageData = ImageData()
        imageData.path = url
        imageData.storageType = Constants.STORAGE_TYPE.URL.value
        imageData.dimensionPer = desiredDimansion
        imageLoader.loadImage(imageData, callback)
    }

    /**
     * Method to load Image from the URL
     * @param imageView
     * @param path Image Path
     * @param desiredDimension [<OL><LI>-1 : ORIGINAL SIZE</LI><LI>1.0 : 100% of device width in pixel</LI></OL>]
     * @param loaderPlaceHolder ID of Loader Placer
     * @param errorPlaceHolder ID of Error Placer
     */
    fun loadImageFromSdCard(imageView: ImageView, path: String, desiredDimension: Float, loaderPlaceHolder: Int, errorPlaceHolder: Int) {
        imageView.setImageResource(loaderPlaceHolder)
        val requestImageData = ImageData()
        requestImageData.path = path
        requestImageData.storageType = Constants.STORAGE_TYPE.EXTERNAL.value
        requestImageData.dimensionPer = desiredDimension
        imageLoader.loadImage(requestImageData, object : ImageLoader.OnImageLoaded {
            override fun onImageLoaded(bitmap: Bitmap?, imageData: ImageData) {
                if (bitmap != null && !bitmap.isRecycled && requestImageData.equals(imageData)) {
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.setImageResource(errorPlaceHolder)
                }
            }
        })
    }

    /**
     * Method to load Image from the URL
     * @param path Image Sd-Card Path
     * @param desiredDimansion [<OL><LI>-1 : ORIGINAL SIZE</LI><LI>1.0 : 100% of device in pixel</LI></OL>]
     * @param callback To Received the Bitmap load from server [imageData.path = path]
     */
    fun loadImageFromSdCard(path: String, desiredDimansion: Float, callback: ImageLoader.OnImageLoaded) {
        val imageData = ImageData()
        imageData.path = path
        imageData.storageType = Constants.STORAGE_TYPE.EXTERNAL.value
        imageData.dimensionPer = desiredDimansion
        imageLoader.loadImage(imageData, callback)
    }
}