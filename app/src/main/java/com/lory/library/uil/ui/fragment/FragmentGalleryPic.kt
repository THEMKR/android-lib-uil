package com.lory.library.uil.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lory.library.ui.callback.OnBaseActivityListener
import com.lory.library.ui.callback.OnBaseFragmentListener
import com.lory.library.ui.ui.adapter.BaseAdapter
import com.lory.library.ui.ui.adapter.BaseAdapterItem
import com.lory.library.ui.ui.adapter.BaseViewHolder
import com.lory.library.ui.utils.Tracer
import com.lory.library.uil.ImageInfo
import com.lory.library.uil.R
import com.lory.library.uil.dto.Model
import com.lory.library.uil.ui.GalleryActivity
import com.lory.library.uil.ui.adapter.AdapterItemHandler
import com.lory.library.uil.utils.JsonUtil

/**
 * @author THEMKR
 */
class FragmentGalleryPic : Fragment(), OnBaseFragmentListener, BaseViewHolder.VHClickable {
    companion object {
        private const val TAG: String = "FragmentGalleryPic"
        const val EXTRA_IMAGE_LIST: String = "EXTRA_IMAGE_LIST"
    }

    private val baseAdapter: BaseAdapter = BaseAdapter(AdapterItemHandler())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = arguments?.getString(FragmentGalleryPic.EXTRA_IMAGE_LIST, "[]") ?: "[]"
        val dtoImageLocationList = JsonUtil.toObjectTokenType<ArrayList<ImageInfo>>(data, false)
        val recyclerView = view.findViewById(R.id.fragment_pic_recyclerView_pic) as RecyclerView
        recyclerView.layoutManager = GridLayoutManager(activity, 3, RecyclerView.VERTICAL, false)
        recyclerView.adapter = baseAdapter
        baseAdapter.setVHClickCallback(this)
        val baseAdapterItemList: ArrayList<BaseAdapterItem<*>> = ArrayList()
        val adapterViewType = AdapterItemHandler.AdapterItemViewType.GALLERY_PIC.ordinal
        for (dto in dtoImageLocationList) {
            Tracer.debug(TAG, "onViewCreated : $dto ")
            baseAdapterItemList.add(BaseAdapterItem(adapterViewType, dto))
        }
        baseAdapter.updateAdapterItemList(baseAdapterItemList)
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
    }

    override fun onRefresh() {
        Tracer.debug(TAG, "onRefresh : ")
    }

    override fun onViewHolderClicked(holder: BaseViewHolder<*>, view: View) {
        Tracer.debug(TAG, "onViewHolderClicked : ")
        when (view.id) {
            R.id.item_pic_cardView -> {
                val tagDto = (view.tag ?: return) as? ImageInfo ?: return
                val model = Model.getInstance()
                if (model.selectedImageInfoList.contains(tagDto)) {
                    model.selectedImageInfoList.remove(tagDto)
                } else {
                    model.selectedImageInfoList.add(tagDto)
                    if (model.maxPicCount == 1) {
                        val newDtoList: ArrayList<ImageInfo> = ArrayList<ImageInfo>()
                        val selectedDtoList = model.selectedImageInfoList
                        for (imageInfo in selectedDtoList) {
                            newDtoList.add(ImageInfo.cloneBuilder(imageInfo).setDimenPer(-1F).build())
                        }
                        val data = JsonUtil.toStringTokenType<ArrayList<ImageInfo>>(newDtoList, false)
                        val intent = Intent()
                        intent.putExtra(GalleryActivity.EXTRA_IMAGE_DATA, data)
                        activity?.setResult(Activity.RESULT_OK, intent)
                        activity?.finish()
                        return
                    }
                }
                if (activity != null && activity is OnBaseActivityListener) {
                    (activity as OnBaseActivityListener).onBaseActivitySetScreenTitle(" : ${model.selectedImageInfoList.size}/${model.maxPicCount}")
                }
                baseAdapter.notifyDataSetChanged()
            }
        }
    }
}
