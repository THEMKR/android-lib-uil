package com.lory.library.uil.ui.adapter.viewholder

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.lory.library.ui.ui.adapter.BaseViewHolder
import com.lory.library.ui.utils.Tracer
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.R
import com.lory.library.uil.dto.DTOAlbumData
import com.lory.library.uil.ui.custom.MKRImageInfoView
import com.lory.library.uil.utils.Constants

/**
 * @author THEMKR
 */
class GalleryAlbumVH : BaseViewHolder<DTOAlbumData> {

    companion object {
        private val TAG: String = "GalleryAlbumVH";
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
        mkrImageView.scaleType = MKRImageInfoView.SCALE_TYPE.CENTER_CROP
    }

    override fun bindData(dto: DTOAlbumData) {
        Tracer.debug(TAG, "bindData: " + dto)
        cardView.tag = dto
        textView.text = dto.albumName
        val imagePathList = dto.imageInfoList
        mkrImageView.imageInfo = ImageInfo.cloneBuilder(imagePathList[0]).setDimenPer(Constants.DEFAULT_ALBUM_ITEM_LOAD_SIZE).build()
    }
}
