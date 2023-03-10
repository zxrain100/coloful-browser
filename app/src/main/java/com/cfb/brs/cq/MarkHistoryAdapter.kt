package com.cfb.brs.cq

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.cfb.brs.CFBUtils
import com.cfb.brs.R
import com.cfb.brs.databinding.ListItemLayoutBinding
import com.cfb.brs.fi.Info

class MarkHistoryAdapter : ListAdapter<Info, MarkHistoryAdapter.ItemHolder>(InfoDiffCallback) {

    class ItemHolder(val binding: ListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = getItem(position)

        val bind = holder.binding
        val iconUrl = CFBUtils.getIconUrlByHost(item.url)
        Glide.with(bind.root.context)
            .load(iconUrl)
            .error(R.drawable.sh_item_def)
            .placeholder(R.drawable.sh_item_def)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(bind.img)
        bind.title.text = item.title
        bind.host.text = Uri.parse(item.url).host

        bind.pageDelete.setOnClickListener {
            listener?.invoke(position, item)
        }
        bind.root.setOnClickListener {
            click?.invoke(position, item)
        }
    }

    private var listener: ((Int, Info) -> Unit)? = null

    fun setOnDeleteListener(l: (Int, Info) -> Unit) {
        listener = l
    }

    private var click: ((Int, Info) -> Unit)? = null
    fun setOnItemClickListener(c: (Int, Info) -> Unit) {
        this.click = c
    }


    object InfoDiffCallback : DiffUtil.ItemCallback<Info>() {
        override fun areItemsTheSame(oldItem: Info, newItem: Info): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Info, newItem: Info): Boolean {
            return oldItem.url == newItem.url
        }
    }

}

