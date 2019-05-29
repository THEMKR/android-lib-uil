package com.lory.library.uil

import android.app.Activity
import android.content.Intent
import com.lory.library.uil.dto.ImageData
import com.lory.library.uil.ui.GalleryActivity
import com.lory.library.uil.utils.JsonUtil
import com.lory.library.uil.utils.Tracer

class UIL {
    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".UIL"

        /**
         * Method to openGallery the Gallery Activity for result
         * @param activity
         * @param requestCode Code to be return in onActivityRresult
         * @param maxImageCount Number of Image to be requested
         * @param isCountFixed  If TRUE then forced user to pick this much of Image. If FALSE then user my choose less image too
         */
        fun openGallery(activity: Activity, requestCode: Int, maxImageCount: Int, isCountFixed: Boolean) {
            Tracer.debug(TAG, "openGallery : Request Code = $requestCode : Image Count = $maxImageCount")
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
        fun parseGalleryResponse(data: Intent?): ArrayList<ImageData> {
            Tracer.debug(TAG, "parseGalleryResponse : ")
            val data = data?.getStringExtra(GalleryActivity.EXTRA_IMAGE_DATA) ?: "[]"
            val dtoImageLocationList = JsonUtil.toObjectTokenType<ArrayList<ImageData>>(data, false)
            return dtoImageLocationList
        }
    }
}