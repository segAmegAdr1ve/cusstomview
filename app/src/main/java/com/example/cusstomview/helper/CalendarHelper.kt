package com.example.cusstomview.helper

import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

typealias Week = List<Day>
class CalendarHelper {
    private val today: LocalDate = LocalDate.now()
    var selectedMonth: Month = today.month

    fun makeMonth(): List<Week> {
        val resultList = mutableListOf<Week>()
        var firstDayOfMonth = today.withMonth(selectedMonth.value).withDayOfMonth(1)

        while (firstDayOfMonth.dayOfWeek != java.time.DayOfWeek.MONDAY) {
            firstDayOfMonth = firstDayOfMonth.minusDays(1)
        }
        do {
            resultList.add(
                makeWeek(since = firstDayOfMonth, today = today)
            )
            firstDayOfMonth = firstDayOfMonth.plusWeeks(1)
        } while (firstDayOfMonth.month == selectedMonth)

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
