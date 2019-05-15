package com.lory.library.uil.ui.adapter.viewholder

import android.support.v7.widget.CardView
import android.view.View
import com.lory.library.ui.ui.adapter.BaseViewHolder
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.R
import com.lory.library.uil.dto.ImageData
import com.lory.library.uil.dto.Model
import com.lory.library.uil.ui.custom.MKRImageView
import com.lory.library.uil.utils.Constants
import com.lory.library.uil.utils.Tracer

/**
 * Created by mkr on 14/3/18.
 */

class GalleryPicVH : BaseViewHolder<ImageData> {

    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".GalleryPicVH";
    }

    private val mkrImageView: MKRImageView
    private val selectedView: View
    private val cardView: CardView

    /**
     * Constructor
     *
     * @param itemView
     */
    constructor(itemView: View) : super(itemView) {
        Tracer.debug(TAG, "GalleryAlbumVH: ")
        mkrImageView = itemView.findViewById<MKRImageView>(R.id.item_pic_imageView)
        selectedView = itemView.findViewById<View>(R.id.item_pic_view_selected)
        cardView = itemView.findViewById<CardView>(R.id.item_pic_cardView)
        cardView.setOnClickListener(this)
    }

    override fun bindData(dto: ImageData) {
        Tracer.debug(TAG, "bindData: " + dto)
        if (dto == null) {
            return
        }
        cardView.tag = dto
        selectedView.visibility = if (Model.getInstance().selectedImageDataList.contains(dto)) {
            View.VISIBLE
        } else {
            View.GONE
        }
        mkrImageView.imageData = dto.clone(Constants.DEFAULT_PIC_ITEM_LOAD_SIZE)
    }
}
