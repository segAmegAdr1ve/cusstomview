package com.nc.calendar

import java.time.LocalDate
import java.util.Locale

object Constants {
    const val DAYS_IN_WEEK = 7
    const val FIRST_DAY_OF_MONTH = 1
    const val TIME_FORMAT_PATTERN = "%02d:00"
    const val NOMINATIVE_MONTH_FORMAT_PATTERN = "LLLL"
    const val YEAR_FORMAT_PATTERN = "YYYY г."
    val locale: Locale by lazy { Locale.getDefault() }
    val today: LocalDate by lazy { LocalDate.now() }
}
