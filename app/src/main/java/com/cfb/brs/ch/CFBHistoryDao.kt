package com.cfb.brs.ch

import androidx.room.*
import com.cfb.brs.fi.CFBHistory

@Dao
abstract class CFBHistoryDao {

    @Query("SELECT * FROM ${CFBHistory.Table.TABLE_NAME}")
    abstract fun getAll(): MutableList<CFBHistory>

    @Query("SELECT * FROM ${CFBHistory.Table.TABLE_NAME} WHERE cfb_url = :url")
    abstract fun getByUrl(url: String): CFBHistory?


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addPage(page: CFBHistory)


    @Delete
    abstract fun deletePage(page: CFBHistory)

}