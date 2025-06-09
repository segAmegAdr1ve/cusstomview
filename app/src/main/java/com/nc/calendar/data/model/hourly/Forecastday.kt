package com.nc.calendar.data.model.hourly

import com.google.gson.annotations.SerializedName

data class Forecastday(
    @SerializedName(value = "date")
    val date: String,
    @SerializedName(value = "date_epoch")
    val dateEpoch: Long,
    @SerializedName(value = "day")
    val day: Day,
    @SerializedName(value = "hour")
    val hour: List<Hour>
)