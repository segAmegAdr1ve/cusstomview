package com.nc.calendar.data.model.hourly

import com.google.gson.annotations.SerializedName

data class Forecastday(
    val astro: Astro,
    val date: String,
    @SerializedName(value = "date_epoch")
    val dateEpoch: Long,
    val day: Day,
    val hour: List<Hour>
)