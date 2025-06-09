package com.nc.calendar.data.model

import com.google.gson.annotations.SerializedName

data class Condition(
    @SerializedName(value = "code")
    val code: Int,
    @SerializedName(value = "icon")
    val icon: String,
    @SerializedName(value = "text")
    val text: String
)