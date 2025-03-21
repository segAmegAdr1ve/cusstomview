package com.example.cusstomview.helper

import android.util.Log
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjuster
import java.util.Locale

typealias Week = List<Day>

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
            firstDayOfSelectedMonth.with(DayOfWeek.MONDAY)//minusWeeks(1).with(DayOfWeek.MONDAY)
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

    fun makeMonth(): List<Week> {
        val resultList = mutableListOf<Week>()
        var firstDayOfMonth = today.withMonth(selectedDate.month.value).withDayOfMonth(1)

        while (firstDayOfMonth.dayOfWeek != java.time.DayOfWeek.MONDAY) {
            firstDayOfMonth = firstDayOfMonth.minusDays(1)
        }
        do {
            resultList.add(
                makeWeek(since = firstDayOfMonth, today = today)
            )
            firstDayOfMonth = firstDayOfMonth.plusWeeks(1)
        } while (firstDayOfMonth.month == selectedDate.month)

        return resultList
    }

    private fun makeWeek(since: LocalDate, today: LocalDate): Week {
        val resultList = mutableListOf<Day>()
        val locale = Locale("ru")

        repeat(7) { extraDays: Int ->
            val date = if (extraDays != 0) since.plusDays(extraDays.toLong()) else since
            resultList.add(
                Day(
                    dayNumber = date.dayOfMonth.toString(),
                    month = date.month,
                    weekName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
                    numberOfWeek = date.dayOfWeek,
                    isCurrentDay = date.dayOfMonth == today.dayOfMonth && date.month == today.month, // если будет баг с двумя метками текущий день - добавить проверку месяца
                )
            )
        }
        return resultList
    }
}

data class Day(
    val dayNumber: String,
    val month: Month,
    val weekName: String,
    val numberOfWeek: java.time.DayOfWeek,
    val isCurrentDay: Boolean,
)
