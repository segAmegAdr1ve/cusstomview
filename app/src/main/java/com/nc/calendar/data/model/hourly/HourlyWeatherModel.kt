package com.nc.calendar.data.model.hourly

import com.nc.calendar.domain.model.Hour
import com.nc.calendar.domain.model.WeatherModel
import com.nc.calendar.utils.formatTime
import com.nc.calendar.utils.parseDateTime
import java.time.Instant
import java.time.ZoneId

data class HourlyWeatherModel(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)

fun HourlyWeatherModel.toWeatherModel(): WeatherModel {
    val forecastDay = forecast.forecastday.first()
    val hourly = forecastDay.hour.map { hour ->
        Hour(
            time = hour.time.parseDateTime().formatTime(),
            iconUrl = hour.condition.icon,
            temp = hour.tempC.toInt()
        )
    }.sortedBy { it.time }

    return WeatherModel(
        minTemp = forecastDay.day.mintempC.toInt(),
        midTemp = forecastDay.day.avgtempC.toInt(),
        maxTemp = forecastDay.day.maxtempC.toInt(),
        humidity = forecastDay.day.avghumidity,
        windSpeed = forecastDay.day.maxwindKph.toInt(),
        iconUrl = forecastDay.day.condition.icon,
        date = Instant.ofEpochSecond(forecastDay.dateEpoch)
            .atZone(ZoneId.systemDefault())
            .toLocalDate(),
        hourly = hourly
    )
}