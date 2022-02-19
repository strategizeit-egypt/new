package com.simcoder.uber.agora.message.data.model

data class MessageModel(
    val action :Int,
    val data :String
)

data class BusLocationParam(
    val lat :Double,
    val lng :Double
)