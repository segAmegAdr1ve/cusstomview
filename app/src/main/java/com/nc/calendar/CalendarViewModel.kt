package com.nc.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.calendar.Constants.DAYS_IN_WEEK
import com.nc.calendar.helper.CalendarHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel : ViewModel() {
    private val calendarHelper = CalendarHelper()

    private val _lastSelectedDay = MutableStateFlow(LocalDate.now())
    val lastSelectedDay = _lastSelectedDay.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    var selectedDate = _selectedDate.asStateFlow()
    private val _currentMonth: MutableStateFlow<List<LocalDate>> =
        MutableStateFlow(fetchCurrentMonthList())
    val currentMonth = _currentMonth.asStateFlow()

    fun onSelectedDateChanged(newDate: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedDate.value = newDate
            _currentMonth.emit(
                calendarHelper.createListForMonth(newDate.month, newDate.year)
            )
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
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