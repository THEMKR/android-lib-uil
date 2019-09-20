package com.lory.library.uil.provider

import android.content.Context
import android.graphics.Bitmap
import com.lory.library.ui.asynctask.AsyncCallBack
import com.lory.library.ui.asynctask.BaseAsyncTaskProvider
import com.lory.library.ui.utils.Tracer
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.dto.DTOAlbumData
import com.lory.library.uil.task.*

open class UILTaskProvider : BaseAsyncTaskProvider() {

    companion object {
        private const val TAG: String = "UILTaskProvider"
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
                notifyTaskProgress(asyncCallBack as AsyncCallBack<Any, Any>, progress ?: Any())
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
     * Method to fetch the Bitmap
     * @param context
     * @param imageInfo
     * @param asyncCallBack
     */
    fun fetchBitmapFromSdCard(context: Context, imageInfo: ImageInfo, asyncCallBack: AsyncCallBack<Bitmap?, Any>) {
        Tracer.debug(TAG, "fetchBitmapFromSdCard : $imageInfo")
        val task = FetchBitmapFromSdCard(context, imageInfo, object : AsyncCallBack<Bitmap?, Any> {
            override fun onProgress(progress: Any?) {
                notifyTaskProgress(asyncCallBack as AsyncCallBack<Any, Any>, progress ?: Any())
            }

            override fun onSuccess(mkr: Bitmap?) {
                notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, mkr)
            }
        })
        task.executeTask()
    }

    /**
     * Method to fetch the Bitmap
     * @param context
     * @param imageInfo
     * @param asyncCallBack
     */
    fun fetchBitmapFromInternal(context: Context, imageInfo: ImageInfo, asyncCallBack: AsyncCallBack<Bitmap?, Any>) {
        Tracer.debug(TAG, "fetchBitmapFromInternal : $imageInfo")
        val task = FetchBitmapFromInternalStorage(context, imageInfo, object : AsyncCallBack<Bitmap?, Any> {
            override fun onProgress(progress: Any?) {
                notifyTaskProgress(asyncCallBack as AsyncCallBack<Any, Any>, progress ?: Any())
            }

            override fun onSuccess(mkr: Bitmap?) {
                notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, mkr)
            }
        })
        task.executeTask()
    }

    /**
     * Method to fetch the Bitmap
     * @param context
     * @param imageInfo
     * @param asyncCallBack
     */
    fun fetchBitmapFromURL(context: Context, imageInfo: ImageInfo, asyncCallBack: AsyncCallBack<Bitmap?, Any>) {
        Tracer.debug(TAG, "fetchBitmapFromURL : $imageInfo")
        val task = FetchBitmapFromURL(context, imageInfo, object : AsyncCallBack<Bitmap?, Any> {
            override fun onProgress(progress: Any?) {
                notifyTaskProgress(asyncCallBack as AsyncCallBack<Any, Any>, progress ?: Any())
            }

            override fun onSuccess(mkr: Bitmap?) {
                notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, mkr)
            }
        })
        task.executeTask()
    }

    /**
     * Method to fetch the Bitmap
     * @param context
     * @param imageInfo
     * @param asyncCallBack
     */
    fun fetchBitmapFromAssets(context: Context, imageInfo: ImageInfo, asyncCallBack: AsyncCallBack<Bitmap?, Any>) {
        Tracer.debug(TAG, "fetchBitmapFromAssets : $imageInfo")
        val task = FetchBitmapFromAssets(context, imageInfo, object : AsyncCallBack<Bitmap?, Any> {
            override fun onProgress(progress: Any?) {
                notifyTaskProgress(asyncCallBack as AsyncCallBack<Any, Any>, progress ?: Any())
            }

            override fun onSuccess(mkr: Bitmap?) {
                notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, mkr)
            }
        })
        task.executeTask()
    }
}