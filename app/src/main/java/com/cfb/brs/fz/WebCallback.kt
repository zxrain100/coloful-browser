package com.cfb.brs.fz

interface WebCallback {

    fun onWebStarted(url: String)

    fun onWebFinish(url: String)

    fun onWebProgress(progress: Int)

}