package com.cfb.brs.cq

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cfb.brs.BaseActivity
import com.cfb.brs.fz.CFBDBManager
import com.cfb.brs.fi.Info
import com.cfb.brs.databinding.ActMarkBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CFBMarkActivity : BaseActivity() {
    private lateinit var binding: ActMarkBinding

    private val adapter: MarkHistoryAdapter = MarkHistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActMarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.statusBar.setStatusBar()

        binding.markClose.setOnClickListener { finish() }

        binding.markRecycler.layoutManager = LinearLayoutManager(this)
        binding.markRecycler.adapter = adapter

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
            val manager = CFBDBManager.instance
            val list = mutableListOf<Info>()
            val data = manager.pageMarkDao().getAll()
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

        val page = manager.pageMarkDao().getByUrl(item.url)
        if (page != null) {
            manager.pageMarkDao().deletePage(page)
        }
    }
}