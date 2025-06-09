package com.nc.calendar.presentation.calendar

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.calendar.Constants
import com.nc.calendar.Constants.API_BACK_DAYS_RESTRICTION
import com.nc.calendar.Constants.API_FORWARD_DAYS_RESTRICTION
import com.nc.calendar.R
import com.nc.calendar.data.helper.CalendarHelper
import com.nc.calendar.domain.WeatherRepository
import com.nc.calendar.presentation.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: WeatherRepository,
    @ApplicationContext private val context: Context,
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
                .flatMapLatest { date ->
                    delay(Constants.DEBOUNCE_DELAY)
                    if (date.isInDateRange(today)) {
                        repository.getWeatherByDate(date, today)
                            .map { result ->
                                result.fold(
                                    onSuccess = { value -> WeatherState.Loaded(value) },
                                    onFailure = { ex ->
                                        WeatherState.Error(
                                            ex.message ?: context.getString(R.string.unknown_error)
                                        )
                                    }
                                )
                            }.onStart { emit(WeatherState.Loading) }
                    } else {
                        flowOf(WeatherState.Error(context.getString(R.string.no_data)))
                    }
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
    }
}

private fun LocalDate.isInDateRange(today: LocalDate): Boolean {
    val longForward = today.plusDays(API_FORWARD_DAYS_RESTRICTION)
    val back = today.minusDays(API_BACK_DAYS_RESTRICTION)
    return this.isAfter(back) && this.isBefore(longForward)
}
