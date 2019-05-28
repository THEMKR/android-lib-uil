package com.lory.library.uil.provider

import android.content.Context
import android.graphics.Bitmap
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.asynctask.BaseAsyncTaskProvider
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.dto.DTOAlbumData
import com.lory.library.uil.dto.ImageData
import com.lory.library.uil.task.FetchBitmapFromExternalStorage
import com.lory.library.uil.task.FetchBitmapFromInternalStorage
import com.lory.library.uil.task.FetchBitmapTask
import com.lory.library.uil.task.FetchGalleryInfoTask
import com.lory.library.uil.utils.Tracer
import com.lory.library.uil.utils.UilUtils

open class AsyncTaskProvider : BaseAsyncTaskProvider() {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".AsyncTaskProvider"
    }

    enum class TASK {
        EXTERNAL_BITMAP, INTERNAL_BITMAP
    }

    /**
     * Method to fetch the List of all Image in Gallery
     * @param context
     * @param asyncCallBack
     */
    fun fetchGalleryInfoList(context: Context, asyncCallBack: AsyncCallBack<ArrayList<DTOAlbumData>, Any>) {
        Tracer.debug(TAG, "fetchGalleryInfoList : ")
        val task = FetchGalleryInfoTask(context, object : AsyncCallBack<ArrayList<DTOAlbumData>, Any> {
            override fun onProgress(progress: Any?) {
                notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, progress ?: Any())
            }

            override fun onSuccess(mkr: ArrayList<DTOAlbumData>?) {
                if (mkr != null) {
                    notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, mkr)
                } else {
                    notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, ArrayList<DTOAlbumData>())
                }
            }
        })
        task.executeTask()
    }

    /**
     * Method to fetch the Bitmap From External Storage
     * @param context
     * @param imageData
     * @param asyncCallBack
     */
    fun fetchBitmapFromExternalStorage(context: Context, imageData: ImageData, asyncCallBack: AsyncCallBack<Bitmap, Any>) {
        Tracer.debug(TAG, "fetchBitmapFromExternalStorage : $imageData")
        val task = getTask(context, imageData, object : AsyncCallBack<Bitmap, Any> {
            override fun onProgress(progress: Any?) {
                notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, progress ?: Any())
            }

            override fun onSuccess(mkr: Bitmap?) {
                if (mkr != null) {
                    notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, mkr)
                } else {
                    notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, UilUtils.getDefaultBitmap(context))
                }
            }
        }, TASK.EXTERNAL_BITMAP)
        task.executeTask()
    }

    /**
     * Method to fetch the Bitmap From External Storage
     * @param context
     * @param imageData
     * @param asyncCallBack
     */
    fun fetchBitmapFromInternalStorage(context: Context, imageData: ImageData, asyncCallBack: AsyncCallBack<Bitmap, Any>) {
        Tracer.debug(TAG, "fetchBitmapFromInternalStorage : $imageData")
        val task = getTask(context, imageData, object : AsyncCallBack<Bitmap, Any> {
            override fun onProgress(progress: Any?) {
                notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, progress ?: Any())
            }

            override fun onSuccess(mkr: Bitmap?) {
                if (mkr != null) {
                    notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, mkr)
                } else {
                    notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, UilUtils.getDefaultBitmap(context))
                }
            }
        }, TASK.INTERNAL_BITMAP)
        task.executeTask()
    }

    /**
     * Method to get the Tasker correspond to the Constant
     * @param context
     * @param imageData
     * @param asyncCallBack
     * @param task
     */
    open protected fun getTask(context: Context, imageData: ImageData, asyncCallBack: AsyncCallBack<Bitmap, Any>, task: TASK): FetchBitmapTask {
        return when (task) {
            TASK.EXTERNAL_BITMAP -> {
                FetchBitmapFromExternalStorage(context, imageData, asyncCallBack)
            }
            TASK.INTERNAL_BITMAP -> {
                FetchBitmapFromInternalStorage(context, imageData, asyncCallBack)
            }
            else -> {
                FetchBitmapFromExternalStorage(context, imageData, asyncCallBack)
            }
        }
    }
}