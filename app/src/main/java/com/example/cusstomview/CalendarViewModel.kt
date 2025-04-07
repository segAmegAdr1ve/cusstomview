package com.example.cusstomview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cusstomview.helper.CalendarHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month

class CalendarViewModel : ViewModel() {
    val calendarHelper = CalendarHelper()
    //var selectedDate: LocalDate = LocalDate.now()
    private val _selectedDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    var selectedDate = _selectedDate.asStateFlow()

    private val _currentMonth: MutableStateFlow<List<LocalDate>> = MutableStateFlow(fetchCurrentMonthList())
    val currentMonth = _currentMonth.asStateFlow()

    /*private val _currentDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    val currentDate = _currentDate.asStateFlow()*/

    fun onSelectedDateChanged(newDate: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedDate.emit(newDate)
            _currentMonth.emit(
                calendarHelper.createListForMonth(newDate.month)
            )
        }
    }

    fun setSelectedDate(newDate: LocalDate) {
        viewModelScope.launch(Dispatchers.Default) {
            _selectedDate.emit(newDate)
        }
    }

    //удалить
    fun setSelectedMonth(month: Month) {
        _selectedDate.update { date ->
            date.withMonth(month.value)
        }
    }

    private fun fetchCurrentMonthList(): List<LocalDate> {
        return calendarHelper.createListOfDaysFromToday()
    }


}