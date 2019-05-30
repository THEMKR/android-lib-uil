package com.lory.library.uil.provider

import android.content.Context
import android.graphics.Bitmap
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.asynctask.BaseAsyncTaskProvider
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.dto.DTOAlbumData
import com.lory.library.uil.dto.ImageData
import com.lory.library.uil.task.*
import com.lory.library.uil.utils.Tracer

open class UILTaskProvider : BaseAsyncTaskProvider() {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".UILTaskProvider"
    }

    /**
     * BITMAP_LOCATION hold the location from where the bitmap is fetched
     */
    enum class BITMAP_LOCATION {
        EXTERNAL, INTERNAL, ASSETS, URL
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
     * Method to fetch the Bitmap
     * @param context
     * @param imageData
     * @param asyncCallBack
     * @param bitmapLocation
     * @param additionalPayoad
     */
    fun <MKR> fetchBitmap(context: Context, imageData: ImageData, asyncCallBack: AsyncCallBack<Bitmap?, Any>, bitmapLocation: BITMAP_LOCATION, additionalPayoad: MKR) {
        Tracer.debug(TAG, "fetchBitmap : $imageData : ${bitmapLocation.name}")
        val task = getTask(context, imageData, object : AsyncCallBack<Bitmap?, Any> {
            override fun onProgress(progress: Any?) {
                notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, progress ?: Any())
            }

            override fun onSuccess(mkr: Bitmap?) {
                notifyTaskResponse(asyncCallBack as AsyncCallBack<Any, Any>, mkr)
            }
        }, bitmapLocation, additionalPayoad)
        task.executeTask()
    }

    /**
     * Method to get the Tasker correspond to the Constant
     * @param context
     * @param imageData
     * @param asyncCallBack
     * @param task
     * @param additionalPayoad
     * [<UL><LI>BITMAP_LOCATION.EXTERNAL : FetchBitmapFromExternalStorage()</LI><LI>BITMAP_LOCATION.INTERNAL : FetchBitmapFromInternalStorage()</LI><LI>BITMAP_LOCATION.ASSETS : FetchBitmapFromAssets()</LI><LI>BITMAP_LOCATION.URL : FetchBitmapFromURL()</LI></UL>]
     */
    open protected fun <MKR> getTask(context: Context, imageData: ImageData, asyncCallBack: AsyncCallBack<Bitmap?, Any>, task: BITMAP_LOCATION, additionalPayoad: MKR): FetchBitmapTask<MKR> {
        return when (task) {
            BITMAP_LOCATION.EXTERNAL -> {
                FetchBitmapFromExternalStorage(context, imageData, asyncCallBack, additionalPayoad)
            }
            BITMAP_LOCATION.INTERNAL -> {
                FetchBitmapFromInternalStorage(context, imageData, asyncCallBack, additionalPayoad)
            }
            BITMAP_LOCATION.ASSETS -> {
                FetchBitmapFromAssets(context, imageData, asyncCallBack, additionalPayoad)
            }
            BITMAP_LOCATION.URL -> {
                FetchBitmapFromURL(context, imageData, asyncCallBack, additionalPayoad)
            }
            else -> {
                FetchBitmapFromExternalStorage(context, imageData, asyncCallBack, additionalPayoad)
            }
        }
    }
}