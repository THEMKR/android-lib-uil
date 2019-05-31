package com.lory.library.uil.ui.adapter.viewholder

import android.view.View
import com.lory.library.ui.ui.adapter.BaseViewHolder
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.R
import com.lory.library.uil.UILLib
import com.lory.library.uil.dto.ImageInfo
import com.lory.library.uil.ui.custom.MKRImageDataView
import com.lory.library.uil.utils.Constants
import com.lory.library.uil.utils.Tracer

/**
 * Created by mkr on 14/3/18.
 */

class SelectedPicVH : BaseViewHolder<ImageInfo> {

    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".GalleryPicVH";
    }

    private val mkrImageView: MKRImageDataView
    private val cancelView: View

    /**
     * Constructor
     *
     * @param itemView
     */
    constructor(itemView: View) : super(itemView) {
        Tracer.debug(TAG, "GalleryAlbumVH: ")
        mkrImageView = itemView.findViewById<MKRImageDataView>(R.id.item_selected_pic_imageView)
        cancelView = itemView.findViewById<View>(R.id.item_selected_pic_imageView_cancel)
        cancelView.setOnClickListener(this)
    }

    override fun bindData(dto: ImageInfo) {
        Tracer.debug(TAG, "bindData: " + dto)
        if (dto == null) {
            return
        }
        cancelView.tag = dto
        mkrImageView.imageInfo = UILLib.resizeImage(dto, , Constants.DEFAULT_PIC_ITEM_LOAD_SIZE)
    }
}
