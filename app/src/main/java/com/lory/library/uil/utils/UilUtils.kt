package com.lory.library.uil.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import com.lory.library.uil.R


class UilUtils {
    companion object {
        private var DEFAULT_BITMAP: Bitmap? = null

        /**
         * Method to get the default Bitmap
         * @param context
         */
        fun getDefaultBitmap(context: Context): Bitmap {
            if (DEFAULT_BITMAP == null) {
                var drawable = ContextCompat.getDrawable(context, R.drawable.default_image) ?: return BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    drawable = DrawableCompat.wrap(drawable!!).mutate()
                }
                val dimenW = (context.resources.displayMetrics.widthPixels.toFloat() * 0.5F).toInt()
                val dimenH = (dimenW.toFloat() * drawable.intrinsicHeight.toFloat() / drawable.intrinsicWidth.toFloat()).toInt()
                val bitmap = Bitmap.createBitmap(dimenW, dimenH, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
                drawable.draw(canvas)
                DEFAULT_BITMAP = bitmap
            }
            return DEFAULT_BITMAP!!
        }

        /**
         * Method to get the sample size of bitmap
         *
         * @param srcWidth      src bitmap width
         * @param srcHeight     src bitmap height
         * @param desiredWidth  dest bitmap width
         * @param desiredHeight dest bitmap height
         * @return the sample size
         */
        fun calculateInSampleSize(srcWidth: Int, srcHeight: Int, desiredWidth: Int, desiredHeight: Int): Int {
            var inSampleSize = 1
            if (srcHeight > desiredHeight || srcWidth > desiredWidth) {
                val heightRatio = Math.round(srcHeight.toFloat() / desiredHeight.toFloat())
                val widthRatio = Math.round(srcWidth.toFloat() / desiredWidth.toFloat())
                inSampleSize = if (heightRatio <= widthRatio) {
                    heightRatio
                } else {
                    widthRatio
                }
            }
            return if (inSampleSize % 2 == 0) {
                inSampleSize
            } else {
                inSampleSize + 1
            }
        }
    }
}