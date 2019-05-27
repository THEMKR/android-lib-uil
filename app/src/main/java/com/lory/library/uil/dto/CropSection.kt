package com.lory.library.uil.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CropSection {

    /**
     * Left Percent to crop
     */
    @SerializedName("left")
    @Expose
    var left: Float = 0F

    /**
     * Top Percent to crop
     */
    @SerializedName("top")
    @Expose
    var top: Float = 0F

    /**
     * RIGHT Percent to crop
     * @since 1 mean full width
     */
    @SerializedName("right")
    @Expose
    var right: Float = 1F

    /**
     * BOTTOM Percent to crop
     * @since 1 mean full height
     */
    @SerializedName("bottom")
    @Expose
    var bottom: Float = 1F

    override fun toString(): String {
        return "CorpSection[$left-$top-$right-$bottom]"
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is CropSection) {
            return left == other.left && top == other.top && right == other.right && bottom == other.bottom
        }
        return false
    }
}