package com.lory.library.uil.ui.adapter.viewholder

import android.view.View
import com.lory.library.ui.ui.adapter.BaseViewHolder
import com.lory.library.ui.utils.Tracer
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.R
import com.lory.library.uil.ui.custom.MKRImageInfoView
import com.lory.library.uil.utils.Constants

/**
 * @author THEMKR
 */
class SelectedPicVH : BaseViewHolder<ImageInfo> {

    companion object {
        private val TAG: String = "GalleryPicVH";
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
        cancelView.tag = dto
        mkrImageView.imageInfo = ImageInfo.cloneBuilder(dto).setDimenPer(Constants.DEFAULT_PIC_ITEM_LOAD_SIZE).build()
    }
}
