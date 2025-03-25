package com.example.cusstomview.helper

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

class CalendarHelper {
    private val today: LocalDate = LocalDate.now()
    var selectedDate: LocalDate = today

    fun createListOfDaysFromToday(): List<LocalDate> {
        val startDate = today.with(DayOfWeek.MONDAY).minusWeeks(2)
        return createListOfDays(startDate, 5)
    }

    fun createListForMonth(month: Month): List<LocalDate> {
        val firstDayOfSelectedMonth = today.withMonth(month.value).withDayOfMonth(1)
        val startDate = if (firstDayOfSelectedMonth.dayOfWeek != DayOfWeek.MONDAY) {
            firstDayOfSelectedMonth.with(DayOfWeek.MONDAY)
        } else firstDayOfSelectedMonth

        val resultList = createListOfDays(startDate, 5).toMutableList()
        val lastItem = resultList.last()
        if (lastItem.dayOfMonth < lastItem.month.maxLength() && lastItem.month == firstDayOfSelectedMonth.month) {
            resultList += createListOfDays(lastItem, 1)
        }
        return resultList
    }

    private fun createListOfDays(startDate: LocalDate, weekNumber: Int): List<LocalDate> {
        return buildList<LocalDate> {
            repeat(weekNumber * 7) { dayNumber ->
                add(startDate.plusDays(dayNumber.toLong()))
            }
        }
    }

}
