package com.nc.calendar.domain

import com.nc.calendar.domain.model.Result
import java.time.LocalDate

interface WeatherRepository {
    suspend fun getWeatherByDate(date: LocalDate, today: LocalDate): Result
}