package com.lory.library.uil.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.lory.library.ui.asynctask.AsyncCallBack
import com.lory.library.uil.ImageInfo


open class FetchBitmapFromSdCard : FetchBitmapTask {

    /**
     * Constructor
     * @param context
     * @param imageInfo
     * @param asyncCallBack
     * @param additionalPayLoad
     */
    constructor(context: Context, imageInfo: ImageInfo, asyncCallBack: AsyncCallBack<Bitmap?, Any>?) : super(context, imageInfo, asyncCallBack) {

    }

    override fun getBitmapFromPath(): Bitmap? {
        try {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            options.inJustDecodeBounds = true
            options.inSampleSize = 1
            BitmapFactory.decodeFile(imageInfo.path, options)
            options.inSampleSize = getSampleSize(options.outWidth, options.outHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(imageInfo.path, options)
        } catch (e: Exception) {
            return null
        }
    }
}