package com.example.green

data class DistanceResponse(
    val routes: List<Route>
)

data class Route(
    val distance: Double // Distance in meters
)
