package com.lory.library.uil.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lory.library.storage.session.SessionStorage
import com.lory.library.uil.utils.Constants

open class ImageData {

    companion object {

        /**
         * Method to return ImageData with New Size
         * @param imageData
         * @param newImageData
         * @param dimensionPer
         * @return Return the newImageData as pass in parameter
         */
        fun resize(imageData: ImageData, newImageData: ImageData, dimensionPer: Float): ImageData {
            newImageData.storageType = imageData.storageType
            newImageData.path = imageData.path
            newImageData.cropSection = imageData.cropSection
            newImageData.flipType = imageData.flipType
            newImageData.dimensionPer = dimensionPer
            newImageData.orientation = imageData.orientation
            return newImageData
        }

        /**
         * Method to return ImageData with New CropSection
         * @param imageData
         * @param newImageData
         * @param cropSection
         * @return Return the newImageData as pass in parameter
         */
        fun crop(imageData: ImageData, newImageData: ImageData, cropSection: CropSection): ImageData {
            newImageData.storageType = imageData.storageType
            newImageData.path = imageData.path
            newImageData.cropSection = cropSection
            newImageData.flipType = imageData.flipType
            newImageData.dimensionPer = imageData.dimensionPer
            newImageData.orientation = imageData.orientation
            return newImageData
        }

        /**
         * Method to return ImageData with New FlipType
         * @param imageData
         * @param newImageData
         * @param flipType
         * @return Return the newImageData as pass in parameter
         */
        fun flip(imageData: ImageData, newImageData: ImageData, flipType: Constants.FLIP_TYPE): ImageData {
            newImageData.storageType = imageData.storageType
            newImageData.path = imageData.path
            newImageData.cropSection = imageData.cropSection
            newImageData.dimensionPer = imageData.dimensionPer
            newImageData.orientation = imageData.orientation
            when (imageData.flipType) {
                Constants.FLIP_TYPE.BOTH.value -> {
                    when (flipType) {
                        Constants.FLIP_TYPE.BOTH -> {
                            newImageData.flipType = Constants.FLIP_TYPE.NAN.value
                        }
                        Constants.FLIP_TYPE.HORIZONTAL -> {
                            newImageData.flipType = Constants.FLIP_TYPE.VERTICAL.value
                        }
                        Constants.FLIP_TYPE.VERTICAL -> {
                            newImageData.flipType = Constants.FLIP_TYPE.HORIZONTAL.value
                        }
                        else -> {
                            newImageData.flipType = imageData.flipType
                        }
                    }
                }
                Constants.FLIP_TYPE.HORIZONTAL.value -> {
                    when (flipType) {
                        Constants.FLIP_TYPE.BOTH -> {
                            newImageData.flipType = Constants.FLIP_TYPE.VERTICAL.value
                        }
                        Constants.FLIP_TYPE.HORIZONTAL -> {
                            newImageData.flipType = Constants.FLIP_TYPE.NAN.value
                        }
                        Constants.FLIP_TYPE.VERTICAL -> {
                            newImageData.flipType = Constants.FLIP_TYPE.BOTH.value
                        }
                        else -> {
                            newImageData.flipType = imageData.flipType
                        }
                    }
                }
                Constants.FLIP_TYPE.VERTICAL.value -> {
                    when (flipType) {
                        Constants.FLIP_TYPE.BOTH -> {
                            newImageData.flipType = Constants.FLIP_TYPE.HORIZONTAL.value
                        }
                        Constants.FLIP_TYPE.HORIZONTAL -> {
                            newImageData.flipType = Constants.FLIP_TYPE.BOTH.value
                        }
                        Constants.FLIP_TYPE.VERTICAL -> {
                            newImageData.flipType = Constants.FLIP_TYPE.NAN.value
                        }
                        else -> {
                            newImageData.flipType = imageData.flipType
                        }
                    }
                }
                else -> {
                    newImageData.flipType = imageData.flipType
                }
            }
            return newImageData
        }
    }

    /**
     * Unique Key used by [SessionStorage]
     */
    val key: String
        get() {
            return "[$path][$orientation][$storageType][$flipType][$dimensionPer][$cropSection]"
        }

    /**
     * Section Of Image to be crop L,T,R,B
     */
    @SerializedName("cropSection")
    @Expose
    var cropSection: CropSection = CropSection()

    /**
     * Type of Storage Location [Constants.STORAGE_TYPE].value
     */
    @SerializedName("storageType")
    @Expose
    var storageType: Int = Constants.STORAGE_TYPE.EXTERNAL.value

    /**
     * Flip Type [Constants.FLIP_TYPE].value
     */
    @SerializedName("flipType")
    @Expose
    var flipType: Int = Constants.FLIP_TYPE.NAN.value

    /**
     * Scale Dimension Correspond to the Screen Width
     * [<OL><LI>0 : 0% of Device Screen Width</LI><LI>1 : 100% of Device Screen Width</LI><LI>-1 : Original Image Size</LI></OL>]
     */
    @SerializedName("dimensionPer")
    @Expose
    var dimensionPer: Float = 1F

    /**
     * Type of Orientation Location [Constants.ORIENTATION].value
     */
    @SerializedName("orientation")
    @Expose
    var orientation: Int = Constants.ORIENTATION.NAN.value

    /**
     * Image Path
     */
    @SerializedName("path")
    @Expose
    var path: String = ""

    override fun toString(): String {
        return super.toString() + " : $key"
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is ImageData) {
            return path.equals(other.path) &&
                    dimensionPer.equals(other.dimensionPer) &&
                    flipType.equals(other.flipType) &&
                    storageType.equals(other.storageType) &&
                    cropSection.equals(other.cropSection)
        }
        return false
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}