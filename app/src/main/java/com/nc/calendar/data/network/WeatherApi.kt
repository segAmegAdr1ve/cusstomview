package com.nc.calendar.data.network

import com.nc.calendar.Constants.DATE_PARAM
import com.nc.calendar.Constants.DAYS_PARAM
import com.nc.calendar.Constants.DAYS_PER_REQUEST
import com.nc.calendar.Constants.DETAIL_WEATHER_INTERVAL
import com.nc.calendar.Constants.INTERVAL_PARAM
import com.nc.calendar.data.model.hourly.HourlyWeatherModel
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

}