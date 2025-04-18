package com.nc.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nc.calendar.Constants.DAYS_IN_WEEK
import com.nc.calendar.helper.CalendarHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel : ViewModel() {
    private val calendarHelper = CalendarHelper()
    val today: LocalDate = LocalDate.now()

    private val _lastSelectedDay = MutableStateFlow(today)
    val lastSelectedDay = _lastSelectedDay.asStateFlow()

    private val _selectedDate = MutableStateFlow(today)
    var selectedDate = _selectedDate.asStateFlow()

    private val _currentMonth = MutableStateFlow(fetchCurrentMonthList())
    val currentMonth = _currentMonth.asStateFlow()
    val monthList = currentMonth
        .combine(lastSelectedDay) { list: List<LocalDate>, selectedDay: LocalDate ->
            list.map { date ->
                date.toDay(today = today, selectedDay = selectedDay)
            }.chunked(DAYS_IN_WEEK)
        }
        .flowOn(Dispatchers.IO)

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet = _showBottomSheet.asStateFlow()

    val showTopPaneYear = selectedDate
        .map { date -> date.year != today.year }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )
    //
    private val _bottomSheetSelectedDate = MutableStateFlow(selectedDate.value)
    val bottomSheetSelectedDate = _bottomSheetSelectedDate.asStateFlow()

    val selectedMonthPosition: StateFlow<Int> = _bottomSheetSelectedDate
        .map { it.month.value - 1 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0
        )

    fun selectMonthWithPosition(position: Int) {
        _bottomSheetSelectedDate.update { currentDate ->
            currentDate.withMonth(position + 1)
        }
    }

    fun selectNextMonth() {
        viewModelScope.launch(Dispatchers.IO) {
            _bottomSheetSelectedDate.update {
                it.plusMonths(1)
            }
        }
    }

    fun selectPreviousMonth() {
        viewModelScope.launch(Dispatchers.IO) {
            _bottomSheetSelectedDate.update {
                it.minusMonths(1)
            }
        }
    }

    fun resetBottomSheetSelectedDate() {
        _bottomSheetSelectedDate.value = selectedDate.value
    }
    //
    fun setShowBottomSheet(value: Boolean) {
        _showBottomSheet.value = value
    }

    fun onSelectedDateChanged(newDate: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedDate.value = newDate
            _currentMonth.emit(
                calendarHelper.createListForMonth(newDate.month, newDate.year)
            )
        }
    }

    fun applySelectedDate() {
        onSelectedDateChanged(bottomSheetSelectedDate.value)
    }

    fun goToToday() {
        _lastSelectedDay.value = today
        onSelectedDateChanged(today)
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

data class Day(
    val dayOfWeek: String,
    val dayOfMonth: String,
    val isSelected: Boolean,
    val isToday: Boolean,
    val date: LocalDate
)