package com.example.cusstomview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cusstomview.helper.CalendarHelper
import com.example.cusstomview.helper.Week
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Month

class CalendarViewModel : ViewModel() {
    val calendarHelper = CalendarHelper()
    var scrolled = false

    private val _scrollEvent = MutableSharedFlow<Int>(replay = 1)
    val scrollEvent = _scrollEvent.asSharedFlow()

    val _currentMonth: MutableStateFlow<List<Week>> = MutableStateFlow(fetchCurrentMonthList())
    val currentMonth = _currentMonth.asStateFlow().onEach {
        it.forEachIndexed { index: Int, week ->
            week.forEach { day ->
                if (day.isCurrentDay) {
                    _scrollEvent.emit(index)
                }
            }
        }
    }

    fun onSelectedMonthChanged(month: Month) {
        calendarHelper.selectedMonth = month
        viewModelScope.launch {
            _currentMonth.update {
                calendarHelper.makeMonth()
            }
        }
    }

    /*private fun emitScrollEvent() {
        viewModelScope.launch(Dispatchers.Default) {
            _currentMonth.map { month ->
                month.forEachIndexed { index: Int, days ->
                    days.forEach {
                        if (it.isCurrentDay) {
                            Log.d("tag", "Index in emitEvent: $index")
                            _scrollEvent.emit(index)
                        }//return@map index
                    }
                }
            }
        }
    }*/

    private fun fetchCurrentMonthList(): List<Week> {
        return calendarHelper.makeMonth()
    }

}