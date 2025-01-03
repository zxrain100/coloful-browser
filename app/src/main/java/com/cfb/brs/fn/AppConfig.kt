package com.cfb.brs.fn

import android.content.Context
import com.cfb.brs.CFB

class AppConfig{

    companion object {
        val instance: AppConfig by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AppConfig() }
    }

    private val mConfig = (CFB.context.applicationContext ?: CFB.context).getSharedPreferences("cfb_config", Context.MODE_PRIVATE)!!

    fun isSafe() = mConfig.getBoolean("is_safe_mode", false)
    fun setSafeMode(bool: Boolean) {
        mConfig.edit().putBoolean("is_safe_mode", bool).apply()
    }



    fun setRefUrl(string: String) {
        mConfig.edit().putString("ref_url", string).apply()
    }

    fun getRefUrl() = mConfig.getString("ref_url", "") ?: ""


    fun isM(): Boolean {
        val ref = getRefUrl()
        val isM = ref.contains("fb4a") || ref.contains("gclid") || ref.contains("not%20set") || ref.contains(
            "youtubeads"
        )
        return isM
    }

}