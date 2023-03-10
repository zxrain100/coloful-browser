package com.cfb.brs.fz

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cfb.brs.CFB
import com.cfb.brs.ch.CFBMark
import com.cfb.brs.ce.CFBMarkDao
import com.cfb.brs.ch.CFBHistoryDao
import com.cfb.brs.fi.CFBHistory

@Database(
    entities = [
        CFBMark::class,
        CFBHistory::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CFBDBManager : RoomDatabase() {

    companion object {
        val instance: CFBDBManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(CFB.context, CFBDBManager::class.java, "cfb_data.db")
                // 允许主线程操作数据库
                .allowMainThreadQueries()
                // debug模式使用JournalMode.TRUNCATE，数据实时写入数据库，方便Android-Debug-Database工具查看
                // release模式使用JournalMode.AUTOMATIC（16及以下使用TRUNCATE，16以上使用WRITE_AHEAD_LOGGING）
                // WRITE_AHEAD_LOGGING（WAL模式）为了提高写入速度（但是读取速度会降低），数据不是实时的写入到数据库里。
                .setJournalMode(JournalMode.AUTOMATIC)
                .fallbackToDestructiveMigration()// 数据库升级迁移时如果发生错误，将会重新创建数据库，而不是发生崩溃
//            .fallbackToDestructiveMigrationFrom(int... startVersions) // 数据库从某个版本升级迁移时如果发生错误，将会重新创建数据库，而不是发生崩溃
//            .addCallback() // 监听数据库创建和打开的操作
                .build()
        }

    }

    abstract fun pageMarkDao(): CFBMarkDao
    abstract fun pageHistoryDao(): CFBHistoryDao

}