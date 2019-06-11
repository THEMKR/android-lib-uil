package com.lory.library.uil.ui.adapter.viewholder

import android.view.View
import com.lory.library.ui.ui.adapter.BaseViewHolder
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.R
import com.lory.library.uil.UILLib
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.ui.custom.MKRImageInfoView
import com.lory.library.uil.utils.Constants
import com.lory.library.uil.utils.Tracer

/**
 * Created by mkr on 14/3/18.
 */

class SelectedPicVH : BaseViewHolder<ImageInfo> {

    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".GalleryPicVH";
    }

    private val mkrImageView: MKRImageInfoView
    private val cancelView: View

    /**
     * Constructor
     *
     * @param itemView
     */
    constructor(itemView: View) : super(itemView) {
        Tracer.debug(TAG, "GalleryAlbumVH: ")
        mkrImageView = itemView.findViewById<MKRImageInfoView>(R.id.item_selected_pic_imageView)
        cancelView = itemView.findViewById<View>(R.id.item_selected_pic_imageView_cancel)
        cancelView.setOnClickListener(this)
        mkrImageView.scaleType = MKRImageInfoView.SCALE_TYPE.CENTER_CROP
    }

    override fun bindData(dto: ImageInfo) {
        Tracer.debug(TAG, "bindData: " + dto)
        if (dto == null) {
            return
        }
        cancelView.tag = dto
        mkrImageView.imageInfo = ImageInfo.resizeImage(dto, Constants.DEFAULT_PIC_ITEM_LOAD_SIZE)
    }
}
