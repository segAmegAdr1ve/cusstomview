package com.nc.calendar

import com.nc.calendar.domain.model.WeatherModel

sealed interface WeatherState {
    object Loading : WeatherState
    data class Loaded(val data: WeatherModel) : WeatherState
    data class Error(val message: String) : WeatherState
}