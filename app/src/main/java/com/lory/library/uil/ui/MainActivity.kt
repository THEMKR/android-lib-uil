package com.lory.library.uil.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.lory.library.storage.session.SessionStorage
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.R
import com.lory.library.uil.UIL
import com.lory.library.uil.utils.Tracer

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tracer.debug(TAG, "onCreate : ")
        setContentView(R.layout.activity_main)
        SessionStorage.getInstance(this).clear()
        findViewById<View>(R.id.activity_main_open_gallery).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Tracer.debug(TAG, "onClick : ")
                UIL.openGallery(this@MainActivity, 1001, 10, false)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Tracer.debug(TAG, "onActivityResult : $requestCode")
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                Tracer.debug(TAG, "onActivityResult : SUCCESS : ")
                val parseResponseIntent = UIL.parseGalleryResponse(data)
                Tracer.debug(TAG, "onActivityResult : SUCCESS : ${parseResponseIntent.size}")
                for (imageData in parseResponseIntent) {
                    Tracer.debug(TAG, "onActivityResult : SUCCESS : $imageData")
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Tracer.debug(TAG, "onActivityResult : CANCEL : ")
            }
        }
    }
}
