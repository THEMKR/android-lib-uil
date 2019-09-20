package com.lory.library.uil.task

import android.content.Context
import android.provider.MediaStore
import com.lory.library.ui.asynctask.AsyncCallBack
import com.lory.library.ui.asynctask.BaseAsyncTask
import com.lory.library.ui.utils.Tracer
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.dto.DTOAlbumData
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.utils.Constants
import java.util.*


open class FetchGalleryInfoTask : BaseAsyncTask<ArrayList<DTOAlbumData>, Any> {
    companion object {
        private const val TAG: String =  "FetchGalleryInfoTask"
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

        Collections.sort(arrayList, object : Comparator<DTOAlbumData> {
            override fun compare(o1: DTOAlbumData?, o2: DTOAlbumData?): Int {
                return (o1?.albumName ?: "").compareTo((o2?.albumName ?: ""))
            }
        })
        return arrayList
    }

    /**
     * Method to get the list of All External Image
     */
    protected fun getExternalImageData(): ArrayList<DTOAlbumData>? {
        Tracer.debug(TAG, "getExternalImageData : ")
        val mapAlbum = HashMap<String, DTOAlbumData>()
        try {
            val locale = Locale.getDefault()
            val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC") ?: return null
            val colPathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val colAlbumNameIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val colOrientation = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION)
            while (cursor.moveToNext()) {
                val path = cursor.getString(colPathIndex) ?: continue
                if (path.trim().isEmpty()) {
                    continue
                }
                val bucketName = cursor.getString(colAlbumNameIndex).toUpperCase(locale).trim()
                if (!mapAlbum.containsKey(bucketName)) {
                    mapAlbum[bucketName] = DTOAlbumData(Constants.STORAGE_TYPE.EXTERNAL.value, bucketName)
                }
                mapAlbum[bucketName]?.imageInfoList?.add(
                    ImageInfo.Builder()
                        .setStorageLocation(path.trim())
                        .setStorageType(Constants.STORAGE_TYPE.EXTERNAL.value)
                        .setOrientation(cursor.getInt(colOrientation))
                        .setIsCached(true)
                        .build()
                )
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
    protected fun getInternalImageData(): ArrayList<DTOAlbumData>? {
        Tracer.debug(TAG, "getInternalImageData : ")
        val mapAlbum = HashMap<String, DTOAlbumData>()
        try {
            val locale = Locale.getDefault()
            val cursor = context.contentResolver.query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC") ?: return null
            val colPathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val colAlbumNameIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val colOrientation = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION)
            while (cursor.moveToNext()) {
                val path = cursor.getString(colPathIndex) ?: continue
                if (path.trim().isEmpty()) {
                    continue
                }
                val bucketName = cursor.getString(colAlbumNameIndex).toUpperCase(locale).trim()
                if (!mapAlbum.containsKey(bucketName)) {
                    mapAlbum[bucketName] = DTOAlbumData(Constants.STORAGE_TYPE.INTERNAL.value, bucketName)
                }
                mapAlbum[bucketName]?.imageInfoList?.add(
                    ImageInfo.Builder()
                        .setStorageLocation(path.trim())
                        .setStorageType(Constants.STORAGE_TYPE.INTERNAL.value)
                        .setOrientation(cursor.getInt(colOrientation))
                        .setIsCached(true)
                        .build()
                )
            }
            cursor.close()
        } catch (e: Exception) {
            Tracer.error(TAG, "doInBackground : INTERNAL : ${e.message} ")
        }
        return ArrayList(mapAlbum.values)
    }
}