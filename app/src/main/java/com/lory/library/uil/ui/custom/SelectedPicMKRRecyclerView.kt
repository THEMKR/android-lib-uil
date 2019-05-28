package com.lory.library.uil.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import com.lory.library.ui.ui.view.MKRRecyclerView
import com.lory.library.uil.R


class SelectedPicMKRRecyclerView : MKRRecyclerView {

    private val paint = Paint()
    private var color1: Int = Color.BLACK
    private var color2: Int = Color.RED

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    fun init() {
        paint.setAntiAlias(true);
        paint.strokeWidth = resources.displayMetrics.widthPixels.toFloat() * 0.005F
        color1 = ContextCompat.getColor(context, R.color.colorPrimary)
        color2 = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    }

    override fun onDraw(c: Canvas?) {
        super.onDraw(c)
        val strokeWidth: Int = if (paint.strokeWidth.toInt() > 0) {
            paint.strokeWidth.toInt()
        } else {
            1
        }

        var x: Int = 0
        var temp = false
        while (x <= width + height) {
            temp = !temp
            paint.color = if (temp) {
                color1
            } else {
                color2
            }
            c?.drawLine(x.toFloat(), 0F - strokeWidth.toFloat(), x.toFloat() - height.toFloat(), height.toFloat() + strokeWidth.toFloat(), paint)
            x += strokeWidth
        }
    }
}