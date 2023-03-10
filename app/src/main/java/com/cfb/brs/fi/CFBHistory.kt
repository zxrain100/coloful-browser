package com.cfb.brs.fi

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = CFBHistory.Table.TABLE_NAME)
data class CFBHistory(
    /**
     * 页面url
     */
    @ColumnInfo(name = "cfb_url")
    @PrimaryKey
    val url: String,
    /**
     * 页面标题
     */
    @ColumnInfo(name = "cfb_title")
    val title: String,

    /**
     * 添加时间/浏览时间
     */
    @ColumnInfo(name = "cfb_time")
    val time: Long

) {
    object Table {
        const val TABLE_NAME = "cfb_history"
    }
}