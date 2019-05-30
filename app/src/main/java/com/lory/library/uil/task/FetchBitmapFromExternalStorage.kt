package com.lory.library.uil.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.uil.dto.ImageData


open class FetchBitmapFromExternalStorage<MKR> : FetchBitmapTask<MKR> {

    /**
     * Constructor
     * @param context
     * @param imageData
     * @param asyncCallBack
     * @param additionalPayLoad
     */
    constructor(context: Context, imageData: ImageData, asyncCallBack: AsyncCallBack<Bitmap?, Any>?, additionalPayLoad: MKR) : super(context, imageData, asyncCallBack, additionalPayLoad) {

    }

    override fun getBitmapFromPath(): Bitmap? {
        try {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            options.inJustDecodeBounds = true
            options.inSampleSize = 1
            BitmapFactory.decodeFile(imageData.path, options)
            options.inSampleSize = getSampleSize(options.outWidth, options.outHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(imageData.path, options)
        } catch (e: Exception) {
            Log.e("UIL", "getBitmapFromPath : EXTERNAL : ${e.message} ")
            return null
        }
    }
}