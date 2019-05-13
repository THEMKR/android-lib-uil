package com.lory.library.uil.ui.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
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
                    imageLoader.loadImage(field, this)
                }
            } else {
                bitmap = UilUtils.getDefaultBitmap(context)
            }
            invalidate()
        }

    private lateinit var rectF: RectF
    private lateinit var bitmap: Bitmap
    private lateinit var imageLoader: ImageLoader

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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        imageData = imageData
    }

    override fun onDetachedFromWindow() {
        imageLoader.removeImage(imageData)
        super.onDetachedFromWindow()
    }

    /**
     * Method to init the View
     */
    private fun init() {
        rectF = RectF()
        imageLoader = ImageLoader.getInstance(context)
        bitmap = UilUtils.getDefaultBitmap(context)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        rectF.left = 0F
        rectF.top = 0F
        rectF.right = width.toFloat()
        rectF.bottom = height.toFloat()

        if (bitmap.isRecycled) {
            bitmap = UilUtils.getDefaultBitmap(context)
            imageLoader.loadImage(imageData, this)
        }

        canvas?.drawBitmap(bitmap, null, rectF, null)
    }

    override fun onImageLoaded(bitmap: Bitmap, imageData: ImageData) {
        Tracer.debug(TAG, "onImageLoaded : $bitmap : $imageData")
        if (imageData.equals(this.imageData)) {
            this.bitmap = bitmap
        } else {
            imageLoader.loadImage(this.imageData!!, this)
        }
        invalidate()
    }

}