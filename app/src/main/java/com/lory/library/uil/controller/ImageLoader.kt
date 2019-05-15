package com.lory.library.uil.controller

import android.content.Context
import android.graphics.Bitmap
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.storage.session.OnSessionStorageListener
import com.lory.library.storage.session.SessionStorage
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.dto.ImageData
import com.lory.library.uil.provider.AsyncTaskProvider
import com.lory.library.uil.utils.Constants
import com.lory.library.uil.utils.Tracer
import java.util.*


class ImageLoader {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".ImageLoader"
        private val MAX_THREAD_COUNT: Int = 5
        private var mThreadCount: Int = 0
        private var instance: ImageLoader? = null

        /**
         * Method to get the Current Instance of the ImageLoader
         * @param context
         */
        fun getInstance(context: Context): ImageLoader {
            if (instance == null) {
                instance = ImageLoader(context.applicationContext)
            }
            return instance!!
        }

    }

    private var context: Context
    private val query: Vector<ImageData>
    private val listenerList = Hashtable<ImageData, OnImageLoaded>()
    val asyncTaskProvider: AsyncTaskProvider = AsyncTaskProvider()
    val onSessionStorageListener = object : OnSessionStorageListener<Bitmap> {
        override fun onItemRecycled(mkr: Bitmap): Int {
            mkr.recycle()
            return 0
        }

        override fun onItemSizeInByte(mkr: Bitmap): Int {
            return mkr.height * mkr.width * 4
        }

    }

    private constructor(context: Context) {
        this.context = context.applicationContext
        query = Vector()
        asyncTaskProvider.attachProvider()
    }

    /**
     * Method to load image
     * @param imageData
     */
    fun removeImage(imageData: ImageData?) {
        if (imageData == null) {
            return
        }
        query.remove(imageData)
        listenerList.remove(imageData)
    }

    /**
     * Method to load image
     * @param imageData
     * @param onImageLoaded
     */
    fun loadImage(imageData: ImageData?, onImageLoaded: OnImageLoaded?) {
        Tracer.debug(TAG, "loadImage : ")
        if (imageData == null) {
            return
        }
        query.add(imageData)
        if (onImageLoaded != null) {
            listenerList[imageData] = onImageLoaded
        }
        when (imageData.storageType) {
            Constants.STORAGE_TYPE.EXTERNAL.ordinal -> {
                asyncTaskProvider.fetchBitmapFromExternalStorage(context, imageData, object : AsyncCallBack<Bitmap, Any> {
                    override fun onProgress(progress: Any?) {

                    }

                    override fun onSuccess(mkr: Bitmap?) {
                        Tracer.debug(TAG, "onSuccess : $imageData")
                        mThreadCount--
                        if (mkr != null && !mkr.isRecycled) {
                            SessionStorage.getInstance(context).put(imageData.key, mkr, onSessionStorageListener)
                            listenerList[imageData]?.onImageLoaded(mkr, imageData)
                        } else {
                            SessionStorage.getInstance(context).removeValue(imageData.key)
                        }
                    }
                })
            }
            Constants.STORAGE_TYPE.INTERNAL.ordinal -> {
                asyncTaskProvider.fetchBitmapFromInternalStorage(context, imageData, object : AsyncCallBack<Bitmap, Any> {
                    override fun onProgress(progress: Any?) {

                    }

                    override fun onSuccess(mkr: Bitmap?) {
                        Tracer.debug(TAG, "onSuccess : $imageData")
                        mThreadCount--
                        if (mkr != null && !mkr.isRecycled) {
                            SessionStorage.getInstance(context).put(imageData.key, mkr, onSessionStorageListener)
                            listenerList[imageData]?.onImageLoaded(mkr, imageData)
                        } else {
                            SessionStorage.getInstance(context).removeValue(imageData.key)
                        }
                    }
                })
            }
        }
    }

    /**
     * Interface to notify that the image is loaded in the cache
     *
     * @author THE-MKR
     */
    interface OnImageLoaded {
        fun onImageLoaded(bitmap: Bitmap, imageData: ImageData)
    }
}