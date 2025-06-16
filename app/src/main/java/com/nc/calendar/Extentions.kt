package com.nc.calendar

import com.nc.calendar.Constants.DATE_RESPONSE_FORMAT_PATTERN
import com.nc.calendar.Constants.DAY_FORMAT_PATTERN
import com.nc.calendar.Constants.DEFAULT_TIME_FORMAT_PATTERN
import com.nc.calendar.Constants.NOMINATIVE_MONTH_FORMAT_PATTERN
import com.nc.calendar.Constants.TEMPERATURE_C_FORMAT_PATTERN
import com.nc.calendar.Constants.TOOL_BAR_FORMAT_PATTERN
import com.nc.calendar.Constants.WEATHER_TIME_FORMAT_PATTERN
import com.nc.calendar.Constants.YEAR_FORMAT_PATTERN
import com.nc.calendar.Constants.locale
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

fun LocalDate.formatWeatherDate(): String =
    DateTimeFormatter.ofPattern(WEATHER_TIME_FORMAT_PATTERN).format(this)

fun LocalDate.formatDayOfWeek() =
    this.format(DateTimeFormatter.ofPattern(TOOL_BAR_FORMAT_PATTERN, locale))
        .replaceFirstChar { it.titlecase(locale) }

fun Int.format(pattern: String): String = String.format(locale, pattern, this)

fun Int.formatTemp(): String = String.format(locale, TEMPERATURE_C_FORMAT_PATTERN, this)

fun String.parseDateTime(): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern(DATE_RESPONSE_FORMAT_PATTERN))

fun LocalDateTime.formatTime(): String =
    this.format(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT_PATTERN))

fun Month.format() = DateTimeFormatter.ofPattern(NOMINATIVE_MONTH_FORMAT_PATTERN).format(this)
    .replaceFirstChar { it.titlecase() }

fun Month.formatShort() = this.getDisplayName(TextStyle.SHORT, locale)
    .replaceFirstChar { it.titlecase() }

fun LocalDate.formatYear(): String = DateTimeFormatter.ofPattern(YEAR_FORMAT_PATTERN).format(this)

fun LocalDate.formatDay(): String = DateTimeFormatter.ofPattern(DAY_FORMAT_PATTERN).format(this)