package com.lory.library.uil.ui.adapter.viewholder

import android.support.v7.widget.CardView
import android.view.View
import android.widget.TextView
import com.lory.library.ui.ui.adapter.BaseViewHolder
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.R
import com.lory.library.uil.UILLib
import com.lory.library.uil.dto.DTOAlbumData
import com.lory.library.uil.ui.custom.MKRImageInfoView
import com.lory.library.uil.utils.Constants
import com.lory.library.uil.utils.Tracer

/**
 * Created by mkr on 14/3/18.
 */

class GalleryAlbumVH : BaseViewHolder<DTOAlbumData> {

    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".GalleryAlbumVH";
    }

    private val mkrImageView: MKRImageInfoView
    private val cardView: CardView
    private val textView: TextView

    /**
     * Constructor
     *
     * @param itemView
     */
    constructor(itemView: View) : super(itemView) {
        Tracer.debug(TAG, "GalleryAlbumVH: ")
        mkrImageView = itemView.findViewById<MKRImageInfoView>(R.id.item_album_imageView)
        textView = itemView.findViewById<TextView>(R.id.item_album_textView_albumName)
        cardView = itemView.findViewById<CardView>(R.id.item_album_cardView)
        cardView.setOnClickListener(this)
    }

    override fun bindData(dto: DTOAlbumData) {
        Tracer.debug(TAG, "bindData: " + dto)
        if (dto == null) {
            return
        }
        cardView.tag = dto
        textView.text = dto.albumName
        val imagePathList = dto.imageInfoList
        mkrImageView.imageInfo = ImageInfo.resizeImage(imagePathList[0], Constants.DEFAULT_ALBUM_ITEM_LOAD_SIZE)
    }
}
