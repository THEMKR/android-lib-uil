package com.lory.library.uil.dto

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
    val selectedImageDataList: ArrayList<ImageData> = ArrayList<ImageData>()

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
        selectedImageDataList.clear()
        maxPicCount = 1
        isMaxPicCountFixed = false
    }

}