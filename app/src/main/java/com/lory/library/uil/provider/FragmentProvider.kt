package com.lory.library.uil.provider

import androidx.fragment.app.Fragment
import com.lory.library.uil.ui.fragment.FragmentGalleryAlbum
import com.lory.library.uil.ui.fragment.FragmentGalleryPic

/**
 * @author THEMKR
 */
class FragmentProvider {
    companion object {
        fun getFragment(tag: String): Fragment {
            when (tag) {
                TAG.GALLERY_ALBUM -> {
                    return FragmentGalleryAlbum()
                }
                TAG.GALLERY_PIC -> {
                    return FragmentGalleryPic()
                }
                else -> {
                    return Fragment()
                }
            }
        }
    }

    interface TAG {
        companion object {
            val GALLERY_ALBUM = "FragmentAlbum"
            val GALLERY_PIC = "FragmentPic"
        }
    }
}
