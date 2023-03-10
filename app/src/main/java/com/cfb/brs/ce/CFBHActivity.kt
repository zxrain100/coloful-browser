package com.cfb.brs.ce

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cfb.brs.BaseActivity
import com.cfb.brs.fz.CFBDBManager
import com.cfb.brs.fi.Info
import com.cfb.brs.cq.MarkHistoryAdapter
import com.cfb.brs.databinding.ActHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CFBHActivity : BaseActivity() {
    private lateinit var binding: ActHistoryBinding
    private val adapter: MarkHistoryAdapter = MarkHistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.statusBar.setStatusBar()

        binding.back.setOnClickListener { finish() }
        binding.hisRecycle.layoutManager = LinearLayoutManager(this)
        binding.hisRecycle.adapter = adapter

        adapter.setOnDeleteListener { _, info ->
            deleteData(info)
            loadData()
        }

        adapter.setOnItemClickListener { _, info ->
            val intent = Intent()
            intent.putExtra("inputUrl", info.url)
            setResult(RESULT_OK, intent)
            finish()
        }
        loadData()
    }

    private fun loadData() {
        launch {
            val list = mutableListOf<Info>()
            val data = CFBDBManager.instance.pageHistoryDao().getAll()
            data.forEach {
                list.add(Info(it.url, it.title, it.time))
            }

            list.sortBy { -it.time }

            withContext(Dispatchers.Main) {
                adapter.submitList(list)
            }
        }

    }

    private fun deleteData(item: Info) {
        val manager = CFBDBManager.instance
        val page = manager.pageHistoryDao().getByUrl(item.url)
        if (page != null) {
            manager.pageHistoryDao().deletePage(page)
        }
    }
}