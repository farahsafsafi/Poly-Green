package com.example.green


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {
    @GET("search")
    fun searchLocations(
        @Query("q") query: String,              // User's search query
        @Query("format") format: String = "json", // Response format
        @Query("addressdetails") addressDetails: Int = 1, // Include address details
        @Query("limit") limit: Int = 5           // Number of suggestions
    ): Call<List<LocationSuggestion>>
}
