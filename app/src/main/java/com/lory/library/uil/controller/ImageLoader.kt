package com.lory.library.uil.controller

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.storage.session.OnSessionStorageListener
import com.lory.library.storage.session.SessionStorage
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.provider.UILTaskProvider
import com.lory.library.uil.utils.Constants
import com.lory.library.uil.utils.Tracer
import java.util.*


class ImageLoader {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".ImageLoader"
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
    private val queryList = Vector<Query>()
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
     * Method to Re-move image. If callback send in loadImage then it should be send in remove image
     * @param imageInfo
     * @param onImageLoaded
     * @param onImageAlterOperation If need to manipulation image before saved and return back
     */
    fun remove(imageInfo: ImageInfo?, onImageLoaded: OnImageLoaded?, onImageAlterOperation: OnImageAlterOperation?) {
        if (imageInfo == null) {
            return
        }
        remove(Query(imageInfo, onImageLoaded, onImageAlterOperation))
    }

    /**
     * Method to Re-move Query
     * @param query
     */
    private fun remove(query: Query?) {
        if (query != null) {
            queryList.remove(query)
        }
    }

    /**
     * Method to load image
     * @param imageInfo
     * @param onImageLoaded
     * @param onImageAlterOperation If need to manipulation image before saved and return back
     */
    fun loadImage(imageInfo: ImageInfo?, onImageLoaded: OnImageLoaded?, onImageAlterOperation: OnImageAlterOperation?) {
        Tracer.debug(TAG, "loadImage : ")
        if (imageInfo == null) {
            return
        }
        val query = Query(imageInfo, onImageLoaded, onImageAlterOperation)
        val bitmap = sessionStorage.getValue<Bitmap>(imageInfo.key)
        if (bitmap != null && !bitmap.isRecycled) {
            saveAndSendBitmap(bitmap, query, true)
            return
        }
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
    private inner class Worker : AsyncTask<Void, Query, Void>() {

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
                        queryList.removeAt(0)
                        publishProgress(query)
                    } catch (e: Exception) {
                        Tracer.error(TAG, "doInBackground : ${e.message} ")
                        threadCount--
                    }
                }
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Query?) {
            val query: Query = if ((values?.size ?: 0) > 0) {
                values[0]!!
            } else {
                threadCount--
                return
            }
            val bitmap = sessionStorage.getValue<Bitmap>(query.imageInfo.key)
            if (bitmap != null && !bitmap.isRecycled) {
                threadCount--
                saveAndSendBitmap(bitmap, query, true)
                return
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
        private val query: Query

        /**
         * Constructor
         * @param query
         */
        constructor(query: Query) {
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
                query?.onImageLoaded?.onImageLoaded(mkr, query.imageInfo)
            }
        }
    }

    /**
     * Method to save and send the Bitmap to the related caller
     * @param bitmap Bitmap should be bitmap!=NULL && !bitmap.isRecycled
     * @param query Query
     * @param isAlreadyCached TRUE if return from cache else FALSE
     */
    private fun saveAndSendBitmap(bitmap: Bitmap, query: Query, isAlreadyCached: Boolean) {
        if (query.onImageAlterOperation != null) {
            object : AsyncTask<Void, Void, Bitmap?>() {
                override fun doInBackground(vararg params: Void?): Bitmap? {
                    return query.onImageAlterOperation.onImageAlterOperation(bitmap, query.imageInfo)
                }

                override fun onPostExecute(result: Bitmap?) {
                    super.onPostExecute(result)
                    if (result != null && !result.isRecycled) {
                        sessionStorage.put(query.imageInfo.key, result, onSessionStorageListener)
                    }
                    remove(query)
                    query.onImageLoaded?.onImageLoaded(result, query.imageInfo)
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } else {
            if (!isAlreadyCached) {
                sessionStorage.put(query.imageInfo.key, bitmap, onSessionStorageListener)
            }
            remove(query)
            query.onImageLoaded?.onImageLoaded(bitmap, query.imageInfo)
        }
    }

    /**
     * Interface to notify that the image is loaded in the cache
     *
     * @author THE-MKR
     */
    interface OnImageLoaded {
        fun onImageLoaded(bitmap: Bitmap?, imageInfo: ImageInfo)
    }

    /**
     * Interface used to alter the bitmap before saved it in the cache memory.
     * Caller should set the specific value for [ImageInfo.specifire] if user this operation
     *
     * @author THE-MKR
     */
    interface OnImageAlterOperation {

        /**
         * This method is called from the back thread whenevre a bitmap is build successfully, else not call in case of bitmap failure
         */
        fun onImageAlterOperation(bitmap: Bitmap?, imageInfo: ImageInfo): Bitmap?
    }

    /**
     * QUERY CLASS
     */
    class Query {
        val imageInfo: ImageInfo
        val onImageLoaded: OnImageLoaded?
        val onImageAlterOperation: OnImageAlterOperation?

        constructor(imageInfo: ImageInfo, onImageLoaded: OnImageLoaded?, onImageAlterOperation: OnImageAlterOperation?) {
            this.imageInfo = imageInfo
            this.onImageLoaded = onImageLoaded
            this.onImageAlterOperation = onImageAlterOperation
        }

        override fun equals(other: Any?): Boolean {
            if (other != null && other is Query) {
                if (imageInfo.equals(other.imageInfo)) {
                    if (onImageLoaded != null && onImageAlterOperation != null) {
                        return onImageLoaded.equals(other.onImageLoaded) && onImageAlterOperation.equals(other.onImageAlterOperation)
                    } else if (onImageLoaded != null) {
                        return onImageLoaded.equals(other.onImageLoaded)
                    } else if (onImageAlterOperation != null) {
                        return onImageAlterOperation.equals(other.onImageAlterOperation)
                    } else {
                        return false
                    }
                }
                return false
            }
            return false
        }
    }
}