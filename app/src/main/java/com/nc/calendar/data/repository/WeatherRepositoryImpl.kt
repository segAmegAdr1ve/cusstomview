package com.nc.calendar.data.repository

import android.content.Context
import com.nc.calendar.Constants.API_FORWARD_WEEKS_DETAIL_RESTRICTION
import com.nc.calendar.R
import com.nc.calendar.data.model.toWeatherModel
import com.nc.calendar.data.network.NoInternetException
import com.nc.calendar.data.network.WeatherApi
import com.nc.calendar.domain.WeatherRepository
import com.nc.calendar.domain.model.WeatherModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    @ApplicationContext private val context: Context
) : WeatherRepository {

    override suspend fun getWeatherByDate(
        date: LocalDate,
        today: LocalDate
    ): Flow<Result<WeatherModel>> = flow {
        val formattedDate = date.format(ISO_LOCAL_DATE)
        val response = when {
            date.isBefore(today) -> {
                weatherApi.getHistoryWeatherByDate(formattedDate)
            }

            date.isBefore(today.plusWeeks(API_FORWARD_WEEKS_DETAIL_RESTRICTION)) -> {
                weatherApi.getTodayAndFutureWeatherByDate(formattedDate)
            }

            else -> {
                weatherApi.getFutureWeatherByDate(formattedDate)
            }
        }
        emit(Result.success(response.toWeatherModel()))
    }.catch { e ->
        when (e) {
            is NoInternetException -> emit(Result.failure(Exception(context.getString(R.string.no_internet))))
            is Exception -> emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
        }
    }
}