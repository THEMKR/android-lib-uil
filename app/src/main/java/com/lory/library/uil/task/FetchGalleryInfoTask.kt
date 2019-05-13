package com.lory.library.uil.task

import android.content.Context
import android.provider.MediaStore
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.asynctask.BaseAsyncTask
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.dto.DTOAlbumData
import com.lory.library.uil.utils.Constants
import com.lory.library.uil.utils.Tracer


class FetchGalleryInfoTask : BaseAsyncTask<ArrayList<DTOAlbumData>, Any> {
    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".FetchGalleryInfoTask"
    }

    constructor(context: Context, asyncCallBack: AsyncCallBack<ArrayList<DTOAlbumData>, Any>?) : super(context, asyncCallBack) {

    }

    override fun doInBackground(): ArrayList<DTOAlbumData> {
        // EXTRACT EXTERNAL IMAGES
        val dtoAlbumList = ArrayList<DTOAlbumData>()
        try {
            val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC")
            val colPathIndex = cursor!!.getColumnIndex(MediaStore.Images.Media.DATA)
            val colAlbumNameIndex = cursor!!.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor!!.moveToNext()) {
                val path = cursor!!.getString(colPathIndex)
                val bucketName = cursor!!.getString(colAlbumNameIndex)
                Tracer.debug(TAG, "doInBackground : EXTERNAL : $path")
                var dtoAlbumData = getDTOAlbumData(dtoAlbumList, bucketName, Constants.STORAGE_TYPE.EXTERNAL.ordinal)
                if (dtoAlbumData == null) {
                    dtoAlbumData = DTOAlbumData(Constants.STORAGE_TYPE.EXTERNAL, bucketName)
                    dtoAlbumList.add(dtoAlbumData)
                }
                dtoAlbumData.imagePathList.add(path)
            }
            cursor!!.close()
        } catch (e: Exception) {
            Tracer.error(TAG, "doInBackground : EXTERNAL : ${e.message} ")
        }

        // EXTRACT INTERNAL IMAGES
        try {
            val cursor = context.contentResolver.query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC")
            if (cursor != null) {
                val colPathIndex = cursor!!.getColumnIndex(MediaStore.Images.Media.DATA)
                val colAlbumNameIndex = cursor!!.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                while (cursor!!.moveToNext()) {
                    val path = cursor!!.getString(colPathIndex)
                    val bucketName = cursor!!.getString(colAlbumNameIndex)
                    Tracer.debug(TAG, "doInBackground : INTERNAL : $path")
                    var dtoAlbumData = getDTOAlbumData(dtoAlbumList, bucketName, Constants.STORAGE_TYPE.INTERNAL.ordinal)
                    if (dtoAlbumData == null) {
                        dtoAlbumData = DTOAlbumData(Constants.STORAGE_TYPE.INTERNAL, bucketName)
                        dtoAlbumList.add(dtoAlbumData)
                    }
                    dtoAlbumData.imagePathList.add(path)
                }
                cursor!!.close()
            }
        } catch (e: Exception) {
            Tracer.error(TAG, "doInBackground : INTERNAL : ${e.message} ")
        }
        return dtoAlbumList
    }

    /**
     * Method to get the DTOAlbumData
     * @param dtoAlbumList
     * @param albumName
     * @param storageType
     */
    private fun getDTOAlbumData(dtoAlbumList: ArrayList<DTOAlbumData>, albumName: String, storageType: Int): DTOAlbumData? {
        for (dtoAlbum in dtoAlbumList) {
            if (dtoAlbum.albumName.equals(albumName, true) && (dtoAlbum.storageType == storageType)) {
                return dtoAlbum
            }
        }
        return null
    }
}