package com.cfb.brs

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Process
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import com.cfb.brs.cq.RCHelper
import com.cfb.brs.fn.CFBam
import com.cfb.brs.fn.StartActivity
import com.google.firebase.FirebaseApp

//color2023317
class CFB : Application() {

    companion object {
        internal var instance: CFB? = null
        private val baseInstance: CFB by lazy { instance!! }
        val context: Context by lazy { baseInstance.applicationContext }
    }

    override fun attachBaseContext(base: Context?) {
        instance = this
        super.attachBaseContext(base)
    }


    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
        }

        if (getProcess(this) != packageName) return
        //初始化广告
        CFBam.instance.initialize(this)
        AppUtils.registerAppStatusChangedListener(object : Utils.OnAppStatusChangedListener {
            override fun onForeground(activity: Activity?) {
                if (activity !is StartActivity) {
                    val intent = Intent(activity, StartActivity::class.java)
                    intent.putExtra("isB", true)
                    activity?.startActivity(intent)
                }
            }

            override fun onBackground(activity: Activity?) {
            }

        })
        RCHelper.mFirebaseRemoteConfig.fetchAndActivate()
    }


    private fun getProcess(cxt: Context): String? {
        val pid = Process.myPid()
        val am = cxt.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (app in runningApps) {
            if (app.pid == pid) {
                return app.processName
            }
        }
        return null
    }
}