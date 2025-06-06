package com.nc.calendar.presentation.detailweather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.calendar.WeatherState
import com.nc.calendar.domain.model.Result
import com.nc.calendar.domain.model.WeatherModel
import com.nc.calendar.domain.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DetailWeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val today: LocalDate
) : ViewModel() {
    private val _weatherState: MutableStateFlow<WeatherState> =
        MutableStateFlow(WeatherState.Loading)
    val weatherState = _weatherState.asStateFlow()

    fun getWeatherByDate(date: LocalDate) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            when (val result = repository.getWeatherByDate(date, today)) {
                is Result.Success<*> -> _weatherState.value =
                    WeatherState.Loaded(result.data as WeatherModel)

                is Result.Error -> _weatherState.value =
                    WeatherState.Error(message = result.message)
            }
        }
    }
}