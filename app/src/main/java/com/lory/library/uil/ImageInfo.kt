package com.lory.library.uil

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lory.library.storage.session.SessionStorage
import com.lory.library.uil.dto.CropSection
import com.lory.library.uil.utils.Constants

/**
 * @author THEMKR
 */
open class ImageInfo {

    companion object {

        /**
         * Method to return ImageInfo.Builder with pre filed value of the info passed as param
         * @param imageInfo Pass the ImageInfo of current image on which you want to do this operation and get the new one
         * @return Return the Builder with pre filed value of the info passed as param
         */
        fun cloneBuilder(imageInfo: ImageInfo): ImageInfo.Builder {
            return Builder()
                .setCropSection(imageInfo.cropSection)
                .setDimenPer(imageInfo.dimensionPer)
                .setFlipType(imageInfo.flipType)
                .setOrientation(imageInfo.orientation)
                .setStorageLocation(imageInfo.path)
                .setStorageType(imageInfo.storageType)
                .setIsCached(imageInfo.isCached)
                .setSpecifire(imageInfo.specifire)
        }

        /**
         * Method to return ImageInfo with New FlipType
         * @param imageInfo Pass the ImageInfo of current image on which you want to do this operation and get the new one
         * @param flipType
         * @return Return the newImageData as pass in parameter
         */
        fun cloneFlipImageBuilder(imageInfo: ImageInfo, flipType: Constants.FLIP_TYPE): ImageInfo {
            val build = cloneBuilder(imageInfo)
            when (imageInfo.flipType) {
                Constants.FLIP_TYPE.BOTH.value -> {
                    when (flipType) {
                        Constants.FLIP_TYPE.BOTH -> {
                            build.setFlipType(Constants.FLIP_TYPE.NAN.value)
                        }
                        Constants.FLIP_TYPE.HORIZONTAL -> {
                            build.setFlipType(Constants.FLIP_TYPE.VERTICAL.value)
                        }
                        Constants.FLIP_TYPE.VERTICAL -> {
                            build.setFlipType(Constants.FLIP_TYPE.HORIZONTAL.value)
                        }
                        else -> {
                            build.setFlipType(imageInfo.flipType)
                        }
                    }
                }
                Constants.FLIP_TYPE.HORIZONTAL.value -> {
                    when (flipType) {
                        Constants.FLIP_TYPE.BOTH -> {
                            build.setFlipType(Constants.FLIP_TYPE.VERTICAL.value)
                        }
                        Constants.FLIP_TYPE.HORIZONTAL -> {
                            build.setFlipType(Constants.FLIP_TYPE.NAN.value)
                        }
                        Constants.FLIP_TYPE.VERTICAL -> {
                            build.setFlipType(Constants.FLIP_TYPE.BOTH.value)
                        }
                        else -> {
                            build.setFlipType(imageInfo.flipType)
                        }
                    }
                }
                Constants.FLIP_TYPE.VERTICAL.value -> {
                    when (flipType) {
                        Constants.FLIP_TYPE.BOTH -> {
                            build.setFlipType(Constants.FLIP_TYPE.HORIZONTAL.value)
                        }
                        Constants.FLIP_TYPE.HORIZONTAL -> {
                            build.setFlipType(Constants.FLIP_TYPE.BOTH.value)
                        }
                        Constants.FLIP_TYPE.VERTICAL -> {
                            build.setFlipType(Constants.FLIP_TYPE.NAN.value)
                        }
                        else -> {
                            build.setFlipType(imageInfo.flipType)
                        }
                    }
                }
                else -> {
                    build.setFlipType(flipType.value)
                }
            }
            build.setIsCached(imageInfo.isCached)
            return build.build()
        }
    }

    /**
     * Make Constructor Private
     */
    private constructor() {

    }

    /**
     * Unique Key used by [SessionStorage]
     */
    val key: String
        get() {
            return "[$path][$orientation][$storageType][$flipType][$dimensionPer][$cropSection][$specifire][$isCached]"
        }

    /**
     * ADDITIONAL SPECIFIRE FOR KEY CREATION
     */
    @SerializedName("specifire")
    @Expose
    var specifire: String = ""
        private set

    /**
     * Section Of Image to be cropImage L,T,R,B
     */
    @SerializedName("cropSection")
    @Expose
    var cropSection: CropSection = CropSection()
        private set

    /**
     * Type of Storage Location [Constants.STORAGE_TYPE].value
     */
    @SerializedName("storageType")
    @Expose
    var storageType: Int = Constants.STORAGE_TYPE.EXTERNAL.value
        private set

    /**
     * Flip Type [Constants.FLIP_TYPE].value
     */
    @SerializedName("flipType")
    @Expose
    var flipType: Int = Constants.FLIP_TYPE.NAN.value
        private set

    /**
     * Scale Dimension Correspond to the Screen Width
     * [<OL><LI>0.0 : 0% of Device Screen Width</LI><LI>1.0 : 100% of Device Screen Width</LI><LI>-1.0 : Original Image Size</LI></OL>]
     */
    @SerializedName("dimensionPer")
    @Expose
    var dimensionPer: Float = 1F
        private set

    /**
     * Type of Orientation Location [Constants.ORIENTATION].value
     */
    @SerializedName("orientation")
    @Expose
    var orientation: Int = Constants.ORIENTATION.NAN.value
        private set

    /**
     * Image Location
     */
    @SerializedName("path")
    @Expose
    var path: String = ""
        private set

    /**
     * If True then cached image in LUR Cache
     */
    @SerializedName("isCached")
    @Expose
    var isCached: Boolean = false
        private set

    override fun toString(): String {
        return super.toString() + " : $key"
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is ImageInfo) {
            return path.equals(other.path) &&
                    dimensionPer.equals(other.dimensionPer) &&
                    flipType.equals(other.flipType) &&
                    storageType.equals(other.storageType) &&
                    cropSection.equals(other.cropSection) &&
                    specifire.equals(other.specifire) &&
                    isCached.equals(other.isCached)
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
         * Method to set weather the image is cached in LRUCache or not
         * @param isCached If TRUE then cached image in LRU cache else Not
         */
        fun setIsCached(isCached: Boolean): Builder {
            imageInfo.isCached = isCached
            return this
        }

        //=====================================================================================

        /**
         * Method to set the Src Location of Image
         * @param location [<OL><LI>URL -> STORAGE_TYPE.URL</LI><LI>ASSETS-PATH -> STORAGE_TYPE.ASSSETS</LI><LI>SD-CARD-PATH -> STORAGE_TYPE.EXTERNAL</LI><LI>INTERNAL-PATH -> STORAGE_TYPE.INTERNAL</LI></OL>]
         */
        fun setStorageLocation(location: String): Builder {
            imageInfo.path = location
            return this
        }

        /**
         * Method to set the Src Location of Image
         * @param specifire
         */
        fun setSpecifire(specifire: String): Builder {
            imageInfo.specifire = specifire
            return this
        }

        //=====================================================================================

        /**
         * Method to set the Storage Location TYPE [Constants.STORAGE_TYPE].value
         * @param storageType
         */
        internal fun setStorageType(storageType: Constants.STORAGE_TYPE): Builder {
            imageInfo.storageType = storageType.value
            return this
        }

        /**
         * Method to set the Storage Location TYPE [Constants.STORAGE_TYPE].value
         * @param storageType
         */
        internal fun setStorageType(storageType: Int): Builder {
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
        fun setFlipType(flipType: Constants.FLIP_TYPE): Builder {
            imageInfo.flipType = flipType.value
            return this
        }

        /**
         * Method to set the Flip TYPE [Constants.FLIP_TYPE].value
         * @param flipType
         */
        internal fun setFlipType(flipType: Int): Builder {
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
         * @param orientation [0, 90, 180, 270]
         */
        internal fun setOrientation(orientation: Int): Builder {
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