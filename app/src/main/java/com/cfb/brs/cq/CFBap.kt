package com.cfb.brs.cq

import android.util.Base64
import com.cfb.brs.ce.CFBa
import com.cfb.brs.ch.CFBau
import org.json.JSONObject


object CFBap {
    const val LOADING = "cfb_inter_s"
    const val HOME = "cfb_inter_h"
    const val NATIVE = "cfb_native"

    val cacheList = HashMap<String, CFBa>()

    fun hasCache(key: String): Boolean {
        synchronized(cacheList) {
            if (cacheList.isEmpty()) return false
            val cache = cacheList[key] ?: return false
            return if (cache.isAva()) {
                true
            } else {
                cache.onDestroy()
                cacheList.remove(key)
                false
            }
        }
    }

    fun getCache(key: String): CFBa? {
        synchronized(cacheList) {
            if (cacheList.isEmpty()) return null
            val cache = cacheList[key] ?: return null
            return if (!cache.isAva()) {
                cache.onDestroy()
                null
            } else {
                cache
            }
        }
    }

    fun getRequestLists(sk: String): List<CFBau> {
        try {
            val s = RCHelper.getAdConfig()
            val json = JSONObject(String(Base64.decode(s.toByteArray(), Base64.DEFAULT)))
            val jsonArray = json.getJSONArray("cfb_config")

            val adReqList = mutableListOf<CFBau>()
            for (i in 0 until jsonArray.length()) {
                val obj: JSONObject = jsonArray.getJSONObject(i)
                val key = obj.getString("cfb_key")
                val jsonArray2 = obj.getJSONArray("cfb_ids")
                val au: MutableList<CFBau> = mutableListOf()
                for (j in 0 until jsonArray2.length()) {
                    val obj2: JSONObject = jsonArray2.getJSONObject(j)
                    val id = obj2.getString("cfb_id")
                    val priority = obj2.getInt("cfb_priority")
                    val t = when (obj2.optString("cfb_type")) {
                        "nav" -> 0
                        "inter" -> 1
                        "open" -> 2
                        else -> 1
                    }
                    au.add(CFBau(id, priority, t))
                }
                au.sortBy { -it.priority }
                if (key == sk) {
                    adReqList.addAll(au)
                }
            }
            return adReqList
        } catch (e: Exception) {
            e.printStackTrace()
            return mutableListOf()
        }
    }

}
