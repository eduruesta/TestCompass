package com.example.compasstest.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.compasstest.model.AboutData
import com.example.compasstest.model.AboutDataDao

@Database(entities = [AboutData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun aboutDataDao(): AboutDataDao
}
