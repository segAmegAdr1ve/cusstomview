package com.example.cusstomview

import java.time.Month

data class Day(
    val dayNumber: String,
    val month: Month,
    val weekName: String,
    val numberOfWeek: java.time.DayOfWeek,
    val isCurrentDay: Boolean,
)
