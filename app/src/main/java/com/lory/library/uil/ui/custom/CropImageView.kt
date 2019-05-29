package com.lory.library.uil.ui.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
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

open class CropImageView : View, ImageLoader.OnImageLoaded {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".MKRImageDataView"
    }

    enum class CROPPING_TYPE {
        CIRCLE,
        RECT,
        SQUARE
    }

    /**
     * Image DRAW Rect
     */
    private val rectDrawBitmap = Rect()

    /**
     * CROP Section Image DRAW Rect
     */
    private val rectDrawCropSection = Rect()

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
            setRectCordinate()
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
        canvas?.drawBitmap(bitmap, null, rectDrawBitmap, null)
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
     * Method to reset the coordinate of the rect
     */
    private fun setRectCordinate() {
        setRectDrawBitmap()
        resetRectDrawCropSection()
    }

    /**
     * To reset the coordinate of the rectDrawBitmap
     */
    private fun setRectDrawBitmap() {
        val bitmapLamda = (bitmap?.width?.toFloat() ?: 1F) / (bitmap?.height?.toFloat() ?: 1F)
        val viewLamda = width.toFloat() / height.toFloat()
        if (viewLamda <= bitmapLamda) {
            // MATH WIDTH
            rectDrawBitmap.left = 0
            rectDrawBitmap.right = width
            val heightReq = ((height.toFloat() * (bitmap?.height?.toFloat() ?: 1F)) / (bitmap?.width?.toFloat() ?: 1F)).toInt()
            rectDrawBitmap.top = (height - heightReq) shr 1
            rectDrawBitmap.bottom = rectDrawBitmap.top + heightReq
        } else {
            // MATCH HEIGHT
            val widthReq = ((width.toFloat() * (bitmap?.width?.toFloat() ?: 1F)) / (bitmap?.height?.toFloat() ?: 1F)).toInt()
            rectDrawBitmap.left = (width - widthReq) shr 1
            rectDrawBitmap.right = rectDrawBitmap.top + widthReq
            rectDrawBitmap.top = 0
            rectDrawBitmap.bottom = height
        }
    }

    /**
     * Method to reset the Rect of Crop Section
     */
    private fun resetRectDrawCropSection() {

    }
}