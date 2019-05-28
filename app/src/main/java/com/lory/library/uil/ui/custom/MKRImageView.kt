package com.lory.library.uil.ui.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.View
import com.lory.library.storage.session.SessionStorage
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.controller.ImageLoader
import com.lory.library.uil.dto.ImageData
import com.lory.library.uil.utils.Tracer
import com.lory.library.uil.utils.UilUtils

class MKRImageView : View, ImageLoader.OnImageLoaded {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".MKRImageView"
    }

    private val imageMatrix = Matrix()

    /**
     * Image data to be set
     */
    var imageData: ImageData? = null
        set(value) {
            imageLoader?.removeImage(field)
            field = value
            if (field != null) {
                val savedBitmap = SessionStorage.getInstance(context).getValue<Bitmap>(field!!.key)
                if (savedBitmap != null) {
                    bitmap = savedBitmap
                } else {
                    bitmap = UilUtils.getDefaultBitmap(context)
                    imageLoader?.loadImage(field, this)
                }
            } else {
                bitmap = UilUtils.getDefaultBitmap(context)
            }
        }

    /**
     * Drawing Bitmap of the View
     */
    private var bitmap: Bitmap? = null
        set(value) {
            var value = value
            if (value == null) {
                value = UilUtils.getDefaultBitmap(context)
            }
            field = value
            setMatrix()
            invalidate()
        }

    private var imageLoader: ImageLoader? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    /**
     * Method to init the View
     */
    private fun init() {
        imageMatrix.reset()
        imageLoader = ImageLoader.getInstance(context)
        bitmap = UilUtils.getDefaultBitmap(context)
    }

    override fun onDetachedFromWindow() {
        imageLoader?.removeImage(imageData)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = bitmap
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (bitmap?.isRecycled ?: true) {
            bitmap = UilUtils.getDefaultBitmap(context)
        }
        if (bitmap?.equals(UilUtils.getDefaultBitmap(context)) ?: true) {
            imageLoader?.loadImage(imageData, this)
        }
        canvas?.drawBitmap(bitmap, imageMatrix, null)
    }

    override fun onImageLoaded(bitmap: Bitmap?, imageData: ImageData) {
        Tracer.debug(TAG, "onImageLoaded : $bitmap : $imageData")
        if (bitmap != null && !bitmap!!.isRecycled && imageData.equals(this.imageData)) {
            this.bitmap = bitmap
        } else {
            this.bitmap = UilUtils.getDefaultBitmap(context)
            imageLoader?.loadImage(this.imageData!!, this)
        }
        invalidate()
    }

    /**
     * Method to set the matrix based on Bitmap
     * @param bitmap
     */
    private fun setMatrix() {
        imageMatrix.reset()
        setMatrixCropCenter()
    }

    /**
     * Set the matrix by cropping the center of the Bitmap
     */
    private fun setMatrixCropCenter() {
        val bitmapLamda = (bitmap?.width?.toFloat() ?: 1F) / (bitmap?.height?.toFloat() ?: 1F)
        val viewLamda = width.toFloat() / height.toFloat()
        if (viewLamda <= bitmapLamda) {
            val scale: Float = height.toFloat() / (bitmap?.height?.toFloat() ?: 1F)
            imageMatrix.setScale(scale, scale)
            imageMatrix.postTranslate(-((bitmap?.width ?: width).toFloat() * scale - width.toFloat()) / 2F, 0F)
        } else {
            val scale: Float = width.toFloat() / (bitmap?.width?.toFloat() ?: 1F)
            imageMatrix.setScale(scale, scale)
            imageMatrix.postTranslate(0F, -((bitmap?.height ?: height).toFloat() * scale - height.toFloat()) / 2F)
        }
    }
}