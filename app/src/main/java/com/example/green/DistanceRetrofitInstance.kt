package com.example.green

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DistanceRetrofitInstance {
    private const val BASE_URL = "https://api.openrouteservice.org/"

    val api: DistanceApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DistanceApi::class.java)
    }
}
