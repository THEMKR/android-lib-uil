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
         * @param dimensionPer
         */
        fun resize(imageData: ImageData, dimensionPer: Float): ImageData {
            val dto = ImageData()
            dto.storageType = imageData.storageType
            dto.path = imageData.path
            dto.cropSection = imageData.cropSection
            dto.flipType = imageData.flipType
            dto.dimensionPer = dimensionPer
            dto.orientation = imageData.orientation
            return dto
        }

        /**
         * Method to return ImageData with New CropSection
         * @param imageData
         * @param cropSection
         */
        fun crop(imageData: ImageData, cropSection: CropSection): ImageData {
            val dto = ImageData()
            dto.storageType = imageData.storageType
            dto.path = imageData.path
            dto.cropSection = cropSection
            dto.flipType = imageData.flipType
            dto.dimensionPer = imageData.dimensionPer
            dto.orientation = imageData.orientation
            return dto
        }

        /**
         * Method to return ImageData with New FlipType
         * @param imageData
         * @param flipType
         */
        fun flip(imageData: ImageData, flipType: Constants.FLIP_TYPE): ImageData {
            val dto = ImageData()
            dto.storageType = imageData.storageType
            dto.path = imageData.path
            dto.cropSection = imageData.cropSection
            dto.dimensionPer = imageData.dimensionPer
            dto.orientation = imageData.orientation
            when (imageData.flipType) {
                Constants.FLIP_TYPE.BOTH.value -> {
                    when (flipType) {
                        Constants.FLIP_TYPE.BOTH -> {
                            dto.flipType = Constants.FLIP_TYPE.NAN.value
                        }
                        Constants.FLIP_TYPE.HORIZONTAL -> {
                            dto.flipType = Constants.FLIP_TYPE.VERTICAL.value
                        }
                        Constants.FLIP_TYPE.VERTICAL -> {
                            dto.flipType = Constants.FLIP_TYPE.HORIZONTAL.value
                        }
                        else -> {
                            dto.flipType = imageData.flipType
                        }
                    }
                }
                Constants.FLIP_TYPE.HORIZONTAL.value -> {
                    when (flipType) {
                        Constants.FLIP_TYPE.BOTH -> {
                            dto.flipType = Constants.FLIP_TYPE.VERTICAL.value
                        }
                        Constants.FLIP_TYPE.HORIZONTAL -> {
                            dto.flipType = Constants.FLIP_TYPE.NAN.value
                        }
                        Constants.FLIP_TYPE.VERTICAL -> {
                            dto.flipType = Constants.FLIP_TYPE.BOTH.value
                        }
                        else -> {
                            dto.flipType = imageData.flipType
                        }
                    }
                }
                Constants.FLIP_TYPE.VERTICAL.value -> {
                    when (flipType) {
                        Constants.FLIP_TYPE.BOTH -> {
                            dto.flipType = Constants.FLIP_TYPE.HORIZONTAL.value
                        }
                        Constants.FLIP_TYPE.HORIZONTAL -> {
                            dto.flipType = Constants.FLIP_TYPE.BOTH.value
                        }
                        Constants.FLIP_TYPE.VERTICAL -> {
                            dto.flipType = Constants.FLIP_TYPE.NAN.value
                        }
                        else -> {
                            dto.flipType = imageData.flipType
                        }
                    }
                }
                else -> {
                    dto.flipType = imageData.flipType
                }
            }
            return dto
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