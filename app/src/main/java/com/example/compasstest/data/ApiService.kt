package com.example.compasstest.data

import retrofit2.http.GET

interface ApiService {
    @GET("about/")
    suspend fun fetchContent(): String
}
