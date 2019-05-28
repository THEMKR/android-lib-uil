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
import com.lory.library.uil.task.FetchGalleryInfoTask
import com.lory.library.uil.utils.Tracer
import com.lory.library.uil.utils.UilUtils

open class AsyncTaskProvider : BaseAsyncTaskProvider() {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".AsyncTaskProvider"
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
        val task = FetchBitmapFromExternalStorage(context, imageData, object : AsyncCallBack<Bitmap, Any> {
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
        })
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
        val task = FetchBitmapFromInternalStorage(context, imageData, object : AsyncCallBack<Bitmap, Any> {
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
        })
        task.executeTask()
    }
}