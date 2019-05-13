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
    val selectedImageDataLlist: ArrayList<ImageData> = ArrayList<ImageData>()

}