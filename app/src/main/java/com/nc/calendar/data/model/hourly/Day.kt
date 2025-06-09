package com.nc.calendar.data.model.hourly

import com.google.gson.annotations.SerializedName

data class Day(
    @SerializedName(value = "avghumidity")
    val avgHumidity: Int,
    @SerializedName(value = "avgtemp_c")
    val avgTempCelsius: Double,
    @SerializedName(value = "condition")
    val condition: Condition,
    @SerializedName(value = "maxtemp_c")
    val maxTempCelsius: Double,
    @SerializedName(value = "maxwind_kph")
    val maxWindKph: Double,
    @SerializedName(value = "mintemp_c")
    val minTempCelsius: Double,
)