package com.cfb.brs.fi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.cfb.brs.fn.AppConfig
import com.cfb.brs.BaseActivity
import com.cfb.brs.fz.CFBBroActivity
import com.cfb.brs.fn.CFBam
import com.cfb.brs.ce.CFBHActivity
import com.cfb.brs.ce.CFBa
import com.cfb.brs.cq.CFBMarkActivity
import com.cfb.brs.cq.CFBap
import com.cfb.brs.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CFBMActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    private var actLauncher: ActivityResultLauncher<Intent>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.statusBar.setStatusBar()

        initListener()
    }


    private fun initListener() {
        binding.browserLayout.setOnClickListener {
            val spba = getHomeInter()
            if (spba != null && AppConfig.instance.isM()) {
                spba.onClose {
                    gotoBro()
                }.show(this)
            } else {
                gotoBro()
            }
        }
        binding.historyLayout.setOnClickListener {
            val spba = getHomeInter()
            if (spba != null && AppConfig.instance.isM()) {
                spba.onClose {
                    gotoHistory()
                }.show(this)
            } else {
                gotoHistory()
            }

        }
        binding.bookmarksLayout.setOnClickListener {
            val spba = getHomeInter()
            if (spba != null && AppConfig.instance.isM()) {
                spba.onClose {
                    gotoMark()
                }.show(this)
            } else {
                gotoMark()
            }
        }

        actLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                if (data != null) {
                    val url = data.getStringExtra("inputUrl") ?: return@registerForActivityResult
                    gotoBro(url)
                }
            }
        }
    }


    private fun gotoHistory() {
        actLauncher?.launch(Intent(this, CFBHActivity::class.java))
    }

    private fun gotoMark() {
        actLauncher?.launch(Intent(this, CFBMarkActivity::class.java))
    }


    private fun gotoBro(url: String? = null) {
        val intent = Intent(this, CFBBroActivity::class.java)
        if (url != null) {
            intent.putExtra("inputUrl", url)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadAd()
    }


    private fun getHomeInter(): CFBa? {
        return CFBam.instance.get(CFBap.HOME)
    }

    private fun loadAd() {
        launch {
            if (getHomeInter() == null) {
                CFBam.instance.build(CFBap.HOME)
            }
        }
        launch {
            val spba = CFBam.instance.build(CFBap.NATIVE)
            binding.adDef.isVisible = false
            if (spba != null) {
                withContext(Dispatchers.Main) {
                    val adViewBind = binding.adView
                    adViewBind.adViewRoot.isVisible = true
                    adViewBind.adViewRoot.onGlobalLayout {
                        val adView = adViewBind.adView
                        spba.showNav {
                            if (this == null) {
                                adView.visibility = View.GONE
                            } else {
                                adViewBind.adAction.text = this.callToAction
                                adViewBind.adTitle.text = this.headline
                                adViewBind.adDes.text = this.body
                                adView.adChoicesView = adViewBind.adChoices
                                adView.callToActionView = adViewBind.adAction
                                adView.imageView = adViewBind.adImage
                                adView.mediaView = adViewBind.adMedia
                                adView.iconView = adViewBind.adIcon
                                adView.headlineView = adViewBind.adTitle
                                adView.bodyView = adViewBind.adDes
                                Glide.with(this@CFBMActivity).load(this.icon?.uri).into(adViewBind.adIcon)
                                adView.setNativeAd(this)
                                adView.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

}