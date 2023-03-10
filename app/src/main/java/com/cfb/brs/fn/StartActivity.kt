package com.cfb.brs.fn

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import com.cfb.brs.BaseActivity
import com.cfb.brs.fz.CFBBroActivity
import com.cfb.brs.R
import com.cfb.brs.cq.CFBap
import com.cfb.brs.databinding.ActStartBinding
import com.cfb.brs.fi.CFBMActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class StartActivity : BaseActivity() {

    private var isBack = false
    private var anim: ValueAnimator? = null

    private lateinit var binding: ActStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBind()

        isBack = intent.getBooleanExtra("isB", false)
        start()
        anim()
    }


    private fun start() {
        launch {
            withTimeoutOrNull(16000) {
                CFBam.instance.builds(CFBap.LOADING, CFBap.HOME, CFBap.NATIVE)

            }
            withContext(Dispatchers.Main) {
                anim?.cancel()
                val progress = binding.indexBar.progress
                if (progress < 100) {
                    val animator = ValueAnimator.ofInt(progress, 100)
                    animator.duration = 500L
                    animator.addUpdateListener {
                        val v = it.animatedValue as Int
                        binding.indexBar.progress = v
                        binding.indexDesc.text = getString(R.string.index_desc, v.toString())
                        if (v >= 100) {
                            showAd()
                        }
                    }
                    animator.start()
                } else {
                    showAd()
                }
            }
        }
    }

    private fun initBind() {
        binding = ActStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun anim() {
        anim = ValueAnimator.ofInt(0, 90)
        anim?.duration = 9 * 1000L
        anim?.addUpdateListener {
            binding.indexBar.progress = it.animatedValue as Int
            binding.indexDesc.text = getString(R.string.index_desc, (it.animatedValue as Int).toString())
        }
        anim?.start()
    }

    private fun showAd() {
        val ad = CFBam.instance.get(CFBap.LOADING)
        if (ad != null && isFront) {
            ad.onClose {
                if (!isBack) {
                    startToMain()
                }
                finish()
            }
            ad.show(this)
        } else {
            if (!isBack) {
                startToMain()
            }
            finish()
        }
    }


    private fun startToMain() {
        if (AppConfig.instance.isM()) {
            startActivity(Intent(this, CFBMActivity::class.java))
        } else {
            startActivity(Intent(this, CFBBroActivity::class.java))
        }
    }

}