package com.lory.library.uil.dto

import com.lory.library.uil.ImageInfo

class Model {
    companion object {
        private var MODEL: Model? = null

        /**
         * Method to get the MODEL of the model
         */
        fun getInstance(): Model {
            if (MODEL == null) {
                MODEL = Model()
            }
            return MODEL!!
        }
    }

    private constructor() {

    }

    /**
     * List of selected ImageData
     */
    val selectedImageInfoList: ArrayList<ImageInfo> = ArrayList<ImageInfo>()

    /**
     * Maximum pic user select
     */
    var maxPicCount = 1

    /**
     * Maximum pic user select
     */
    var isMaxPicCountFixed = false

    /**
     * Method to reset this cache
     */
    fun reset() {
        selectedImageInfoList.clear()
        maxPicCount = 1
        isMaxPicCountFixed = false
    }

}