package com.nc.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.calendar.helper.CalendarHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
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
    init {
        GlobalScope.launch {
            repeat(100) {
                Log.d("TAG", "VM: ${_selectedDate.value}")
                Log.d("TAG", "VM List: ${currentMonth.value}")
                delay(1000)
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

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setLastSelectedDay(day: LocalDate) {
        _lastSelectedDay.value = day
    }

    fun calculatePositionToScroll(): Int {
        currentMonth.value.indexOf(lastSelectedDay.value).let { index ->
            Log.d("calc", "${(index - 1) / 7}")
            if (index == -1) return 0
            return (index - 1) / 7
        }
    }

    private fun fetchCurrentMonthList(): List<LocalDate> {
        return calendarHelper.createListOfDaysFromToday()
    }

}