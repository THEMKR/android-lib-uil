package com.lory.library.uil.ui.adapter.viewholder

import android.support.v7.widget.CardView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lory.library.ui.ui.adapter.BaseViewHolder
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.R
import com.lory.library.uil.dto.DTOAlbumData
import com.lory.library.uil.dto.ImageData
import com.lory.library.uil.ui.custom.MKRImageView
import com.lory.library.uil.utils.Constants
import com.lory.library.uil.utils.Tracer

/**
 * Created by mkr on 14/3/18.
 */

class GalleryAlbumVH : BaseViewHolder<DTOAlbumData> {

    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".GalleryAlbumVH";
    }

    private val mkrImageView1: MKRImageView
    private val mkrImageView2: MKRImageView
    private val mkrImageView3: MKRImageView
    private val mkrImageView4: MKRImageView
    private val cardView: CardView
    private val textView: TextView
    private val sectionBottom: ViewGroup

    /**
     * Constructor
     *
     * @param itemView
     */
    constructor(itemView: View) : super(itemView) {
        Tracer.debug(TAG, "GalleryAlbumVH: ")
        mkrImageView1 = itemView.findViewById<MKRImageView>(R.id.item_album_imageView_1)
        mkrImageView2 = itemView.findViewById<MKRImageView>(R.id.item_album_imageView_2)
        mkrImageView3 = itemView.findViewById<MKRImageView>(R.id.item_album_imageView_3)
        mkrImageView4 = itemView.findViewById<MKRImageView>(R.id.item_album_imageView_4)
        textView = itemView.findViewById<TextView>(R.id.item_album_textView_albumName)
        cardView = itemView.findViewById<CardView>(R.id.item_album_cardView)
        cardView.setOnClickListener(this)
        sectionBottom = itemView.findViewById<ViewGroup>(R.id.item_album_layout_section_bottom)
    }

    override fun bindData(dto: DTOAlbumData) {
        Tracer.debug(TAG, "bindData: " + dto)
        if (dto == null) {
            return
        }
        cardView.tag = dto
        textView.text = dto.albumName
        mkrImageView1.visibility = View.GONE
        mkrImageView2.visibility = View.GONE
        mkrImageView3.visibility = View.GONE
        mkrImageView4.visibility = View.GONE
        val imagePathList = dto.imagePathList
        when (imagePathList.size) {
            1 -> {
                sectionBottom.visibility = View.GONE
                setMKRImageView(mkrImageView1, imagePathList[0])
            }
            2 -> {
                sectionBottom.visibility = View.GONE
                setMKRImageView(mkrImageView1, imagePathList[0])
                setMKRImageView(mkrImageView2, imagePathList[1])
            }
            3 -> {
                sectionBottom.visibility = View.VISIBLE
                setMKRImageView(mkrImageView1, imagePathList[0])
                setMKRImageView(mkrImageView2, imagePathList[1])
                setMKRImageView(mkrImageView3, imagePathList[2])
            }
            else -> {
                sectionBottom.visibility = View.VISIBLE
                setMKRImageView(mkrImageView1, imagePathList[0])
                setMKRImageView(mkrImageView2, imagePathList[1])
                setMKRImageView(mkrImageView3, imagePathList[2])
                setMKRImageView(mkrImageView4, imagePathList[3])
            }
        }
    }

    /**
     * Method to set the Value of the ImageView
     * @param mkrImageView
     * @param imageData
     */
    private fun setMKRImageView(mkrImageView: MKRImageView, imageData: ImageData) {
        mkrImageView.visibility = View.VISIBLE
        mkrImageView.imageData = imageData.clone(Constants.DEFAULT_ALBUM_ITEM_LOAD_SIZE)
    }
}
