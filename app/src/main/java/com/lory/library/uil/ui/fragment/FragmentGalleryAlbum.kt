package com.lory.library.uil.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lory.library.ui.callback.OnBaseActivityListener
import com.lory.library.ui.callback.OnBaseFragmentListener
import com.lory.library.ui.ui.adapter.BaseAdapter
import com.lory.library.ui.ui.adapter.BaseAdapterItem
import com.lory.library.ui.ui.adapter.BaseViewHolder
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.R
import com.lory.library.uil.dto.DTOAlbumData
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.dto.Model
import com.lory.library.uil.provider.FragmentProvider
import com.lory.library.uil.ui.adapter.AdapterItemHandler
import com.lory.library.uil.utils.JsonUtil
import com.lory.library.uil.utils.Tracer

class FragmentGalleryAlbum : Fragment(), OnBaseFragmentListener, BaseViewHolder.VHClickable {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".FragmentGalleryAlbum"
        const val EXTRA_ALBUM_LIST: String = "EXTRA_ALBUM_LIST"
    }

    private val baseAdapter: BaseAdapter = BaseAdapter(AdapterItemHandler())
    private val baseAdapterSelected: BaseAdapter = BaseAdapter(AdapterItemHandler())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Tracer.debug(TAG, "onViewCreated : ")
        super.onViewCreated(view, savedInstanceState)
        val data = arguments?.getString(EXTRA_ALBUM_LIST, "{}") ?: "{}"
        val dtoAlbumDataList = JsonUtil.toObjectTokenType<ArrayList<DTOAlbumData>>(data, true)
        baseAdapter?.setVHClickCallback(this)
        baseAdapterSelected?.setVHClickCallback(this)

        val recyclerView = view?.findViewById(R.id.fragment_album_recyclerView_pic) as RecyclerView
        recyclerView?.layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
        recyclerView?.adapter = baseAdapter
        val baseAdapterItemList: ArrayList<BaseAdapterItem<*>> = ArrayList()
        val adapterViewType = AdapterItemHandler.AdapterItemViewType.GALLERY_ALBUM.ordinal
        for (dto in dtoAlbumDataList) {
            Tracer.debug(TAG, "onViewCreated : $dto ")
            baseAdapterItemList.add(BaseAdapterItem(adapterViewType, dto))
        }
        baseAdapter.updateAdapterItemList(baseAdapterItemList)

        val recyclerViewSelected = view?.findViewById(R.id.fragment_album_recyclerView_selected_pic) as RecyclerView
        recyclerViewSelected?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewSelected?.adapter = baseAdapterSelected
        updateSelectedImageList()
    }

    override fun onViewHolderClicked(holder: BaseViewHolder<*>, view: View) {
        Tracer.debug(TAG, "onViewHolderClicked : ")
        when (view.id) {
            R.id.item_album_cardView -> {
                val tagDto = view.tag ?: return
                if (tagDto !is DTOAlbumData) {
                    return
                }
                val tag = FragmentProvider.TAG.GALLERY_PIC
                val fragment = FragmentProvider.getFragment(tag)
                val bundle = Bundle()
                bundle.putString(FragmentGalleryPic.EXTRA_IMAGE_LIST, JsonUtil.toStringTokenType<ArrayList<ImageInfo>>(tagDto.imageInfoList, false))
                if (activity is OnBaseActivityListener) {
                    (activity as OnBaseActivityListener)?.onBaseActivityAddFragment(fragment, bundle, true, tag)
                }
            }
            R.id.item_selected_pic_imageView_cancel -> {
                val tagDto = (view.tag ?: return) as? ImageInfo ?: return
                val selectedImageDataList = Model.getInstance().selectedImageInfoList
                if (selectedImageDataList.contains(tagDto)) {
                    selectedImageDataList.remove(tagDto)
                }
                updateSelectedImageList()
            }
        }
    }

    override fun onBackPressed(): Boolean {
        Tracer.debug(TAG, "onBackPressed : ")
        return false
    }

    override fun onNotifyFragment(flag: Int, bundle: Bundle) {
        Tracer.debug(TAG, "onNotifyFragment : ")
    }

    override fun onPermissionsResult(requestCode: Int, permissions: Array<String>?, grantResults: IntArray) {
        Tracer.debug(TAG, "onPermissionsResult : ")
    }

    override fun onPopFromBackStack() {
        Tracer.debug(TAG, "onPopFromBackStack : ")
        updateSelectedImageList()
    }

    override fun onRefresh() {
        Tracer.debug(TAG, "onRefresh : ")
        updateSelectedImageList()
    }

    /**
     * Method to update the UI of the selected Image
     */
    private fun updateSelectedImageList() {
        Tracer.debug(TAG, "updateSelectedImageList : ")
        val dtoSelectedList = Model.getInstance().selectedImageInfoList
        if (dtoSelectedList.size > 0) {
            view?.findViewById<View>(R.id.fragment_album_recyclerView_selected_pic)?.visibility = View.VISIBLE
            val baseAdapterItemList: ArrayList<BaseAdapterItem<*>> = ArrayList()
            val adapterViewType = AdapterItemHandler.AdapterItemViewType.SELECTED_PIC.ordinal
            for (dto in dtoSelectedList) {
                Tracer.debug(TAG, "updateSelectedImageList : $dto ")
                baseAdapterItemList.add(BaseAdapterItem(adapterViewType, dto))
            }
            baseAdapterSelected.updateAdapterItemList(baseAdapterItemList)
        } else {
            view?.findViewById<View>(R.id.fragment_album_recyclerView_selected_pic)?.visibility = View.GONE
        }
    }
}
