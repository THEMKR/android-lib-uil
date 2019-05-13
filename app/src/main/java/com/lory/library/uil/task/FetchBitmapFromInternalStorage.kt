package com.lory.library.uil.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.uil.dto.ImageData


class FetchBitmapFromInternalStorage : FetchBitmapTask {

    constructor(context: Context, imageData: ImageData, asyncCallBack: AsyncCallBack<Bitmap, Any>?) : super(context, imageData, asyncCallBack) {

    }

    override fun getBitmapFromPath(): Bitmap {
        var bitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        options.inJustDecodeBounds = true
        options.inSampleSize = 1
        BitmapFactory.decodeFile(imageData.path, options)
        options.inSampleSize = getSampleSize(options.outWidth, options.outHeight)
        options.inJustDecodeBounds = false
        bitmap = BitmapFactory.decodeFile(imageData.path, options)
        return bitmap
    }
}