package com.lory.library.uil.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lory.library.storage.session.SessionStorage
import com.lory.library.uil.utils.Constants

open class ImageInfo {

    /**
     * Unique Key used by [SessionStorage]
     */
    val key: String
        get() {
            return "[$path][$orientation][$storageType][$flipType][$dimensionPer][$cropSection]"
        }

    /**
     * Section Of Image to be cropImage L,T,R,B
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
     * [<OL><LI>0.0 : 0% of Device Screen Width</LI><LI>1.0 : 100% of Device Screen Width</LI><LI>-1.0 : Original Image Size</LI></OL>]
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
        if (other != null && other is ImageInfo) {
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

    /**
     * Class used to build the ImageData
     */
    class Builder {

        /**
         * Image Info
         */
        private val imageInfo: ImageInfo = ImageInfo()

        /**
         * Method to set the Src Location of Image
         * @param location [URL/File-Path/Assets-Path]
         */
        fun setStorageLocation(location: String): Builder {
            imageInfo.path = location
            return this
        }

        //=====================================================================================

        /**
         * Method to set the Storage Location TYPE [Constants.STORAGE_TYPE].value
         * @param storageType
         */
        fun setStorageType(storageType: Int): Builder {
            imageInfo.storageType = storageType
            return this
        }

        //=====================================================================================

        /**
         * Method to set the Bitmap Crop Section
         * @param cropSection
         */
        fun setCropSection(cropSection: CropSection): Builder {
            imageInfo.cropSection = cropSection
            return this
        }

        //=====================================================================================

        /**
         * Method to set the Flip TYPE [Constants.FLIP_TYPE].value
         * @param flipType
         */
        fun setFlipType(flipType: Int): Builder {
            imageInfo.flipType = flipType
            return this
        }

        //=====================================================================================

        /**
         * Method to set the Scale Dimension Correspond to the Screen Width
         * @param dimensionPer [<OL><LI>0.0 : 0% of Device Screen Width</LI><LI>1.0 : 100% of Device Screen Width</LI><LI>-1.0 : Original Image Size</LI></OL>]
         */
        fun setDimenPer(dimensionPer: Float): Builder {
            imageInfo.dimensionPer = dimensionPer
            return this
        }

        //=====================================================================================

        /**
         * Method to set the Orientation
         * @param orientation
         */
        fun setOrientation(orientation: Constants.ORIENTATION): Builder {
            imageInfo.orientation = orientation.value
            return this
        }

        /**
         * Method to set the Orientation [Constants.ORIENTATION].value [0,90,180,270]
         * @param orientation
         */
        fun setOrientation(orientation: Int): Builder {
            imageInfo.orientation = orientation
            return this
        }

        //=====================================================================================

        /**
         * Method to build and return the ImageData which hold all the Required Info
         */
        fun build(): ImageInfo {
            return imageInfo
        }
    }
}