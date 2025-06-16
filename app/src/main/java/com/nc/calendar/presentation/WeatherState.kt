package com.nc.calendar.presentation

import com.nc.calendar.domain.model.WeatherModel

sealed interface WeatherState {
    data object Loading : WeatherState
    class Loaded(val data: WeatherModel) : WeatherState
    class Error(val message: String) : WeatherState
}