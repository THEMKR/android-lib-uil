package com.lory.library.uil.task

import android.content.Context
import android.provider.MediaStore
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.asynctask.BaseAsyncTask
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.dto.DTOAlbumData
import com.lory.library.uil.dto.ImageData
import com.lory.library.uil.utils.Constants
import com.lory.library.uil.utils.Tracer
import java.util.*


class FetchGalleryInfoTask : BaseAsyncTask<ArrayList<DTOAlbumData>, Any> {
    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".FetchGalleryInfoTask"
    }

    constructor(context: Context, asyncCallBack: AsyncCallBack<ArrayList<DTOAlbumData>, Any>?) : super(context, asyncCallBack) {

    }

    override fun doInBackground(): ArrayList<DTOAlbumData> {
        val arrayList = ArrayList<DTOAlbumData>()
        val externalImageData = getExternalImageData()
        if (externalImageData != null) {
            arrayList.addAll(externalImageData)
        }
        val internalImageData = getInternalImageData()
        if (internalImageData != null) {
            arrayList.addAll(internalImageData)
        }
        return arrayList
    }

    /**
     * Method to get the list of All External Image
     */
    private fun getExternalImageData(): ArrayList<DTOAlbumData>? {
        Tracer.debug(TAG, "getExternalImageData : ")
        val mapAlbum = HashMap<String, DTOAlbumData>()
        try {
            val locale = Locale.getDefault()
            val cursor = context.contentResolver.query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC") ?: return null
            val colPathIndex = cursor!!.getColumnIndex(MediaStore.Images.Media.DATA)
            val colAlbumNameIndex = cursor!!.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val bucketName = cursor!!.getString(colAlbumNameIndex).toUpperCase(locale).trim()
                if (!mapAlbum.containsKey(bucketName)) {
                    mapAlbum[bucketName] = DTOAlbumData(Constants.STORAGE_TYPE.EXTERNAL.value, bucketName)
                }
                val path = cursor!!.getString(colPathIndex)
                val imageData = ImageData()
                imageData.path = path
                imageData.storageType = Constants.STORAGE_TYPE.EXTERNAL.value
                mapAlbum[bucketName]?.imagePathList?.add(imageData)
            }
            cursor.close()
        } catch (e: Exception) {
            Tracer.error(TAG, "doInBackground : EXTERNAL : ${e.message} ")
        }
        return ArrayList(mapAlbum.values)
    }

    /**
     * Method to get the list of All Internal Image
     */
    private fun getInternalImageData(): ArrayList<DTOAlbumData>? {
        Tracer.debug(TAG, "getInternalImageData : ")
        val mapAlbum = HashMap<String, DTOAlbumData>()
        try {
            val locale = Locale.getDefault()
            val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC") ?: return null
            val colPathIndex = cursor!!.getColumnIndex(MediaStore.Images.Media.DATA)
            val colAlbumNameIndex = cursor!!.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val bucketName = cursor!!.getString(colAlbumNameIndex).toUpperCase(locale).trim()
                if (!mapAlbum.containsKey(bucketName)) {
                    mapAlbum[bucketName] = DTOAlbumData(Constants.STORAGE_TYPE.INTERNAL.value, bucketName)
                }
                val path = cursor!!.getString(colPathIndex)
                val imageData = ImageData()
                imageData.path = path
                imageData.storageType = Constants.STORAGE_TYPE.INTERNAL.value
                mapAlbum[bucketName]?.imagePathList?.add(imageData)
            }
            cursor.close()
        } catch (e: Exception) {
            Tracer.error(TAG, "doInBackground : INTERNAL : ${e.message} ")
        }
        return ArrayList(mapAlbum.values)
    }
}