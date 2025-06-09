package com.nc.calendar.presentation.detailweather

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.calendar.R
import com.nc.calendar.domain.WeatherRepository
import com.nc.calendar.presentation.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DetailWeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _weatherState: MutableStateFlow<WeatherState> =
        MutableStateFlow(WeatherState.Loading)
    val weatherState = _weatherState.asStateFlow()
    private val today = LocalDate.now()

    fun getWeatherByDate(date: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWeatherByDate(date, today)
                .collect { result ->
                    result.fold(
                        onSuccess = { value -> _weatherState.value = WeatherState.Loaded(value) },
                        onFailure = { ex ->
                            _weatherState.value = WeatherState.Error(
                                ex.message ?: context.getString(R.string.unknown_error)
                            )
                        }
                    )
                }
        }
    }
}