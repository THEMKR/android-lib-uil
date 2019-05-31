package com.lory.library.uil.controller

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.storage.session.OnSessionStorageListener
import com.lory.library.storage.session.SessionStorage
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.dto.ImageInfo
import com.lory.library.uil.provider.UILTaskProvider
import com.lory.library.uil.utils.Constants
import com.lory.library.uil.utils.Tracer
import java.util.*


class ImageLoader {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".ImageLoader"
        private val MAX_THREAD_COUNT: Int = 5
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

    private val sessionStorage: SessionStorage
    private var worker: Worker? = null
    private var threadCount = 0
    private var context: Context
    private val query = Vector<ImageInfo>()
    private val listenerList = Hashtable<ImageInfo, OnImageLoaded>()
    val uilTaskProvider = UILTaskProvider()
    val onSessionStorageListener = object : OnSessionStorageListener<Bitmap> {
        override fun onItemRecycled(mkr: Bitmap): Int {
            mkr.recycle()
            return 0
        }

        override fun onItemSizeInByte(mkr: Bitmap): Int {
            return mkr.height * mkr.width * 4
        }
    }

    /**
     *
     */
    private constructor(context: Context) {
        this.context = context.applicationContext
        uilTaskProvider.attachProvider()
        sessionStorage = SessionStorage.getInstance(context)
    }

    /**
     * Method to load image
     * @param imageInfo
     */
    fun removeImage(imageInfo: ImageInfo?) {
        if (imageInfo == null) {
            return
        }
        query.remove(imageInfo)
        listenerList.remove(imageInfo)
    }

    /**
     * Method to load image
     * @param imageInfo
     * @param onImageLoaded
     */
    fun loadImage(imageInfo: ImageInfo?, onImageLoaded: OnImageLoaded?) {
        Tracer.debug(TAG, "loadImage : ")
        if (imageInfo == null) {
            return
        }
        val bitmap = sessionStorage.getValue<Bitmap>(imageInfo.key)
        if (bitmap != null && !bitmap.isRecycled) {
            onImageLoaded?.onImageLoaded(bitmap, imageInfo)
            return
        }
        removeImage(imageInfo)
        query.add(imageInfo)
        if (onImageLoaded != null) {
            listenerList[imageInfo] = onImageLoaded
        }
        runWorker()
    }

    /**
     * Method to run the worker
     */
    private fun runWorker() {
        Tracer.debug(TAG, "runWorker : ${(worker == null && query.size > 0)}")
        if (worker == null && query.size > 0) {
            worker = Worker()
            worker!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    /**
     * Class used to control the Number of Request to load bitmap
     */
    private inner class Worker : AsyncTask<Void, ImageInfo, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            while (query.size > 0) {
                try {
                    Thread.sleep(10)
                } catch (e: Exception) {
                    Tracer.error(TAG, "doInBackground : ${e.message} ")
                }
                if (threadCount < MAX_THREAD_COUNT) {
                    threadCount++
                    try {
                        val imageData = query[0]
                        query.removeAt(0)
                        publishProgress(imageData)
                    } catch (e: Exception) {
                        Tracer.error(TAG, "doInBackground : ${e.message} ")
                        threadCount--
                    }
                }
            }
            return null
        }

        override fun onProgressUpdate(vararg values: ImageInfo?) {
            val imageInfo: ImageInfo = if ((values?.size ?: 0) > 0) {
                values[0]!!
            } else {
                threadCount--
                return
            }
            when (imageInfo.storageType) {
                Constants.STORAGE_TYPE.EXTERNAL.value -> {
                    uilTaskProvider.fetchBitmapFromSdCard(context, imageInfo, BitmapCallback(imageInfo))
                }
                Constants.STORAGE_TYPE.INTERNAL.value -> {
                    uilTaskProvider.fetchBitmapFromInternal(context, imageInfo, BitmapCallback(imageInfo))
                }
                Constants.STORAGE_TYPE.URL.value -> {
                    uilTaskProvider.fetchBitmapFromURL(context, imageInfo, BitmapCallback(imageInfo))
                }
                Constants.STORAGE_TYPE.ASSSETS.value -> {
                    uilTaskProvider.fetchBitmapFromAssets(context, imageInfo, BitmapCallback(imageInfo))
                }
                else -> {
                    uilTaskProvider.fetchBitmapFromSdCard(context, imageInfo, BitmapCallback(imageInfo))
                }
            }
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            worker = null
            runWorker()
        }
    }

    /**
     * Class to handle the callback
     */
    private inner class BitmapCallback : AsyncCallBack<Bitmap?, Any> {
        private val imageData: ImageInfo

        /**
         * Constructor
         * @param imageData
         */
        constructor(imageData: ImageInfo) {
            this.imageData = imageData
        }

        override fun onProgress(progress: Any?) {
            Tracer.debug(TAG, "onProgress : ")
        }

        override fun onSuccess(mkr: Bitmap?) {
            Tracer.debug(TAG, "BitmapCallback:onSuccess : ")
            threadCount--
            listenerList[imageData]?.onImageLoaded(mkr, imageData)
            if (mkr != null && !mkr.isRecycled) {
                sessionStorage.put(imageData.key, mkr, onSessionStorageListener)
            }
        }
    }

    /**
     * Interface to notify that the image is loaded in the cache
     *
     * @author THE-MKR
     */
    interface OnImageLoaded {
        fun onImageLoaded(bitmap: Bitmap?, imageData: ImageInfo)
    }
}