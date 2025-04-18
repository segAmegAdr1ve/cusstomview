package com.nc.calendar

import com.nc.calendar.Constants.DAY_FORMAT_PATTERN
import com.nc.calendar.Constants.NOMINATIVE_MONTH_FORMAT_PATTERN
import com.nc.calendar.Constants.YEAR_FORMAT_PATTERN
import com.nc.calendar.Constants.locale
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

fun Month.format() = DateTimeFormatter.ofPattern(NOMINATIVE_MONTH_FORMAT_PATTERN).format(this)
    .replaceFirstChar { it.titlecase() }

fun Month.formatShort() = this.getDisplayName(TextStyle.SHORT, locale)
    .replaceFirstChar { it.titlecase() }

fun LocalDate.formatYear(): String = DateTimeFormatter.ofPattern(YEAR_FORMAT_PATTERN).format(this)

fun LocalDate.formatDay(): String = DateTimeFormatter.ofPattern(DAY_FORMAT_PATTERN).format(this)

fun LocalDate.formatDayOfWeek(): String = dayOfWeek.getDisplayName(TextStyle.SHORT, locale)

fun LocalDate.toDay(today: LocalDate? = null, selectedDay: LocalDate? = null): Day {
    return Day(
        dayOfWeek = this.formatDayOfWeek(),
        dayOfMonth = this.formatDay(),
        isSelected = this == selectedDay,
        isToday = this == today,
        date = this
    )
}