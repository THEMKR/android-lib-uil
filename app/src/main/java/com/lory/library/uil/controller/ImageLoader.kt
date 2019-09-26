package com.lory.library.uil.controller

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import com.lory.library.storage.session.OnSessionStorageListener
import com.lory.library.storage.session.SessionStorage
import com.lory.library.ui.asynctask.AsyncCallBack
import com.lory.library.ui.utils.Tracer
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.provider.UILTaskProvider
import com.lory.library.uil.utils.Constants
import java.util.*


class ImageLoader {

    companion object {
        private const val TAG: String = "ImageLoader"
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
    private val queryList = Vector<LoaderQuery>()
    private val uilTaskProvider = UILTaskProvider()
    private val onSessionStorageListener = object : OnSessionStorageListener<Bitmap> {
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
     * Method to Re-move image. If callback send in loadImage then it should be send in remove image
     * @param imageInfo
     * @param onImageLoaded
     */
    fun remove(imageInfo: ImageInfo?, onImageLoaded: OnImageLoaderListener) {
        if (imageInfo == null) {
            return
        }
        remove(LoaderQuery(imageInfo, onImageLoaded))
    }

    /**
     * Method to Re-move LoaderQuery
     * @param query
     */
    private fun remove(query: LoaderQuery?) {
        if (query != null) {
            queryList.remove(query)
        }
    }

    /**
     * Method to load image
     * @param imageInfo
     * @param onImageLoaded
     */
    fun loadImage(imageInfo: ImageInfo?, onImageLoaded: OnImageLoaderListener) {
        Tracer.debug(TAG, "loadImage : ")
        if (imageInfo == null) {
            return
        }
        val query = LoaderQuery(imageInfo, onImageLoaded)
        if (imageInfo.isCached) {
            val bitmap = sessionStorage.getValue<Bitmap>(imageInfo.key)
            if (bitmap != null && !bitmap.isRecycled) {
                saveAndSendBitmap(bitmap, query, true)
                return
            }
        }
        remove(query)
        queryList.add(query)
        runWorker()
    }

    /**
     * Method to run the worker
     */
    private fun runWorker() {
        Tracer.debug(TAG, "runWorker : ${(worker == null && queryList.size > 0)}")
        if (worker == null && queryList.size > 0) {
            worker = Worker()
            worker!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    /**
     * Class used to control the Number of Request to load bitmap
     */
    private inner class Worker : AsyncTask<Void, LoaderQuery, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            while (queryList.size > 0) {
                try {
                    Thread.sleep(10)
                } catch (e: Exception) {
                    Tracer.error(TAG, "doInBackground : ${e.message} ")
                }
                if (threadCount < Constants.MAX_THREAD_COUNT) {
                    threadCount++
                    try {
                        val query = queryList[0]
                        queryList.remove(query)
                        publishProgress(query)
                    } catch (e: Exception) {
                        Tracer.error(TAG, "doInBackground : ${e.message} ")
                        threadCount--
                    }
                }
            }
            return null
        }

        override fun onProgressUpdate(vararg values: LoaderQuery?) {
            val query: LoaderQuery = if (values.size > 0) {
                values[0]!!
            } else {
                threadCount--
                return
            }
            if (query.imageInfo.isCached) {
                val bitmap = sessionStorage.getValue<Bitmap>(query.imageInfo.key)
                if (bitmap != null && !bitmap.isRecycled) {
                    threadCount--
                    saveAndSendBitmap(bitmap, query, true)
                    return
                }
            }
            when (query.imageInfo.storageType) {
                Constants.STORAGE_TYPE.EXTERNAL.value -> {
                    uilTaskProvider.fetchBitmapFromSdCard(context, query.imageInfo, BitmapCallback(query))
                }
                Constants.STORAGE_TYPE.INTERNAL.value -> {
                    uilTaskProvider.fetchBitmapFromInternal(context, query.imageInfo, BitmapCallback(query))
                }
                Constants.STORAGE_TYPE.URL.value -> {
                    uilTaskProvider.fetchBitmapFromURL(context, query.imageInfo, BitmapCallback(query))
                }
                Constants.STORAGE_TYPE.ASSSETS.value -> {
                    uilTaskProvider.fetchBitmapFromAssets(context, query.imageInfo, BitmapCallback(query))
                }
                else -> {
                    uilTaskProvider.fetchBitmapFromSdCard(context, query.imageInfo, BitmapCallback(query))
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
        private val query: LoaderQuery

        /**
         * Constructor
         * @param query
         */
        constructor(query: LoaderQuery) {
            this.query = query
        }

        override fun onProgress(progress: Any?) {
            Tracer.debug(TAG, "onProgress : ")
        }

        override fun onSuccess(mkr: Bitmap?) {
            Tracer.debug(TAG, "BitmapCallback:onSuccess : ")
            threadCount--
            if (mkr != null && !mkr.isRecycled) {
                saveAndSendBitmap(mkr, query, false)
            } else {
                remove(query)
                try {
                    query.onImageLoaded.onImageLoaded(mkr, query.imageInfo)
                } catch (e: Exception) {
                    Log.e("MKT", "${TAG} : BitmapCallback : onSuccess : ${e.message} ")
                }
            }
        }
    }

    /**
     * Method to save and send the Bitmap to the related caller
     * @param bitmap Bitmap should be bitmap!=NULL && !bitmap.isRecycled
     * @param query LoaderQuery
     * @param isAlreadyCached TRUE if return from cache else FALSE
     */
    private fun saveAndSendBitmap(bitmap: Bitmap, query: LoaderQuery, isAlreadyCached: Boolean) {
        if (query.onImageLoaded != null) {
            object : AsyncTask<Void, Void, Bitmap?>() {
                override fun doInBackground(vararg params: Void?): Bitmap? {
                    return query.onImageLoaded.onImageAlter(bitmap, query.imageInfo)
                }

                override fun onPostExecute(result: Bitmap?) {
                    super.onPostExecute(result)
                    if (result != null && !result.isRecycled && query.imageInfo.isCached) {
                        sessionStorage.put(query.imageInfo.key, result, onSessionStorageListener)
                    }
                    remove(query)
                    try {
                        query.onImageLoaded.onImageLoaded(result, query.imageInfo)
                    } catch (e: Exception) {
                        Log.e("MKT", "${TAG} : saveAndSendBitmap : onSuccess : ${e.message} ")
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } else {
            if (!isAlreadyCached && query.imageInfo.isCached) {
                sessionStorage.put(query.imageInfo.key, bitmap, onSessionStorageListener)
            }
            remove(query)
            try {
                query.onImageLoaded.onImageLoaded(bitmap, query.imageInfo)
            } catch (e: Exception) {
                Log.e("MKT", "${TAG} : saveAndSendBitmap : onSuccess : ${e.message} ")
            }
        }
    }

    /**
     * Interface to notify that the image is loaded in the cache
     *
     * @author THE-MKR
     */
    interface OnImageLoaderListener {

        /**
         * Method called when image is successfully downloaded and saved in cache
         */
        fun onImageLoaded(bitmap: Bitmap?, imageInfo: ImageInfo)

        /**
         * Method used to alter the bitmap before saved it in the cache memory.
         * Caller should set the specific value for [ImageInfo.specifire] if user this operation
         * This method is called from the back thread whenever a bitmap is build successfully, else not call in case of bitmap failure
         */
        fun onImageAlter(bitmap: Bitmap?, imageInfo: ImageInfo): Bitmap?
    }

    /**
     * QUERY CLASS
     */
    class LoaderQuery {
        val imageInfo: ImageInfo
        val onImageLoaded: OnImageLoaderListener

        /**
         * Constructor
         * @param imageInfo
         * @param onImageLoaded
         */
        constructor(imageInfo: ImageInfo, onImageLoaded: OnImageLoaderListener) {
            this.imageInfo = imageInfo
            this.onImageLoaded = onImageLoaded as OnImageLoaderListener
        }

        override fun equals(other: Any?): Boolean {
            if (other != null && other is LoaderQuery) {
                return imageInfo.equals(other.imageInfo) && onImageLoaded.equals(other.onImageLoaded)
            }
            return false
        }
    }
}