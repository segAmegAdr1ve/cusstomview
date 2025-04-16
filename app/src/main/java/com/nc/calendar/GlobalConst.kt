package com.nc.calendar

import java.util.Locale

object Constants {
    const val DAYS_IN_WEEK = 7
    const val FIRST_DAY_OF_MONTH = 1
    const val TIME_FORMAT_PATTERN = "%02d:00"
    const val NOMINATIVE_MONTH_FORMAT_PATTERN = "LLLL"
    val locale: Locale by lazy { Locale.getDefault() }
}
