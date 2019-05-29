package com.lory.library.uil.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.util.Log
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.asynctask.BaseAsyncTask
import com.lory.library.uil.dto.ImageData
import com.lory.library.uil.utils.Constants
import com.lory.library.uil.utils.UilUtils


abstract class FetchBitmapTask : BaseAsyncTask<Bitmap?, Any> {

    val imageData: ImageData

    constructor(context: Context, imageData: ImageData, asyncCallBack: AsyncCallBack<Bitmap?, Any>?) : super(context, asyncCallBack) {
        this.imageData = imageData
    }

    /**
     * Method to get from the Location
     */
    abstract fun getBitmapFromPath(): Bitmap?


    override fun doInBackground(): Bitmap? {
        val cropSection = imageData.cropSection
        if (cropSection.left >= cropSection.right || cropSection.top >= cropSection.bottom) {
            Log.e("MKR", "FetchBitmapTask : INVALID CROP SECTION : $cropSection")
            return null
        }
        // CREATE BITMAP
        var bitmap = getBitmapFromPath() ?: return null
        bitmap = cropBitmap(bitmap)
        bitmap = flipBitmap(bitmap)
        return bitmap
    }

    /**
     * Method to flip the Bitmap
     * @param bitmap
     */
    protected fun flipBitmap(bitmap: Bitmap): Bitmap {
        when (imageData.flipType) {
            Constants.FLIP_TYPE.VERTICAL.ordinal -> {
                val flipBitmapVertical = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                val canvas = Canvas(flipBitmapVertical)
                val flipHorizontalMatrix = Matrix()
                flipHorizontalMatrix.setScale(1F, -1F)
                flipHorizontalMatrix.postTranslate(0F, bitmap.height.toFloat())
                canvas.drawBitmap(bitmap, flipHorizontalMatrix, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
                if (!bitmap.equals(flipBitmapVertical)) {
                    bitmap.recycle()
                }
                return flipBitmapVertical
            }
            Constants.FLIP_TYPE.HORIZONTAL.ordinal -> {
                val flipBitmapHorizontal = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                val canvas = Canvas(flipBitmapHorizontal)
                val flipHorizontalMatrix = Matrix()
                flipHorizontalMatrix.setScale(-1F, 1F)
                flipHorizontalMatrix.postTranslate(bitmap.width.toFloat(), 0F)
                canvas.drawBitmap(bitmap, flipHorizontalMatrix, Paint(ANTI_ALIAS_FLAG or FILTER_BITMAP_FLAG))
                if (!bitmap.equals(flipBitmapHorizontal)) {
                    bitmap.recycle()
                }
                return flipBitmapHorizontal
            }
            Constants.FLIP_TYPE.BOTH.ordinal -> {
                val flipBitmapHorizontal = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                var canvas = Canvas(flipBitmapHorizontal)
                var flipHorizontalMatrix = Matrix()
                flipHorizontalMatrix.setScale(-1F, 1F)
                flipHorizontalMatrix.postTranslate(bitmap.width.toFloat(), 0F)
                canvas.drawBitmap(bitmap, flipHorizontalMatrix, Paint(ANTI_ALIAS_FLAG or FILTER_BITMAP_FLAG))
                if (!bitmap.equals(flipBitmapHorizontal)) {
                    bitmap.recycle()
                }
                flipBitmapHorizontal

                val flipBitmapVertical = Bitmap.createBitmap(flipBitmapHorizontal.width, flipBitmapHorizontal.height, flipBitmapHorizontal.config)
                canvas = Canvas(flipBitmapVertical)
                flipHorizontalMatrix = Matrix()
                flipHorizontalMatrix.setScale(1F, -1F)
                flipHorizontalMatrix.postTranslate(0F, bitmap.height.toFloat())
                canvas.drawBitmap(flipBitmapHorizontal, flipHorizontalMatrix, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
                if (!flipBitmapHorizontal.equals(flipBitmapVertical)) {
                    flipBitmapHorizontal.recycle()
                }
                return flipBitmapVertical
            }
        }
        return bitmap
    }

    /**
     * Method to crop the Bitmap
     * @param bitmap
     */
    protected fun cropBitmap(bitmap: Bitmap): Bitmap {
        val cropSection = imageData.cropSection
        // IF NO CROP
        if (cropSection.left == 0F && cropSection.top == 0F && cropSection.right == 1F && cropSection.bottom == 1F) {
            return bitmap
        }
        val left = (bitmap.width.toFloat() * cropSection.left).toInt()
        val top = (bitmap.width.toFloat() * cropSection.top).toInt()
        val width = (bitmap.width.toFloat() * (cropSection.right - cropSection.left)).toInt()
        val height = (bitmap.width.toFloat() * (cropSection.bottom - cropSection.top)).toInt()
        val cropBitmap = Bitmap.createBitmap(bitmap, left, top, width, height)
        if (cropBitmap != bitmap) {
            bitmap.recycle()
        }
        return cropBitmap
    }

    /**
     * Method to get the Sample Size
     * @param optionWidth
     * @param optionHeight
     */
    protected fun getSampleSize(optionWidth: Int, optionHeight: Int): Int {
        return if (imageData.dimensionPer < 0F) {
            1
        } else {
            val displayMetrics = context.resources.displayMetrics
            val cropSection = imageData.cropSection
            val width = displayMetrics.widthPixels.toFloat() * imageData.dimensionPer / (cropSection.right - cropSection.left)
            val height = displayMetrics.heightPixels.toFloat() * imageData.dimensionPer / (cropSection.bottom - cropSection.top)
            UilUtils.calculateInSampleSize(optionWidth, optionHeight, width.toInt(), height.toInt())
        }
    }
}