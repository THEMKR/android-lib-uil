package com.lory.library.uil.controller

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import com.lory.library.asynctask.AsyncCallBack
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

    private var watcher: Watcher? = null
    private var context: Context
    private val query: Vector<ImageData>
    private val listenerList = Hashtable<ImageData, OnImageLoaded>()

    private constructor(context: Context) {
        this.context = context.applicationContext
        query = Vector()
    }

    /**
     * Method to load image
     * @param imageData
     */
    fun removeImage(imageData: ImageData?) {
        Tracer.debug(TAG, "removeImage : $imageData")
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
        Tracer.debug(TAG, "loadImage : $imageData")
        if (imageData == null) {
            return
        }
        query.add(imageData)
        if (onImageLoaded != null) {
            listenerList[imageData] = onImageLoaded
        }
        initiateWatcher()
    }


    /**
     * Method to initiate the Request Queue Watcher
     */
    private fun initiateWatcher() {
        if (watcher == null || !watcher!!.isWatching()) {
            watcher = Watcher()
            watcher?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    /**
     * Watcher is used to check weather the request Queue is empty or not
     */
    inner class Watcher : AsyncTask<Void, ImageData, Void>() {
        private var mIsWatching: Boolean = true
        private val asyncTaskProvider: AsyncTaskProvider = AsyncTaskProvider()

        override fun doInBackground(vararg voids: Void): Void? {
            asyncTaskProvider.attachProvider()
            setWatching(true)
            while (query.size > 0 || mThreadCount > 0) {
                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                if (mThreadCount < MAX_THREAD_COUNT && query.size > 0) {
                    val networkRequest = query.get(0)
                    query.removeAt(0)
                    publishProgress(networkRequest)
                    mThreadCount++
                }
            }
            setWatching(false)
            asyncTaskProvider.detachProvider()
            return null
        }

        override fun onProgressUpdate(vararg values: ImageData) {
            super.onProgressUpdate(*values)
            if (values.isNotEmpty()) {
                val imageData = values[0]
                when (imageData.storageType) {
                    Constants.STORAGE_TYPE.EXTERNAL.ordinal -> {
                        asyncTaskProvider.fetchBitmapFromExternalStorage(context, imageData, object : AsyncCallBack<Bitmap, Any> {
                            override fun onProgress(progress: Any?) {

                            }

                            override fun onSuccess(mkr: Bitmap?) {
                                Tracer.debug(TAG, "onSuccess : $imageData")
                                mThreadCount--
                                if (mkr != null && !mkr.isRecycled) {
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
                                    listenerList[imageData]?.onImageLoaded(mkr, imageData)
                                } else {
                                    SessionStorage.getInstance(context).removeValue(imageData.key)
                                }
                            }
                        })
                    }
                }
            } else {
                mThreadCount--
            }
        }

        protected override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            setWatching(false)
            initiateWatcher()
        }

        /**
         * Method to check weather the watcher is watching or not
         *
         * @return
         */
        @Synchronized
        fun isWatching(): Boolean {
            return mIsWatching
        }

        /**
         * Method to set the Watching state
         *
         * @param isWatching
         */
        @Synchronized
        private fun setWatching(isWatching: Boolean) {
            mIsWatching = isWatching
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