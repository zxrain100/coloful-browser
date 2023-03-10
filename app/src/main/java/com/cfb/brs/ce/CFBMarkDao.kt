package com.cfb.brs.ce

import androidx.room.*
import com.cfb.brs.ch.CFBMark

@Dao
abstract class CFBMarkDao {

    @Query("SELECT * FROM ${CFBMark.Table.TABLE_NAME}")
    abstract fun getAll(): MutableList<CFBMark>

    @Query("SELECT * FROM ${CFBMark.Table.TABLE_NAME} WHERE cfb_url = :url")
    abstract fun getByUrl(url: String): CFBMark?


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addPage(page: CFBMark)

    @Delete
    abstract fun deletePage(page: CFBMark)

}