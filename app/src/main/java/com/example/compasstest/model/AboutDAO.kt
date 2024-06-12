package com.example.compasstest.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AboutDataDao {
    @Query("SELECT * FROM about_data WHERE id = :id")
    fun getAboutData(id: Int): Flow<AboutData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAboutData(aboutData: AboutData)
}
