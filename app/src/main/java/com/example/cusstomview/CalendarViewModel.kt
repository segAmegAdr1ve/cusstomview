package com.example.cusstomview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cusstomview.helper.CalendarHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel : ViewModel() {
    val calendarHelper = CalendarHelper()

    private val _selectedDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    var selectedDate = _selectedDate.asStateFlow()

    private val _currentMonth: MutableStateFlow<List<LocalDate>> =
        MutableStateFlow(fetchCurrentMonthList())
    val currentMonth = _currentMonth.asStateFlow()

    fun onSelectedDateChanged(newDate: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedDate.emit(newDate)
            _currentMonth.emit(
                calendarHelper.createListForMonth(newDate.month)
            )
        }
    }

    private fun fetchCurrentMonthList(): List<LocalDate> {
        return calendarHelper.createListOfDaysFromToday()
    }

}