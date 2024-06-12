package com.example.compasstest.di

import androidx.room.Room
import com.example.compasstest.MainViewModel
import com.example.compasstest.data.ApiService
import com.example.compasstest.data.AppDatabase
import com.example.compasstest.data.RetrofitClient
import org.koin.dsl.module


val appModule = module {
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "app_database")
            .build()
    }
    single { get<AppDatabase>().aboutDataDao() }
    single {
        val retrofit = RetrofitClient.create()
        retrofit.create(ApiService::class.java)
    }
    single { MainViewModel(get(), get()) }
}
