package com.lory.library.uil.ui.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import com.lory.library.storage.session.SessionStorage
import com.lory.library.ui.utils.Tracer
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.UILLib
import com.lory.library.uil.controller.ImageLoader

open class MKRImageInfoView : View, ImageLoader.OnImageLoaderListener<MKRImageInfoView> {

    companion object {
        private const val TAG: String = "MKRImageInfoView"
    }

    /**
     * Method to set the scale type of the MKRImageInfoView
     */
    enum class SCALE_TYPE {
        CENTER_CROP,
        FIT_CENTER,
        FIT_XY,
        CUSTOM
    }

    /**
     * Hold the scale type
     */
    var scaleType = SCALE_TYPE.CENTER_CROP
        set(value) {
            field = value
            bitmap = bitmap
        }

    /**
     * Rect used to draw the bitmap
     */
    protected val rectDrawBitmap = Rect()

    /**
     * Image data to be set
     */
    var imageInfo: ImageInfo? = null
        set(value) {
            UILLib.removeImage(context, field, this)
            field = value
            if (field != null) {
                val savedBitmap = SessionStorage.getInstance(context).getValue<Bitmap>(field!!.key)
                if (savedBitmap != null) {
                    bitmap = savedBitmap
                } else {
                    bitmap = UILLib.getDefaultBitmap(context)
                    UILLib.loadImage(context, field, this)
                }
            } else {
                bitmap = UILLib.getDefaultBitmap(context)
            }
        }

    /**
     * Drawing Bitmap of the View
     */
    protected var bitmap: Bitmap? = null
        set(value) {
            var value = value
            if (value == null) {
                value = UILLib.getDefaultBitmap(context)
            }
            field = value
            resetRect()
            invalidate()
        }

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
    protected fun init() {
        bitmap = UILLib.getDefaultBitmap(context)
    }

    override fun onDetachedFromWindow() {
        UILLib.removeImage(context, imageInfo, this)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = bitmap
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (bitmap?.isRecycled ?: true) {
            bitmap = UILLib.getDefaultBitmap(context)
        }
        if (bitmap?.equals(UILLib.getDefaultBitmap(context)) ?: true) {
            UILLib.loadImage(context, imageInfo, this)
        }
        canvas?.drawBitmap(bitmap!!, null, rectDrawBitmap, null)
    }

    override fun onImageLoaded(bitmap: Bitmap?, imageInfo: ImageInfo, mkr: MKRImageInfoView) {
        Tracer.debug(TAG, "onImageLoaded : $bitmap : $imageInfo")
        if (bitmap != null && !bitmap!!.isRecycled && imageInfo.equals(this.imageInfo)) {
            mkr.bitmap = bitmap
        } else {
            mkr.bitmap = UILLib.getDefaultBitmap(context)
            UILLib.loadImage(context, this.imageInfo!!, this)
        }
        invalidate()
    }

    override fun onImageAlter(bitmap: Bitmap?, imageInfo: ImageInfo, mkr: MKRImageInfoView): Bitmap? {
        return bitmap
    }

    override fun onImageCaller(): MKRImageInfoView {
        return this
    }

    /**
     * Method to set the rect based on Bitmap
     */
    protected fun resetRect() {
        when (scaleType) {
            SCALE_TYPE.CENTER_CROP -> {
                setRectCropCenter()
            }
            SCALE_TYPE.FIT_CENTER -> {
                setRectCenterInside()
            }
            SCALE_TYPE.FIT_XY -> {
                setRectFitXY()
            }
            else -> {
                setRectCustom()
            }
        }
    }

    /**
     * Set the rect by cropping the center of the Bitmap
     */
    protected fun setRectCropCenter() {
        val bitmapLamda = (bitmap?.width?.toFloat() ?: 1F) / (bitmap?.height?.toFloat() ?: 1F)
        val viewLamda = width.toFloat() / height.toFloat()
        if (viewLamda <= bitmapLamda) {
            // MATCH HEIGHT
            val widthReq = ((width.toFloat() * (bitmap?.width?.toFloat() ?: 1F)) / (bitmap?.height?.toFloat() ?: 1F)).toInt()
            rectDrawBitmap.left = -((widthReq - width) shr 1)
            rectDrawBitmap.right = rectDrawBitmap.top + widthReq
            rectDrawBitmap.top = 0
            rectDrawBitmap.bottom = height
        } else {
            // MATH WIDTH
            rectDrawBitmap.left = 0
            rectDrawBitmap.right = width
            val heightReq = ((height.toFloat() * (bitmap?.height?.toFloat() ?: 1F)) / (bitmap?.width?.toFloat() ?: 1F)).toInt()
            rectDrawBitmap.top = -((heightReq - height) shr 1)
            rectDrawBitmap.bottom = rectDrawBitmap.top + heightReq
        }
    }

    /**
     * Set the rect by fit the center Inside
     */
    protected fun setRectCenterInside() {
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
     * Set the rect by fit XY
     */
    protected fun setRectFitXY() {
        rectDrawBitmap.left = 0
        rectDrawBitmap.right = width
        rectDrawBitmap.top = 0
        rectDrawBitmap.bottom = height
    }

    /**
     * Set the rect by custom, DEFAUKT FIT XY call
     */
    protected fun setRectCustom() {
        setRectFitXY()
    }
}