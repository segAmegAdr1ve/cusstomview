package com.nc.calendar.data

import com.nc.calendar.Constants.DAYS_PER_REQUEST
import com.nc.calendar.Constants.DETAIL_WEATHER_INTERVAL
import com.nc.calendar.data.model.hourly.HourlyWeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast.json")
    suspend fun getTodayAndFutureWeatherByDate(
        @Query("dt") date: String,
        @Query("days") days: String = DAYS_PER_REQUEST,
        @Query("tp") interval: String = DETAIL_WEATHER_INTERVAL,
    ): Response<HourlyWeatherModel>

    @GET("history.json")
    suspend fun getHistoryWeatherByDate(
        @Query("dt") date: String,
    ): Response<HourlyWeatherModel>

    @GET("future.json")
    suspend fun getFutureWeatherByDate(
        @Query("dt") date: String,
    ): Response<HourlyWeatherModel>

}