package com.lory.library.uil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import android.widget.ImageView
import com.lory.library.uil.controller.ImageLoader
import com.lory.library.uil.dto.CropSection
import com.lory.library.uil.ui.GalleryActivity
import com.lory.library.uil.utils.Constants
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
                var drawable = ContextCompat.getDrawable(context, R.drawable.default_image) ?: return BitmapFactory.decodeResource(context.resources, R.drawable.ic_default)
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

        // =============================================================================================================
        // =============================================================================================================
        // =============================================================================================================
        // LOAD IMAGE START
        // =============================================================================================================
        // =============================================================================================================
        // =============================================================================================================

        /**
         * Method to load Image
         * @param imageInfo Imfo of the Image
         * @param onImageLoaded Callback to get back the loaded image form the dest location definen in imageInfo
         * @param onImageAlterOperation Callback used to alter the image before fully loaded and return bak to the caller
         */
        fun loadImage(imageInfo: ImageInfo, onImageLoaded: ImageLoader.OnImageLoaded?, onImageAlterOperation: ImageLoader.OnImageAlterOperation?) {

        }

        /**
         * Method to load Image
         * @param imageView View on which the loaded image is shown
         * @param imageInfo Imfo of the Image
         * @param onImageLoaded Callback to get back the loaded image form the dest location definen in imageInfo
         * @param onImageAlterOperation Callback used to alter the image before fully loaded and return bak to the caller
         */
        fun loadImage(imageView: ImageView, imageInfo: ImageInfo, onImageLoaded: ImageLoader.OnImageLoaded?, onImageAlterOperation: ImageLoader.OnImageAlterOperation?) {
            imageView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener{
                override fun onViewDetachedFromWindow(v: View?) {

                }

                override fun onViewAttachedToWindow(v: View?) {

                }
            })
        }

        // =============================================================================================================
        // =============================================================================================================
        // =============================================================================================================
        // LOAD IMAGE END
        // =============================================================================================================
        // =============================================================================================================
        // =============================================================================================================

        // =============================================================================================================
        // =============================================================================================================
        // =============================================================================================================
        // IMAGE INFO OPERATION START
        // =============================================================================================================
        // =============================================================================================================
        // =============================================================================================================

        /**
         * Method to return ImageInfo with New Size
         * @param imageInfo Pass the ImageInfo of current image on which you want to do this operation and get the new one
         * @param dimensionPer
         * @return Return the newImageData as pass in parameter
         */
        fun resizeImage(imageInfo: ImageInfo, dimensionPer: Float): ImageInfo {
            return ImageInfo.Builder()
                .setCropSection(imageInfo.cropSection)
                .setDimenPer(dimensionPer)
                .setFlipType(imageInfo.flipType)
                .setOrientation(imageInfo.orientation)
                .setStorageLocation(imageInfo.path)
                .setStorageType(imageInfo.storageType)
                .build()
        }

        /**
         * Method to return ImageInfo with New CropSection
         * @param imageInfo Pass the ImageInfo of current image on which you want to do this operation and get the new one
         * @param cropSection
         * @return Return the newImageData as pass in parameter
         */
        fun cropImage(imageInfo: ImageInfo, cropSection: CropSection): ImageInfo {
            return ImageInfo.Builder()
                .setCropSection(cropSection)
                .setDimenPer(imageInfo.dimensionPer)
                .setFlipType(imageInfo.flipType)
                .setOrientation(imageInfo.orientation)
                .setStorageLocation(imageInfo.path)
                .setStorageType(imageInfo.storageType)
                .build()
        }

        /**
         * Method to return ImageInfo with New FlipType
         * @param imageInfo Pass the ImageInfo of current image on which you want to do this operation and get the new one
         * @param flipType
         * @return Return the newImageData as pass in parameter
         */
        fun flipImage(imageInfo: ImageInfo, flipType: Constants.FLIP_TYPE): ImageInfo {
            return ImageInfo.Builder()
                .setCropSection(imageInfo.cropSection)
                .setDimenPer(imageInfo.dimensionPer)
                .setOrientation(imageInfo.orientation)
                .setStorageLocation(imageInfo.path)
                .setStorageType(imageInfo.storageType)
                .setFlipType(
                    when (imageInfo.flipType) {
                        Constants.FLIP_TYPE.BOTH.value -> {
                            when (flipType) {
                                Constants.FLIP_TYPE.BOTH -> {
                                    Constants.FLIP_TYPE.NAN.value
                                }
                                Constants.FLIP_TYPE.HORIZONTAL -> {
                                    Constants.FLIP_TYPE.VERTICAL.value
                                }
                                Constants.FLIP_TYPE.VERTICAL -> {
                                    Constants.FLIP_TYPE.HORIZONTAL.value
                                }
                                else -> {
                                    imageInfo.flipType
                                }
                            }
                        }
                        Constants.FLIP_TYPE.HORIZONTAL.value -> {
                            when (flipType) {
                                Constants.FLIP_TYPE.BOTH -> {
                                    Constants.FLIP_TYPE.VERTICAL.value
                                }
                                Constants.FLIP_TYPE.HORIZONTAL -> {
                                    Constants.FLIP_TYPE.NAN.value
                                }
                                Constants.FLIP_TYPE.VERTICAL -> {
                                    Constants.FLIP_TYPE.BOTH.value
                                }
                                else -> {
                                    imageInfo.flipType
                                }
                            }
                        }
                        Constants.FLIP_TYPE.VERTICAL.value -> {
                            when (flipType) {
                                Constants.FLIP_TYPE.BOTH -> {
                                    Constants.FLIP_TYPE.HORIZONTAL.value
                                }
                                Constants.FLIP_TYPE.HORIZONTAL -> {
                                    Constants.FLIP_TYPE.BOTH.value
                                }
                                Constants.FLIP_TYPE.VERTICAL -> {
                                    Constants.FLIP_TYPE.NAN.value
                                }
                                else -> {
                                    imageInfo.flipType
                                }
                            }
                        }
                        else -> {
                            imageInfo.flipType
                        }
                    }
                ).build()
        }

        // =============================================================================================================
        // =============================================================================================================
        // =============================================================================================================
        // IMAGE INFO OPERATION END
        // =============================================================================================================
        // =============================================================================================================
        // =============================================================================================================
    }
}