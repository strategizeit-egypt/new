package com.simcoder.uber

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface MapsApiServices {

    @GET
    fun getPointsList(@Url url: String) :Single<JsonObject>
}