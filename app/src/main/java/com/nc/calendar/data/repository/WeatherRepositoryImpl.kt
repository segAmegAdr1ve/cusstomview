package com.nc.calendar.data.repository

import com.nc.calendar.data.model.toWeatherModel
import com.nc.calendar.data.network.WeatherApi
import com.nc.calendar.domain.WeatherRepository
import com.nc.calendar.domain.model.WeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi
) : WeatherRepository {

    override suspend fun getWeatherByDate(
        date: LocalDate,
        today: LocalDate
    ): WeatherModel = withContext(Dispatchers.IO) {
        val formattedDate = date.format(ISO_LOCAL_DATE)
        when {
            date.isBefore(today) -> {
                weatherApi.getHistoryWeatherByDate(formattedDate)
            }

            date.isBefore(today.plusWeeks(API_FORWARD_WEEKS_DETAIL_RESTRICTION)) -> {
                weatherApi.getTodayAndFutureWeatherByDate(formattedDate)
            }

            else -> {
                weatherApi.getFutureWeatherByDate(formattedDate)
            }
        }.toWeatherModel()
    }

    companion object {
        const val API_FORWARD_WEEKS_DETAIL_RESTRICTION = 2L
    }
}