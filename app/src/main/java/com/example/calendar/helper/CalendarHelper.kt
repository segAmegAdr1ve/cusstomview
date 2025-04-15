package com.example.calendar.helper

import com.example.calendar.Constants.DAYS_IN_WEEK
import com.example.calendar.Constants.FIRST_DAY_OF_MONTH
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

class CalendarHelper {
    private val today: LocalDate = LocalDate.now()
    val selectedDate: LocalDate = today

    fun createListOfDaysFromToday(): List<LocalDate> {
        val startDate = today.with(DayOfWeek.MONDAY).minusWeeks(DEFAULT_WEEKS_SUBTRACT)
        return createListOfDays(startDate, DEFAULT_WEEK_NUMBER)
    }

    fun createListForMonth(month: Month): List<LocalDate> {
        val firstDayOfSelectedMonth =
            today.withMonth(month.value).withDayOfMonth(FIRST_DAY_OF_MONTH)
        val startDate = if (firstDayOfSelectedMonth.dayOfWeek != DayOfWeek.MONDAY) {
            firstDayOfSelectedMonth.with(DayOfWeek.MONDAY)
        } else {
            firstDayOfSelectedMonth
        }

        val weekNumber = firstDayOfSelectedMonth.plusWeeks(DEFAULT_WEEK_NUMBER.toLong()).let {
            if (it.month == firstDayOfSelectedMonth.month && it.dayOfWeek != DayOfWeek.MONDAY) {
                DEFAULT_WEEK_NUMBER + EXTRA_WEEK_NUMBER
            } else {
                DEFAULT_WEEK_NUMBER
            }
        }
        return createListOfDays(startDate, weekNumber)
    }

    private fun createListOfDays(startDate: LocalDate, weekNumber: Int): List<LocalDate> {
        return buildList {
            repeat(weekNumber * DAYS_IN_WEEK) { dayNumber ->
                add(startDate.plusDays(dayNumber.toLong()))
            }
        }
    }

    companion object {
        private const val DEFAULT_WEEKS_SUBTRACT = 2L
        private const val DEFAULT_WEEK_NUMBER = 5
        private const val EXTRA_WEEK_NUMBER = 1
    }
}
