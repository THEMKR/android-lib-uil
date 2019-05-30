package com.lory.library.uil

import com.lory.library.uil.dto.CropSection
import com.lory.library.uil.dto.ImageData
import com.lory.library.uil.utils.Constants

/**
 * Class used to build the ImageData
 */
class ImageDataBuilder {

    /**
     * Image Data
     */
    private val imageData: ImageData = ImageData()

    /**
     * Method to set the Src Location of Image
     * @param location [URL/File-Path/Assets-Path]
     */
    fun setStorageLocation(location: String): ImageDataBuilder {
        imageData.path = location
        return this
    }

    //=====================================================================================

    /**
     * Method to set the Storage Location TYPE
     * @param storageType
     */
    fun setStorageType(storageType: Constants.STORAGE_TYPE): ImageDataBuilder {
        imageData.storageType = storageType.value
        return this
    }

    /**
     * Method to set the Storage Location TYPE [Constants.STORAGE_TYPE].value
     * @param storageType
     */
    fun setStorageType(storageType: Int): ImageDataBuilder {
        imageData.storageType = storageType
        return this
    }

    //=====================================================================================

    /**
     * Method to set the Bitmap Crop Section
     * @param cropSection
     */
    fun setCropSection(cropSection: CropSection): ImageDataBuilder {
        imageData.cropSection = cropSection
        return this
    }

    //=====================================================================================

    /**
     * Method to set the Flip TYPE
     * @param flipType
     */
    fun setFlipType(flipType: Constants.FLIP_TYPE): ImageDataBuilder {
        imageData.flipType = flipType.value
        return this
    }

    /**
     * Method to set the Flip TYPE [Constants.FLIP_TYPE].value
     * @param flipType
     */
    fun setFlipType(flipType: Int): ImageDataBuilder {
        imageData.flipType = flipType
        return this
    }

    //=====================================================================================

    /**
     * Method to set the Scale Dimension Correspond to the Screen Width
     * @param dimensionPer [<OL><LI>0.0 : 0% of Device Screen Width</LI><LI>1.0 : 100% of Device Screen Width</LI><LI>-1.0 : Original Image Size</LI></OL>]
     */
    fun setDimenPer(dimensionPer: Float): ImageDataBuilder {
        imageData.dimensionPer = dimensionPer
        return this
    }

    //=====================================================================================

    /**
     * Method to set the Orientation
     * @param orientation
     */
    fun setOrientation(orientation: Constants.ORIENTATION): ImageDataBuilder {
        imageData.orientation = orientation.value
        return this
    }

    /**
     * Method to set the Orientation [Constants.ORIENTATION].value [0,90,180,270]
     * @param orientation
     */
    fun setOrientation(orientation: Int): ImageDataBuilder {
        imageData.orientation = orientation
        return this
    }

    //=====================================================================================

    /**
     * Method to build and return the ImageData which hold all the Required Info
     */
    fun build(): ImageData {
        return imageData
    }
}