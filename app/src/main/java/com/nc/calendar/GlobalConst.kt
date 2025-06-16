package com.nc.calendar

import java.util.Locale

object Constants {
    const val DAYS_IN_WEEK = 7
    const val FIRST_DAY_OF_MONTH = 1
    const val TIME_FORMAT_PATTERN = "%02d:00"
    const val NOMINATIVE_MONTH_FORMAT_PATTERN = "LLLL"
    const val YEAR_FORMAT_PATTERN = "YYYY г."
    const val DAY_FORMAT_PATTERN = "dd"
    const val WEATHER_TIME_FORMAT_PATTERN = "E, d MMM"
    const val DATE_RESPONSE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm"
    const val DEFAULT_TIME_FORMAT_PATTERN = "HH:mm"
    const val TOOL_BAR_FORMAT_PATTERN = "EEEE, dd.MM"
    const val TEMPERATURE_C_FORMAT_PATTERN = "%d°С"
    const val TEMPERATURE_FORMAT_PATTERN = "%d°"
    const val M_PER_SECOND_FORMAT_PATTERN = "%d м/с"
    const val PERCENT_FORMAT_PATTERN = "%d%%"
    const val PROTOCOL = "https:"
    val locale: Locale by lazy { Locale.getDefault() }
}
