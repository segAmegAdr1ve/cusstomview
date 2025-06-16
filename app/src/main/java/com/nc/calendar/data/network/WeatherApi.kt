package com.nc.calendar.data.network

import com.nc.calendar.data.model.HourlyWeatherModel
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast.json")
    suspend fun getTodayAndFutureWeatherByDate(
        @Query(DATE_PARAM) date: String,
        @Query(DAYS_PARAM) days: String = DAYS_PER_REQUEST,
        @Query(INTERVAL_PARAM) interval: String = DETAIL_WEATHER_INTERVAL,
    ): HourlyWeatherModel

    @GET("history.json")
    suspend fun getHistoryWeatherByDate(
        @Query(DATE_PARAM) date: String,
    ): HourlyWeatherModel

    @GET("future.json")
    suspend fun getFutureWeatherByDate(
        @Query(DATE_PARAM) date: String,
    ): HourlyWeatherModel

    companion object {
        const val DATE_PARAM = "dt"
        const val INTERVAL_PARAM = "tp"
        const val DAYS_PARAM = "days"
        const val DAYS_PER_REQUEST = "1"
        const val DETAIL_WEATHER_INTERVAL = "60"
    }
}