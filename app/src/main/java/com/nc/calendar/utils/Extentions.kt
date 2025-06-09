package com.nc.calendar.utils

import com.nc.calendar.Constants.DATE_RESPONSE_FORMAT_PATTERN
import com.nc.calendar.Constants.DEFAULT_TIME_FORMAT_PATTERN
import com.nc.calendar.Constants.TEMPERATURE_C_FORMAT_PATTERN
import com.nc.calendar.Constants.TOOL_BAR_FORMAT_PATTERN
import com.nc.calendar.Constants.WEATHER_TIME_FORMAT_PATTERN
import com.nc.calendar.Constants.locale
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDate.formatWeatherDate(): String =
    DateTimeFormatter.ofPattern(WEATHER_TIME_FORMAT_PATTERN).format(this)

fun LocalDate.formatDayOfWeek() =
    this.format(DateTimeFormatter.ofPattern(TOOL_BAR_FORMAT_PATTERN, locale))
        .replaceFirstChar { it.titlecase(locale) }

fun Int.format(pattern: String): String = String.format(locale, pattern, this)

fun Int.formatTemp(): String = String.format(locale, TEMPERATURE_C_FORMAT_PATTERN, this)

fun String.parseDateTime() =
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern(DATE_RESPONSE_FORMAT_PATTERN))

fun LocalDateTime.formatTime() =
    this.format(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT_PATTERN))