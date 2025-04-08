package com.example.cusstomview

import java.util.Locale

object Constants {
    const val DAYS_IN_WEEK = 7
    const val FIRST_DAY_OF_MONTH = 1
    const val TIME_FORMAT_PATTERN = "%02d:00"
    fun getLocale(): Locale = Locale.getDefault()
}
