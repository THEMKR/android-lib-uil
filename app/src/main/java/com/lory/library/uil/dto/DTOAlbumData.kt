package com.lory.library.uil.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lory.library.uil.ImageInfo

/**
 * @author THEMKR
 */
class DTOAlbumData {

    /**
     * Storage Location [com.lory.library.uil.utils.Constants.STORAGE_TYPE]
     */
    @SerializedName("storageType")
    @Expose
    val storageType: Int

    /**
     * Album name
     */
    @SerializedName("albumName")
    @Expose
    val albumName: String

    /**
     * Album name
     */
    @SerializedName("imageInfoList")
    @Expose
    val imageInfoList: ArrayList<ImageInfo>

    constructor(storageType: Int, albumName: String) {
        this.storageType = storageType
        this.albumName = albumName
        imageInfoList = ArrayList()
    }

    override fun toString(): String {
        return super.toString() + " : $albumName : ${imageInfoList.size}"
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is DTOAlbumData) {
            return other.storageType.equals(storageType) && other.albumName.equals(albumName, true)
        }
        return false
    }

    override fun hashCode(): Int {
        return albumName.hashCode()
    }
}