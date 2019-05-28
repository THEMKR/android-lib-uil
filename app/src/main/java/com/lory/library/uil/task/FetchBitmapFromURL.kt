package com.lory.library.uil.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.uil.dto.ImageData
import java.net.HttpURLConnection
import java.net.URL


open class FetchBitmapFromURL : FetchBitmapTask {

    constructor(context: Context, imageData: ImageData, asyncCallBack: AsyncCallBack<Bitmap?, Any>?) : super(context, imageData, asyncCallBack) {

    }

    override fun getBitmapFromPath(): Bitmap? {
        try {
            val url: URL = URL(imageData.path)
            var connection = url.openConnection() as HttpURLConnection
            var inputStream = connection.getInputStream()
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()
            connection = url.openConnection() as HttpURLConnection
            inputStream = connection.getInputStream()
            options.inSampleSize = getSampleSize(options.outWidth, options.outHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: Exception) {
            Log.e("UIL", "getBitmapFromPath : URL : ${e.message} ")
            return null
        }
    }
}