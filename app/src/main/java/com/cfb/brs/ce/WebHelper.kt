package com.cfb.brs.ce

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.util.Patterns
import android.webkit.*
import com.cfb.brs.ch.CFBMark
import com.cfb.brs.fi.CFBHistory
import com.cfb.brs.fz.CFBDBManager
import com.cfb.brs.fz.WebCallback
import java.net.URISyntaxException

class WebHelper(val callback: WebCallback) {

    private lateinit var context: Context

    private var isPageLoad = false

    fun initWebView(webView: WebView) {
        context = webView.context
        val mSettings = webView.settings
        //支持获取手势焦点
        webView.requestFocusFromTouch()
        webView.isHorizontalFadingEdgeEnabled = true
        webView.isVerticalFadingEdgeEnabled = false
        webView.isVerticalScrollBarEnabled = false

        //支持js
        mSettings.javaScriptEnabled = true
        mSettings.javaScriptCanOpenWindowsAutomatically = true
        mSettings.builtInZoomControls = true
        mSettings.displayZoomControls = true
        mSettings.loadWithOverviewMode = true
        // 支持插件
        mSettings.pluginState = WebSettings.PluginState.ON
        mSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        // 自适应屏幕
        mSettings.useWideViewPort = true
        mSettings.loadWithOverviewMode = true
        // 支持缩放
        mSettings.setSupportZoom(false)
        // 隐藏原声缩放控件
        mSettings.displayZoomControls = false
        // 支持内容重新布局
        mSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN;
        mSettings.supportMultipleWindows()
        mSettings.setSupportMultipleWindows(true)
        // 设置缓存模式
        mSettings.domStorageEnabled = true
        mSettings.databaseEnabled = true
        mSettings.cacheMode = WebSettings.LOAD_DEFAULT
        //mSettings.setAppCacheEnabled(true)
        //mSettings.setAppCachePath(webView.context.cacheDir.absolutePath)
        // 设置可访问文件
        mSettings.allowFileAccess = true
        mSettings.setNeedInitialFocus(true)
        mSettings.blockNetworkImage = false
        // 支持自定加载图片
        mSettings.loadsImagesAutomatically = true
        mSettings.setNeedInitialFocus(true)
        // 设定编码格式
        mSettings.defaultTextEncodingName = "UTF-8"

        //帮助WebView处理各种通知、请求事件
        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                isPageLoad = true
                callback.onWebStarted(url)
                super.onPageStarted(view, url, favicon)
            }

            override fun onLoadResource(view: WebView, url: String) {
                super.onLoadResource(view, url)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                isPageLoad = false
                val url = request.url.toString()
                //isLoad = false
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    //设置cookie
                    val cookieManager = CookieManager.getInstance()
                    val ck = cookieManager.getCookie(url)
                    if (!ck.isNullOrEmpty()) {
                        cookieManager.setAcceptCookie(true)
                        cookieManager.setCookie(url, ck)
                        cookieManager.flush()
                    }
                    view.loadUrl(url)
                    val cookies = cookieManager.getCookie(url)
                    //更新cookie
                    if (ck != cookies && !cookies.isNullOrEmpty()) {
                        cookieManager.setCookie(url, cookies)
                    }
                    return true
                } else {
                    try {
                        //处理intent协议
                        if (url.startsWith("intent://")) {
                            val intent: Intent
                            try {
                                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                                intent.addCategory("android.intent.category.BROWSABLE")
                                intent.component = null
                                intent.selector = null
                                val resolves: List<ResolveInfo> =
                                    context.packageManager.queryIntentActivities(intent, 0)
                                if (resolves.isNotEmpty()) {
                                    context.startActivity(intent)
                                }
                                return true
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            }
                        }

                        // 处理自定义scheme协议
                        try {
                            // 以下固定写法
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                                    or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            context.startActivity(intent)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        return true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                return false

            }


            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (url.isNotEmpty() && isPageLoad) {
                    isPageLoad = false
                    val title = webView.copyBackForwardList().currentItem?.title ?: ""
                    val uri = webView.copyBackForwardList().currentItem?.url ?: ""

                    println("onPageFinished title=$title,url=$uri")
                    if (uri.isNotEmpty() && uri != "about:blank") {
                        CFBDBManager.instance.pageHistoryDao()
                            .addPage(CFBHistory(uri, title, System.currentTimeMillis()))
                        callback.onWebFinish(uri)
                    }
                }
                super.onPageFinished(view, url)
            }
        }

        //辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                callback.onWebProgress(newProgress)
                super.onProgressChanged(view, newProgress)
            }
        }
    }

    fun isUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }

    fun addBookMar(url: String, title: String) {
        if (url.isNotEmpty()) {
            val item = CFBDBManager.instance.pageMarkDao().getByUrl(url)
            if (item == null) {
                CFBDBManager.instance.pageMarkDao()
                    .addPage(CFBMark(url, title, System.currentTimeMillis()))
            }
        }
    }

    fun isBookmarkUrl(url: String): Boolean {
        if (url.isNotEmpty() && isUrl(url)) {
            val item = CFBDBManager.instance.pageMarkDao().getByUrl(url)
            if (item != null) {
                return true
            }
        }
        return false
    }

}