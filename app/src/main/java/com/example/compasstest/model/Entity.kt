package com.example.compasstest.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "about_data")
data class AboutData(
    @PrimaryKey val id: Int,
    val content: String
)
