package com.lory.library.uil.ui.adapter.viewholder

import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.View
import com.lory.library.ui.ui.adapter.BaseViewHolder
import com.lory.library.ui.utils.Tracer
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.R
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.dto.Model
import com.lory.library.uil.ui.custom.MKRImageInfoView
import com.lory.library.uil.utils.Constants

/**
 * Created by mkr on 14/3/18.
 */

class GalleryPicVH : BaseViewHolder<ImageInfo> {

    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".GalleryPicVH";
    }

    private val mkrImageView: MKRImageInfoView
    private val cardView: CardView

    /**
     * Constructor
     *
     * @param itemView
     */
    constructor(itemView: View) : super(itemView) {
        Tracer.debug(TAG, "GalleryAlbumVH: ")
        mkrImageView = itemView.findViewById<MKRImageInfoView>(R.id.item_pic_imageView)
        cardView = itemView.findViewById<CardView>(R.id.item_pic_cardView)
        cardView.setOnClickListener(this)
        mkrImageView.scaleType = MKRImageInfoView.SCALE_TYPE.CENTER_CROP
    }

    override fun bindData(dto: ImageInfo) {
        Tracer.debug(TAG, "bindData: " + dto)
        if (dto == null) {
            return
        }
        cardView.tag = dto
        cardView.setCardBackgroundColor(
            if (Model.getInstance().selectedImageInfoList.contains(dto)) {
                ContextCompat.getColor(context, R.color.selected)
            } else {
                ContextCompat.getColor(context, R.color.card_background)
            }
        )
        mkrImageView.imageInfo = ImageInfo.resizeImage(dto, Constants.DEFAULT_PIC_ITEM_LOAD_SIZE)
    }
}
