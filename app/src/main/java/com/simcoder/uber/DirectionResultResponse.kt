package com.simcoder.uber

import com.google.gson.annotations.SerializedName


class DirectionResults {
    @SerializedName("routes")
    val routes: List<Route>? = null
}

class Route {
    @SerializedName("overview_polyline")
    val overviewPolyLine: OverviewPolyLine? = null

    val legs: List<Legs>? = null
}

class Legs {
    val steps: List<Steps>? = null
}

class Steps {
    val start_location: LocationResponse? = null
    val end_location: LocationResponse? = null
    val polyline: OverviewPolyLine? = null
}

class OverviewPolyLine {

    @SerializedName("points")
    var points: String? = null
}

class LocationResponse {
    val lat: Double = 0.toDouble()
    val lng: Double = 0.toDouble()
}