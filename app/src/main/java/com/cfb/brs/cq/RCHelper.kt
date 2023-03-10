package com.cfb.brs.cq

import android.annotation.SuppressLint
import com.cfb.brs.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

object RCHelper {
    @SuppressLint("StaticFieldLeak")
    val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    init {
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
    }

    fun getAdConfig() = mFirebaseRemoteConfig.getString("cfb_config")

}