package com.lory.library.uil.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.FILTER_BITMAP_FLAG
import com.lory.library.asynctask.AsyncCallBack
import com.lory.library.asynctask.BaseAsyncTask
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.UILLib
import com.lory.library.uil.utils.Constants

/**
 * @author THEMKR
 */
abstract class FetchBitmapTask : BaseAsyncTask<Bitmap?, Any> {

    val imageInfo: ImageInfo

    /**
     * Constructor
     * @param context
     * @param imageData
     * @param asyncCallBack
     */
    constructor(context: Context, imageData: ImageInfo, asyncCallBack: AsyncCallBack<Bitmap?, Any>?) : super(context, asyncCallBack) {
        this.imageInfo = imageData
    }

    /**
     * Method to get from the Location
     */
    abstract fun getBitmapFromPath(): Bitmap?

    override fun doInBackground(): Bitmap? {
        val cropSection = imageInfo.cropSection
        if (cropSection.left >= cropSection.right || cropSection.top >= cropSection.bottom) {
            return null
        }
        // CREATE BITMAP
        var bitmap = getBitmapFromPath() ?: return null
        bitmap = flipBitmap(bitmap)
        bitmap = orientBitmap(bitmap)
        bitmap = cropBitmap(bitmap)
        return bitmap
    }

    /**
     * Method to orient the bitmap
     * @param bitmap
     */
    protected fun orientBitmap(bitmap: Bitmap): Bitmap {
        when (imageInfo.orientation) {
            Constants.ORIENTATION.LANDSCAPE_90.value -> {
                val rotatedBitmap = Bitmap.createBitmap(bitmap.height, bitmap.width, bitmap.config)
                val matrix = Matrix()
                matrix.preRotate(90F)
                matrix.postTranslate(rotatedBitmap.width.toFloat(), 0F)
                val canvas = Canvas(rotatedBitmap)
                canvas.drawBitmap(bitmap, matrix, null)
                if (rotatedBitmap != bitmap) {
                    bitmap.recycle()
                }
                return rotatedBitmap
            }
            Constants.ORIENTATION.LANDSCAPE_180.value -> {
                val rotatedBitmap = Bitmap.createBitmap(bitmap.height, bitmap.width, bitmap.config)
                val matrix = Matrix()
                matrix.preRotate(-90F)
                matrix.postTranslate(0F, rotatedBitmap.height.toFloat())
                val canvas = Canvas(rotatedBitmap)
                canvas.drawBitmap(bitmap, matrix, null)
                if (rotatedBitmap != bitmap) {
                    bitmap.recycle()
                }
                return rotatedBitmap
            }
            Constants.ORIENTATION.REVERSED.value -> {
                val rotatedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                val matrix = Matrix()
                matrix.preRotate(180F)
                matrix.postTranslate(rotatedBitmap.width.toFloat(), rotatedBitmap.height.toFloat())
                val canvas = Canvas(rotatedBitmap)
                canvas.drawBitmap(bitmap, matrix, null)
                if (rotatedBitmap != bitmap) {
                    bitmap.recycle()
                }
                return rotatedBitmap
            }
        }
        return bitmap
    }

    /**
     * Method to flipImage the Bitmap
     * @param bitmap
     */
    protected fun flipBitmap(bitmap: Bitmap): Bitmap {
        when (imageInfo.flipType) {
            Constants.FLIP_TYPE.VERTICAL.value -> {
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
            Constants.FLIP_TYPE.HORIZONTAL.value -> {
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
            Constants.FLIP_TYPE.BOTH.value -> {
                val flipBitmapHorizontal = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                var canvas = Canvas(flipBitmapHorizontal)
                var flipHorizontalMatrix = Matrix()
                flipHorizontalMatrix.setScale(-1F, 1F)
                flipHorizontalMatrix.postTranslate(bitmap.width.toFloat(), 0F)
                canvas.drawBitmap(bitmap, flipHorizontalMatrix, Paint(ANTI_ALIAS_FLAG or FILTER_BITMAP_FLAG))
                if (!bitmap.equals(flipBitmapHorizontal)) {
                    bitmap.recycle()
                }
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
     * Method to cropImage the Bitmap
     * @param bitmap
     */
    protected fun cropBitmap(bitmap: Bitmap): Bitmap {
        val cropSection = imageInfo.cropSection
        // IF NO CROP
        if (cropSection.left <= 0F && cropSection.top <= 0F && cropSection.right >= 1F && cropSection.bottom >= 1F) {
            return bitmap
        }
        val left = (bitmap.width.toFloat() * cropSection.left).toInt()
        val top = (bitmap.height.toFloat() * cropSection.top).toInt()
        val width = (bitmap.width.toFloat() * (cropSection.right - cropSection.left)).toInt()
        val height = (bitmap.height.toFloat() * (cropSection.bottom - cropSection.top)).toInt()
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
        return if (imageInfo.dimensionPer < 0F) {
            1
        } else {
            val displayMetrics = context.resources.displayMetrics
            val cropSection = imageInfo.cropSection
            val width = displayMetrics.widthPixels.toFloat() * imageInfo.dimensionPer / (cropSection.right - cropSection.left)
            val height = displayMetrics.heightPixels.toFloat() * imageInfo.dimensionPer / (cropSection.bottom - cropSection.top)
            UILLib.calculateInSampleSize(optionWidth, optionHeight, width.toInt(), height.toInt())
        }
    }
}