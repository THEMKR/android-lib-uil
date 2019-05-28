package com.lory.library.uil.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lory.library.storage.session.SessionStorage
import com.lory.library.uil.utils.Constants

open class ImageData {

    /**
     * Unique Key used by [SessionStorage]
     */
    val key: String
        get() {
            return "$path-$storageType-$flipType-$dimensionPer-$cropSection"
        }

    /**
     * Section Of Image to be crop L,T,R,B
     */
    @SerializedName("cropSection")
    @Expose
    var cropSection: CropSection = CropSection()

    /**
     * Type of Storage Location [Constants.STORAGE_TYPE.value]
     */
    @SerializedName("storageType")
    @Expose
    var storageType: Int = Constants.STORAGE_TYPE.EXTERNAL.value

    /**
     * Flip Type [Constants.FLIP_TYPE.value]
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

    // ================================================================================================
    // ================================================================================================
    // ================================================================================================
    // ================================================================================================
    // ================================================================================================
    // ================================================================================================
    // ================================================================================================


    /**
     * Method to clone the Object based on the New Dimension given
     * @param dimensionPer
     */
    fun clone(dimensionPer: Float): ImageData {
        val imageData = ImageData()
        imageData.storageType = storageType
        imageData.path = path
        imageData.cropSection = cropSection
        imageData.flipType = flipType
        imageData.dimensionPer = dimensionPer
        return imageData
    }

    /**
     * Method to clone the Object based on the New CropSection
     * @param cropSection
     */
    fun clone(cropSection: CropSection): ImageData {
        val imageData = ImageData()
        imageData.storageType = storageType
        imageData.path = path
        imageData.cropSection = cropSection
        imageData.flipType = flipType
        imageData.dimensionPer = dimensionPer
        return imageData
    }

    /**
     * Method to clone the Object based on the New FlipType
     * @param dimensionPer
     */
    fun clone(flipType: Int): ImageData {
        val imageData = ImageData()
        imageData.storageType = storageType
        imageData.path = path
        imageData.cropSection = cropSection
        imageData.flipType = flipType
        imageData.dimensionPer = dimensionPer
        return imageData
    }
}