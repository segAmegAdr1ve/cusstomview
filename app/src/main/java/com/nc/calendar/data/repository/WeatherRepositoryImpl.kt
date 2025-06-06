package com.nc.calendar.data.repository

import com.nc.calendar.Constants.API_FORWARD_WEEKS_DETAIL_RESTRICTION
import com.nc.calendar.Constants.NO_INTERNET
import com.nc.calendar.Constants.UNKNOWN_ERROR
import com.nc.calendar.data.WeatherApi
import com.nc.calendar.data.model.hourly.toWeatherModel
import com.nc.calendar.domain.WeatherRepository
import com.nc.calendar.domain.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi
) : WeatherRepository {

    override suspend fun getWeatherByDate(date: LocalDate, today: LocalDate): Result =
        withContext(Dispatchers.IO) {
            val formattedDate = date.format(ISO_LOCAL_DATE)
            try {
                val response = if (date.isBefore(today)) {
                    weatherApi.getHistoryWeatherByDate(formattedDate)
                } else if (date.isBefore(today.plusWeeks(API_FORWARD_WEEKS_DETAIL_RESTRICTION))) {
                    weatherApi.getTodayAndFutureWeatherByDate(formattedDate)
                } else {
                    weatherApi.getFutureWeatherByDate(formattedDate)
                }
                return@withContext if (response.isSuccessful) {
                    Result.Success(response.body()?.toWeatherModel())
                } else {
                    Result.Error(UNKNOWN_ERROR)
                }
            } catch (e: UnknownHostException) {
                return@withContext Result.Error(NO_INTERNET)
            } catch (e: Exception) {
                return@withContext Result.Error(e.message ?: UNKNOWN_ERROR)
            }
        }
}