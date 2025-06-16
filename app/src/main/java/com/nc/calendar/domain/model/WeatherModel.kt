package com.nc.calendar.domain.model

import java.time.LocalDate

data class WeatherModel(
    val minTemp: Int,
    val midTemp: Int,
    val maxTemp: Int,
    val humidity: Int,
    val windSpeed: Int,
    val iconUrl: String,
    val date: LocalDate,
    val hourly: List<Hour>
)

data class Hour(
    val time: String,
    val iconUrl: String,
    val temp: Int
)