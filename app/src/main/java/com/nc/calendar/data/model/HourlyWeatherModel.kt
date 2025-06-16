package com.nc.calendar.data.model

import com.google.gson.annotations.SerializedName
import com.nc.calendar.domain.model.Hour
import com.nc.calendar.domain.model.WeatherModel
import com.nc.calendar.formatTime
import com.nc.calendar.parseDateTime
import java.time.Instant
import java.time.ZoneId

data class HourlyWeatherModel(
    @SerializedName(value = "forecast")
    val forecast: Forecast,
)

fun HourlyWeatherModel.toWeatherModel(): WeatherModel {
    val forecastDay = forecast.forecastDay.first()
    val hourly = forecastDay.hour.map { hour ->
        Hour(
            time = hour.time.parseDateTime().formatTime(),
            iconUrl = hour.condition.icon,
            temp = hour.tempCelsius.toInt()
        )
    }.sortedBy { it.time }

    return WeatherModel(
        minTemp = forecastDay.day.minTempCelsius.toInt(),
        midTemp = forecastDay.day.avgTempCelsius.toInt(),
        maxTemp = forecastDay.day.maxTempCelsius.toInt(),
        humidity = forecastDay.day.avgHumidity,
        windSpeed = forecastDay.day.maxWindKph.toInt(),
        iconUrl = forecastDay.day.condition.icon,
        date = Instant.ofEpochSecond(forecastDay.dateEpoch)
            .atZone(ZoneId.systemDefault())
            .toLocalDate(),
        hourly = hourly
    )
}