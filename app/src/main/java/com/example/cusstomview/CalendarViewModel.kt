package com.example.cusstomview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cusstomview.helper.CalendarHelper
import com.example.cusstomview.helper.Week
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month

class CalendarViewModel : ViewModel() {
    val calendarHelper = CalendarHelper()

    private val _currentMonth: MutableStateFlow<List<LocalDate>> = MutableStateFlow(fetchCurrentMonthList())
    val currentMonth = _currentMonth.asStateFlow()

    fun onSelectedMonthChanged(selectedMonth: Month) {
        //calendarHelper.selectedDate = calendarHelper.selectedDate.withMonth(selectedMonth.value)
        viewModelScope.launch(Dispatchers.Default) {
            _currentMonth.update {
                calendarHelper.createListForMonth(month = selectedMonth)
            }
        }
    }

    private fun fetchCurrentMonthList(): List<LocalDate> {
        return calendarHelper.createListOfDaysFromToday()
    }

    /*private fun updateSelectedMonthList(selectedMonth: Month): List<LocalDate> {
        viewModelScope.launch(Dispatchers.Default) {
            _currentMonth.update {
                calendarHelper.createListForMonth(month = selectedMonth)
            }
        }
    }*/

}