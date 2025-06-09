package com.nc.calendar.data.model

import com.google.gson.annotations.SerializedName

data class Hour(
    @SerializedName(value = "condition")
    val condition: Condition,
    @SerializedName(value = "temp_c")
    val tempCelsius: Double,
    @SerializedName(value = "time")
    val time: String,
)