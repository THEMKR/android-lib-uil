package com.lory.library.uil.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.lory.library.uil.BuildConfig
import com.lory.library.uil.R

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = BuildConfig.BASE_TAG + ".MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.activity_main_open_gallery).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // LAUNCH GALLERY
            }
        })
    }
}
