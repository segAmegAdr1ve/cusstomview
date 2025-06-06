package com.nc.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.calendar.Constants.API_BACK_DAYS_RESTRICTION
import com.nc.calendar.Constants.API_FORWARD_DAYS_RESTRICTION
import com.nc.calendar.Constants.DAYS_IN_WEEK
import com.nc.calendar.Constants.DEBOUNCE_DELAY
import com.nc.calendar.Constants.NO_DATA
import com.nc.calendar.domain.WeatherRepository
import com.nc.calendar.domain.model.Result
import com.nc.calendar.domain.model.WeatherModel
import com.nc.calendar.data.helper.CalendarHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {
    init {
        observeDateChanges()
    }

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

    private fun observeDateChanges() {
        viewModelScope.launch {
            _lastSelectedDay
                .debounce(DEBOUNCE_DELAY)
                .distinctUntilChanged()
                .flatMapLatest { date ->
                    flow {
                        if (date.isInDateRange(today)) {
                            when (val response = repository.getWeatherByDate(date, today)) {
                                is Result.Success<*> -> emit(WeatherState.Loaded(response.data as WeatherModel))
                                is Result.Error -> emit(WeatherState.Error(response.message))
                            }
                        } else {
                            emit(WeatherState.Error(NO_DATA))
                        }
                    }
                        .onStart { emit(WeatherState.Loading) }
                }
                .flowOn(Dispatchers.IO)
                .collect { state ->
                    _weatherState.value = state
                }
        }
    }

    fun onSelectedDateChanged(newDate: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedDate.value = newDate
            _currentMonth.emit(
                calendarHelper.createListForMonth(newDate.month, newDate.year)
            )
        }
    }

    fun setLastSelectedDay(day: LocalDate) {
        _lastSelectedDay.value = day
    }

    fun calculatePositionToScroll(): Int {
        currentMonth.value.indexOf(lastSelectedDay.value).let { index ->
            return if (index == NO_SELECTED_DAY) {
                LIST_START
            } else {
                index / DAYS_IN_WEEK
            }
        }
    }

    private fun fetchCurrentMonthList(): List<LocalDate> {
        return calendarHelper.createListOfDaysFromToday()
    }

    companion object {
        const val NO_SELECTED_DAY = -1
        const val LIST_START = 0
    }
}

private fun LocalDate.isInDateRange(today: LocalDate): Boolean {
    val longForward = today.plusDays(API_FORWARD_DAYS_RESTRICTION)
    val back = today.minusDays(API_BACK_DAYS_RESTRICTION)
    return this.isAfter(back) && this.isBefore(longForward)
}