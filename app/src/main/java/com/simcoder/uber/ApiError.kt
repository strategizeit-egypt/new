package com.simcoder.uber

import com.google.gson.annotations.SerializedName

data class ApiError (val message :String?,
                     @SerializedName("errors") val error: String?)