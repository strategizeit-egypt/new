package com.simcoder.uber

import com.google.gson.annotations.SerializedName

data class ApiErrors (val message :String?,
                      @SerializedName("errors") val errors :HashMap<String ,MutableList< String>>?)