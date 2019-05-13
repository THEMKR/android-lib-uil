package com.lory.library.uil.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lory.library.ui.ui.adapter.BaseAdapterItemHandler
import com.lory.library.ui.ui.adapter.BaseViewHolder
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.R
import com.lory.library.uil.utils.Tracer

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
        NONE, GALLERY_ALBUM, GALLERY_IMAGE
    }

    override fun createHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        Tracer.debug(TAG, "createHolder: " + getItemViewType(viewType));
        when (getItemViewType(viewType)) {
            //AdapterItemHandler.AdapterItemViewType.GALLERY_ALBUM -> return GalleryAlbumVH(inflater.inflate(R.layout.item_gallery_album, parent, false))
            //AdapterItemHandler.AdapterItemViewType.GALLERY_PIC -> return GalleryImageVH(inflater.inflate(R.layout.item_gallery_image, parent, false))
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
