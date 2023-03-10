package com.cfb.brs

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.TypedValue

object CFBUtils {

    fun px2dip(px: Float): Float = px / CFB.context.resources.displayMetrics.density + 0.5f

    fun dip2px(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            CFB.context.resources.displayMetrics
        ).toInt()


    fun getIconUrlByHost(url: String): String {
        val host = Uri.parse(url).host
        val iconUrl = if (url.startsWith("https")) {
            "https://$host/favicon.ico"
        } else {
            "http://$host/favicon.ico"
        }
        return iconUrl
    }

    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    fun getStatusBarH(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = runCatching { context.resources.getDimensionPixelSize(resourceId) }.getOrDefault(0)
        }

        if (result == 0) {
            result = CFBUtils.dip2px(24)
        }

        return result
    }
}