package com.nc.calendar.data.model.hourly

import com.google.gson.annotations.SerializedName

data class Day(
    val avghumidity: Int,
    @SerializedName(value = "avgtemp_c")
    val avgtempC: Double,
    val avgtemp_f: Double,
    val avgvis_km: Double,
    val avgvis_miles: Int,
    val condition: Condition,
    val daily_chance_of_rain: Int,
    val daily_chance_of_snow: Int,
    val daily_will_it_rain: Int,
    val daily_will_it_snow: Int,
    @SerializedName(value = "maxtemp_c")
    val maxtempC: Double,
    val maxtemp_f: Double,
    @SerializedName(value = "maxwind_kph")
    val maxwindKph: Double,
    val maxwind_mph: Double,
    @SerializedName(value = "mintemp_c")
    val mintempC: Double,
    val mintemp_f: Double,
    val totalprecip_in: Double,
    val totalprecip_mm: Double,
    val totalsnow_cm: Int,
    val uv: Double
)