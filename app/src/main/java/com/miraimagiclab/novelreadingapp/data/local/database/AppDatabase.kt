package com.miraimagiclab.novelreadingapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.miraimagiclab.novelreadingapp.data.local.dao.NovelDao
import com.miraimagiclab.novelreadingapp.data.local.entity.NovelEntity

@Database(
    entities = [NovelEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun novelDao(): NovelDao
}
