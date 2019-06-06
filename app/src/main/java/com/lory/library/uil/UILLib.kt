package com.lory.library.uil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.lory.library.uil.controller.ImageLoader
import com.lory.library.uil.ui.GalleryActivity
import com.lory.library.uil.utils.JsonUtil
import com.lory.library.uil.utils.Tracer

class UILLib {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".UILLib"
        private var DEFAULT_BITMAP: Bitmap? = null

        /**
         * Method to launchGallery the Gallery Activity for result
         * @param activity
         * @param requestCode Code to be return in onActivityRresult
         * @param maxImageCount Number of Image to be requested
         * @param isCountFixed  If TRUE then forced user to pick this much of Image. If FALSE then user my choose less image too
         */
        fun launchGallery(activity: Activity, requestCode: Int, maxImageCount: Int, isCountFixed: Boolean) {
            Tracer.debug(TAG, "launchGallery : Request Code = $requestCode : Image Count = $maxImageCount")
            val intent = Intent(activity, GalleryActivity::class.java)
            intent.putExtra(GalleryActivity.EXTRA_IMAGE_COUNT, maxImageCount)
            intent.putExtra(GalleryActivity.EXTRA_IS_COUNT_FIXED, isCountFixed)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            activity.startActivityForResult(intent, requestCode)
        }

        /**
         * Method to parse the response Intent
         * @param data Intent received in the onActivityResult of caller gallery
         */
        fun parseGalleryResponse(data: Intent?): ArrayList<ImageInfo> {
            Tracer.debug(TAG, "parseGalleryResponse : ")
            val data = data?.getStringExtra(GalleryActivity.EXTRA_IMAGE_DATA) ?: "[]"
            val dtoImageLocationList = JsonUtil.toObjectTokenType<ArrayList<ImageInfo>>(data, false)
            return dtoImageLocationList
        }

        /**
         * Method to get the ic_default Bitmap
         * @param context
         */
        fun getDefaultBitmap(context: Context): Bitmap {
            if (DEFAULT_BITMAP == null) {
                DEFAULT_BITMAP = getSVGBitmap(context, R.drawable.default_image, 0.5F)
            }
            return DEFAULT_BITMAP!!
        }

        /**
         * Method to get the bitmap from svg based on the device width
         * @param context
         * @param id  SVG RESOURCE ID
         * @param ratio Ration means the per of width pixel 0-1. Where 1 = 100%
         */
        fun getSVGBitmap(context: Context, id: Int, ratio: Float): Bitmap {
            var drawable = ContextCompat.getDrawable(context, id) ?: return BitmapFactory.decodeResource(context.resources, R.drawable.ic_default)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                drawable = DrawableCompat.wrap(drawable!!).mutate()
            }
            val dimenW = (context.resources.displayMetrics.widthPixels.toFloat() * ratio).toInt()
            val dimenH = (dimenW.toFloat() * drawable.intrinsicHeight.toFloat() / drawable.intrinsicWidth.toFloat()).toInt()
            val bitmap = Bitmap.createBitmap(dimenW, dimenH, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
            drawable.draw(canvas)
            return bitmap
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

        /**
         * Method to load Image
         * @param context
         * @param imageInfo Info of the Image
         * @param onImageLoaded Callback to get back the loaded image form the dest location define in imageInfo [pass the same which is pass at the time of load image]
         */
        fun <MKR> removeImage(context: Context, imageInfo: ImageInfo?, onImageLoaded: ImageLoader.OnImageLoaderListener<MKR>) {
            ImageLoader.getInstance(context).remove(imageInfo, onImageLoaded)
        }

        /**
         * Method to cancel Image
         * @param context
         * @param imageInfo Info of the Image
         * @param onImageLoaded Callback to get back the loaded image form the dest location definen in imageInfo
         */
        fun <MKR> loadImage(context: Context, imageInfo: ImageInfo?, onImageLoaded: ImageLoader.OnImageLoaderListener<MKR>) {
            ImageLoader.getInstance(context).loadImage(imageInfo, onImageLoaded)
        }

        /**
         * Method to load Image
         * @param context
         * @param imageView View on which the loaded image is shown (Set Image as SRC)
         * @param imageInfo Info of the Image
         * @param loaderImageId
         * @param errorImageId
         */
        fun loadImage(context: Context, imageView: ImageView, imageInfo: ImageInfo?, loaderImageId: Int, errorImageId: Int) {
            imageView.setImageResource(loaderImageId)
            val callback = object : ImageLoader.OnImageLoaderListener<ImageView> {
                override fun onImageLoaded(bitmap: Bitmap?, imageInfo: ImageInfo, mkr: ImageView) {
                    try {
                        if (bitmap != null && !bitmap.isRecycled) {
                            imageView.setImageBitmap(bitmap)
                        } else {
                            imageView.setImageResource(errorImageId)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "onImageLoaded : IMAGE-VIEW : ${e.message} ")
                    }
                }

                override fun onImageAlter(bitmap: Bitmap?, imageInfo: ImageInfo, mkr: ImageView): Bitmap? {
                    return bitmap
                }

                override fun onImageCaller(): ImageView {
                    return imageView
                }
            }
            // REMOVE OLD IMAGE
            if (imageView.tag != null && imageView.tag is ImageInfo) {
                removeImage(context, imageInfo, callback)
            }

            // LOAD NEW IMAGE
            loadImage(context, imageInfo, callback)
        }

        /**
         * Method to load Image
         * @param context
         * @param view View on which the loaded image is shown (Set Image as background)
         * @param imageInfo Info of the Image
         * @param loaderImageId
         * @param errorImageId
         */
        fun loadImage(context: Context, view: View, imageInfo: ImageInfo?, loaderImageId: Int, errorImageId: Int) {
            view.setBackgroundResource(loaderImageId)
            val callback = object : ImageLoader.OnImageLoaderListener<View> {
                override fun onImageLoaded(bitmap: Bitmap?, imageInfo: ImageInfo, mkr: View) {
                    try {
                        if (bitmap != null && !bitmap.isRecycled) {
                            view.background = BitmapDrawable(bitmap)
                        } else {
                            view.setBackgroundResource(errorImageId)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "onImageLoaded : VIEW : ${e.message} ")
                    }
                }

                override fun onImageAlter(bitmap: Bitmap?, imageInfo: ImageInfo, mkr: View): Bitmap? {
                    return bitmap
                }

                override fun onImageCaller(): View {
                    return view
                }
            }

            // REMOVE OLD IMAGE
            if (view.tag != null && view.tag is ImageInfo) {
                removeImage(context, imageInfo, callback)
            }

            // LOAD NEW IMAGE
            loadImage(context, imageInfo, callback)
        }
    }
}