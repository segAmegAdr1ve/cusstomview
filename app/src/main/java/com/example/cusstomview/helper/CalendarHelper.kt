package com.example.cusstomview.helper

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

private const val DEFAULT_WEEK_NUMBER = 5
private const val EXTRA_WEEK_NUMBER = 1
const val DAYS_IN_WEEK_NUMBER = 7
class CalendarHelper {
    private val today: LocalDate = LocalDate.now()
    var selectedDate: LocalDate = today

    fun createListOfDaysFromToday(): List<LocalDate> {
        val startDate = today.with(DayOfWeek.MONDAY).minusWeeks(2)
        return createListOfDays(startDate, DEFAULT_WEEK_NUMBER)
    }

    fun createListForMonth(month: Month): List<LocalDate> {
        val firstDayOfSelectedMonth = today.withMonth(month.value).withDayOfMonth(1)
        val startDate = if (firstDayOfSelectedMonth.dayOfWeek != DayOfWeek.MONDAY) {
            firstDayOfSelectedMonth.with(DayOfWeek.MONDAY)
        } else firstDayOfSelectedMonth

        val resultList = createListOfDays(startDate, DEFAULT_WEEK_NUMBER).toMutableList()
        val lastItem = resultList.last()
        if (lastItem.dayOfMonth < lastItem.month.maxLength() && lastItem.month == firstDayOfSelectedMonth.month) {
            resultList += createListOfDays(lastItem, EXTRA_WEEK_NUMBER)
        }
        return resultList
    }

    private fun createListOfDays(startDate: LocalDate, weekNumber: Int): List<LocalDate> {
        return buildList<LocalDate> {
            repeat(weekNumber * DAYS_IN_WEEK_NUMBER) { dayNumber ->
                add(startDate.plusDays(dayNumber.toLong()))
            }
        }
    }

}
