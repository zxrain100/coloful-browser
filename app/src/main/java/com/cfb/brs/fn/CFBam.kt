package com.cfb.brs.fn

import android.content.Context
import com.cfb.brs.ce.CFBa
import com.cfb.brs.ch.CFBau
import com.cfb.brs.cq.CFBap
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CFBam {
    companion object {
        val instance: CFBam by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { CFBam() }
    }

    private lateinit var context: Context
    private var isLoad = hashMapOf<String, Boolean>()

    fun initialize(context: Context): CFBam {
        this.context = context
        try {
            MobileAds.initialize(context) {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    suspend fun build(key: String): CFBa? = withContext(Dispatchers.Main) {
        val lists = CFBap.getRequestLists(key)
        var ret: CFBa?
        for (req in lists) {
            if (req.id.isNotEmpty()) {
                ret = build(key, req)
                if (ret != null) {
                    return@withContext ret
                }
            }
        }
        return@withContext null
    }

    suspend fun builds(vararg key: String) = withContext(Dispatchers.Main) {
        val list = mutableListOf<CFBa>()
        for (k in key) {
            val ret = build(k)
            if (ret != null) {
                list.add(ret)
            }
        }
        return@withContext list
    }


    fun get(vararg key: String): CFBa? = getCache(*key, index = 0)


    private suspend fun build(key: String, abpAu: CFBau): CFBa? =
        withContext(Dispatchers.Main) {
            if (isLoad[key] == true) {
                return@withContext null
            }
            if (CFBap.hasCache(key)) {
                val abpA = get(key)
                if (abpA != null) {
                    return@withContext abpA
                }
            }
            isLoad[key] = true
            val ret = runCatching {
                when (abpAu.type) {
                    0 -> buildNativeAd(key, abpAu.id)
                    1 -> buildInterstitialAd(key, abpAu.id)
                    2 -> buildOpenAd(key, abpAu.id)
                    else -> buildInterstitialAd(key, abpAu.id)
                }
            }
            isLoad[key] = false

            if (ret.isSuccess) {
                val ad = ret.getOrNull()
                if (ad != null) {
                    return@withContext ad
                }
            }
            return@withContext null
        }


    private fun getCache(vararg key: String, index: Int): CFBa? {
        val count = key.size
        val abpA = CFBap.getCache(key[index])
        return abpA ?: if (index < count - 1) {
            getCache(*key, index = index + 1)
        } else {
            null
        }
    }

    private suspend fun buildNativeAd(key: String, id: String): CFBa {
        return suspendCancellableCoroutine {
            //广告需要ui线程上加载
            val adLoader = AdLoader.Builder(context, id)
                .forNativeAd { abpa ->
                    val nativeAd = CFBa(abpa)
                    CFBap.cacheList[key]?.onDestroy()
                    CFBap.cacheList[key] = nativeAd
                    it.resume(nativeAd)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        it.resumeWithException(Exception(p0.code.toString()))
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build()
                )
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }


    private suspend fun buildInterstitialAd(key: String, id: String): CFBa {
        return suspendCancellableCoroutine {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(context, id, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    it.resumeWithException(Exception(adError.code.toString()))
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    val abpa = CFBa(interstitialAd)
                    //之前有缓存的广告，先将之前的广告销毁
                    CFBap.cacheList[key]?.onDestroy()
                    CFBap.cacheList[key] = abpa
                    it.resume(abpa)
                }
            })
        }
    }

    private suspend fun buildOpenAd(key: String, id: String): CFBa {
        return suspendCancellableCoroutine {
            AppOpenAd.load(
                context,
                id,
                AdRequest.Builder().build(),
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        super.onAdFailedToLoad(adError)
                        it.resumeWithException(Exception(adError.code.toString()))
                    }

                    override fun onAdLoaded(p0: AppOpenAd) {
                        super.onAdLoaded(p0)
                        val abpa = CFBa(p0)
                        //之前有缓存的广告，先将之前的广告销毁
                        CFBap.cacheList[key]?.onDestroy()
                        CFBap.cacheList[key] = abpa
                        it.resume(abpa)
                    }
                }
            )
        }
    }

}