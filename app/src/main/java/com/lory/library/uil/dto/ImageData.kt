package com.lory.library.uil.dto

import com.lory.library.storage.session.SessionStorage
import com.lory.library.uil.utils.Constants

class ImageData {

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
    var cropSection: CropSection = CropSection()

    /**
     * Type of Storage Location [Constants.STORAGE_TYPE]
     */
    var storageType: Int = Constants.STORAGE_TYPE.EXTERNAL.value

    /**
     * Flip Type [Constants.FLIP_TYPE]
     */
    var flipType: Int = Constants.FLIP_TYPE.NAN.value

    /**
     * Scale Dimension Correspond to the Screen Width
     * [<OL><LI>0 : 0% of Device Screen Width</LI><LI>1 : 100% of Device Screen Width</LI><LI>-1 : Original Image Size</LI></OL>]
     */
    var dimensionPer: Float = 1F

    /**
     * Image Path
     */
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