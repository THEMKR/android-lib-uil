package com.lory.library.uil.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lory.library.ui.ui.adapter.BaseAdapterItemHandler
import com.lory.library.ui.ui.adapter.BaseViewHolder
import com.lory.library.ui.utils.Tracer
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.R
import com.lory.library.uil.ui.adapter.viewholder.GalleryAlbumVH
import com.lory.library.uil.ui.adapter.viewholder.GalleryPicVH
import com.lory.library.uil.ui.adapter.viewholder.SelectedPicVH

/**
 * Created by mkr on 14/3/18.
 */

class AdapterItemHandler : BaseAdapterItemHandler() {
    companion object {
        private val TAG: String = BuildConfig.BASE_TAG + ".AdapterItemHandler";
    }

    /**
     * Type of view hold by adapter
     */
    enum class AdapterItemViewType {
        NONE, GALLERY_ALBUM, GALLERY_PIC, SELECTED_PIC
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        Tracer.debug(TAG, "createHolder: " + getItemViewType(viewType));
        when (getItemViewType(viewType)) {
            AdapterItemHandler.AdapterItemViewType.GALLERY_ALBUM -> return GalleryAlbumVH(inflater.inflate(R.layout.item_album, parent, false))
            AdapterItemHandler.AdapterItemViewType.GALLERY_PIC -> return GalleryPicVH(inflater.inflate(R.layout.item_pic, parent, false))
            AdapterItemHandler.AdapterItemViewType.SELECTED_PIC -> return SelectedPicVH(inflater.inflate(R.layout.item_selected_pic, parent, false))
            else -> return object : BaseViewHolder<Any>(FrameLayout(inflater.context)) {
                protected override fun bindData(o: Any) {
                    mParent.tag = o
                }
            }
        }
    }

    /**
     * Method to get the ENUM as per viewType
     *
     * @return
     */
    private fun getItemViewType(viewType: Int): AdapterItemViewType {
        return AdapterItemViewType.values()[viewType]
    }
}
