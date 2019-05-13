package com.lory.library.uil.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.lory.library.uil.BuildConfig
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Created by delhivery on 21/3/16.
 */
class Tracer {
    companion object {
        private val LOG_ENABLE = !BuildConfig.BUILD_TYPE.equals("release")

        /**
         * Method to print debug log<br></br>
         * [Log] Information
         *
         * @param TAG
         * @param message
         */
        fun debug(TAG: String, message: String) {
            if (LOG_ENABLE) {
                Log.d(TAG, message)
            }
        }

        /**
         * Method to write data in Disk
         *
         * @param TAG
         * @param num
         * @param caller
         * @param data
         */
        fun writeOnDisk(TAG: String, num: Int, caller: String, data: String) {
            if (LOG_ENABLE) {
                val fileDir = File(Environment.getExternalStorageDirectory(), "CTA")
                if (!fileDir.exists()) {
                    fileDir.mkdir()
                }
                val file = File(fileDir.absolutePath, "" + caller + "_" + num + "_" + System.currentTimeMillis() + ".txt")
                if (!file.exists()) {
                    try {
                        file.createNewFile()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                var bufferedWriter: BufferedWriter? = null
                try {
                    bufferedWriter = BufferedWriter(FileWriter(file))
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (bufferedWriter != null) {
                    try {
                        bufferedWriter.write(data)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    try {
                        bufferedWriter.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }

        /**
         * Method to print error log<br></br>
         * [Log] Error
         *
         * @param TAG
         * @param message
         */
        fun error(TAG: String, message: String) {
            if (LOG_ENABLE) {
                Log.e(TAG, message)
            }
        }

        /**
         * Method to print information log<br></br>
         * [Log] Debug
         *
         * @param TAG
         * @param message
         */
        fun info(TAG: String, message: String) {
            if (LOG_ENABLE) {
                Log.i(TAG, message)
            }
        }

        /**
         * Show TOAST<br></br>
         *
         * @param context activity context.
         * @param text    The text to show.  Can be formatted text.
         */
        fun showToast(context: Context, text: String) {
            val toast = Toast.makeText(context, text, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
            toast.show()
        }
    }
}
