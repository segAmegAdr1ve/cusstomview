package com.nc.calendar.data.model

import com.google.gson.annotations.SerializedName

data class Forecast(
    @SerializedName(value = "forecastday")
    val forecastDay: List<Forecastday>
)