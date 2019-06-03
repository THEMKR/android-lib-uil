package com.lory.library.uil.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.uil.ImageInfo


open class FetchBitmapFromAssets : FetchBitmapTask {

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
            var inputStream = context.assets.open(imageInfo.path)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()
            inputStream = context.assets.open(imageInfo.path)
            options.inSampleSize = getSampleSize(options.outWidth, options.outHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: Exception) {
            Log.e("UIL", "getBitmapFromPath : ASSETS : ${e.message} ")
            return null
        }
    }
}