package com.cds.childrensmall.util

import android.view.View
import java.util.*

/**
 * 防止双击
 */
 abstract class NoDoubleClickListener: View.OnClickListener {

    val MIN_CLICK_DELAY_TIME = 1000
    private var lastClickTime = 0L
    protected abstract fun onNoDoubleClick(view:View?)

    override fun onClick(v: View?) {
        val currentTime = Calendar.getInstance().timeInMillis
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME){
            lastClickTime = currentTime
            onNoDoubleClick(v)
        }
    }

}