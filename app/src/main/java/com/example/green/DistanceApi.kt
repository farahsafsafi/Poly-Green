package com.example.green

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DistanceApi {
    @GET("v2/directions/driving-car")
    fun calculateDistance(
        @Query("api_key") apiKey: String,
        @Query("start") startCoordinates: String,  // Format: "longitude,latitude"
        @Query("end") endCoordinates: String       // Format: "longitude,latitude"
    ): Call<DistanceResponse>
}
