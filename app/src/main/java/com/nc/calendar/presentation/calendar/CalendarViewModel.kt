package com.nc.calendar.presentation.calendar

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.calendar.Constants
import com.nc.calendar.R
import com.nc.calendar.data.helper.CalendarHelper
import com.nc.calendar.data.network.NoInternetException
import com.nc.calendar.domain.WeatherRepository
import com.nc.calendar.presentation.WeatherState
import com.nc.calendar.presentation.calendar.CalendarViewModel.Companion.API_BACK_DAYS_RESTRICTION
import com.nc.calendar.presentation.calendar.CalendarViewModel.Companion.API_FORWARD_DAYS_RESTRICTION
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: WeatherRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val calendarHelper = CalendarHelper()
    val today: LocalDate = LocalDate.now()

    private val _lastSelectedDay = MutableStateFlow(today)
    val lastSelectedDay = _lastSelectedDay.asStateFlow()

    private val _selectedDate = MutableStateFlow(today)
    var selectedDate = _selectedDate.asStateFlow()

    private val _currentMonth = MutableStateFlow(fetchCurrentMonthList())
    val currentMonth = _currentMonth.asStateFlow()

    private val _weatherState: MutableStateFlow<WeatherState> =
        MutableStateFlow(WeatherState.Loading)
    val weatherState = _weatherState.asStateFlow()

    private var lastRequest: Job? = null

    fun onSelectedDateChanged(newDate: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedDate.value = newDate
            _currentMonth.emit(
                calendarHelper.createListForMonth(newDate.month, newDate.year)
            )
        }
    }

    private fun getWeatherByDate(date: LocalDate) {
        lastRequest?.let { request ->
            if (request.isActive) request.cancel()
        }
        lastRequest = viewModelScope.launch {
            if (date.isInDateRange(today)) {
                _weatherState.value = WeatherState.Loading
                delay(DEBOUNCE_DELAY)
                runCatching {
                    repository.getWeatherByDate(date, today)
                }.onSuccess { data ->
                    _weatherState.value = WeatherState.Loaded(data)
                }.onFailure { e ->
                    when (e) {
                        is CancellationException -> throw e
                        is NoInternetException -> _weatherState.value =
                            WeatherState.Error(context.getString(R.string.no_internet))

                        else -> _weatherState.value =
                            WeatherState.Error(context.getString(R.string.unknown_error))
                    }
                }
            } else {
                _weatherState.value = WeatherState.Error(context.getString(R.string.no_data))
            }
        }
    }

    fun setLastSelectedDay(day: LocalDate) {
        if (_lastSelectedDay.value != day) {
            _lastSelectedDay.value = day
            getWeatherByDate(day)
        }
    }

    fun calculatePositionToScroll(): Int {
        currentMonth.value.indexOf(lastSelectedDay.value).let { index ->
            return if (index == NO_SELECTED_DAY) {
                LIST_START
            } else {
                index / Constants.DAYS_IN_WEEK
            }
        }
    }

    private fun fetchCurrentMonthList(): List<LocalDate> {
        return calendarHelper.createListOfDaysFromToday()
    }

    companion object {
        const val NO_SELECTED_DAY = -1
        const val LIST_START = 0
        const val DEBOUNCE_DELAY = 350L
        const val API_FORWARD_DAYS_RESTRICTION = 300L
        const val API_BACK_DAYS_RESTRICTION = 7L
    }
}

private fun LocalDate.isInDateRange(today: LocalDate): Boolean {
    val longForward = today.plusDays(API_FORWARD_DAYS_RESTRICTION)
    val back = today.minusDays(API_BACK_DAYS_RESTRICTION)
    return this.isAfter(back) && this.isBefore(longForward)
}
