package com.example.cusstomview.helper

import android.util.Log
import com.example.cusstomview.DayOfWeek
import java.time.Clock
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.time.temporal.TemporalField
import java.util.Calendar
import java.util.Date

class CalendarHelper(
    @Deprecated("use LocalDate.now()")
    private val currentDate: Date
) {

    private val temporalCalendar = Calendar.getInstance()
    private val currentDateCalendar = Calendar.getInstance().apply { time = currentDate }

    fun getMonthList(): List<List<DayOfWeek>> {
        val resultMonth: MutableList<List<DayOfWeek>> = mutableListOf()

        temporalCalendar.time = currentDate
        temporalCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (temporalCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            temporalCalendar.add(Calendar.DAY_OF_MONTH, -1)
        }
        do {
            var weekList = buildList<DayOfWeek> {
                repeat(7) {
                    add(
                        createDayOfWeek(
                            dayOfWeek = temporalCalendar.get(Calendar.DAY_OF_WEEK),
                            dayNumber = temporalCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                            isCurrentDay = currentDateCalendar.get(Calendar.DAY_OF_MONTH)
                                    == temporalCalendar.get(Calendar.DAY_OF_MONTH),
                            month = temporalCalendar.get(Calendar.MONTH),
                        )
                    )
                    temporalCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }
            }
            resultMonth.add(weekList)
        } while (temporalCalendar.get(Calendar.MONTH) == currentDateCalendar.get(Calendar.MONTH))

        return resultMonth.toList()
    }
}



fun createDayOfWeek(
    dayOfWeek: Int,//Calendar.DAY_OF_WEEK
    dayNumber: String,
    isCurrentDay: Boolean,
    month: Int): DayOfWeek {
    return when(dayOfWeek) {
        Calendar.MONDAY -> DayOfWeek.Monday(
            dayNumber = dayNumber,
            isCurrentDay = isCurrentDay,
            month = month
        )
        Calendar.TUESDAY -> DayOfWeek.Tuesday(
            dayNumber = dayNumber,
            isCurrentDay = isCurrentDay,
            month = month
        )
        Calendar.WEDNESDAY -> DayOfWeek.Wednesday(
            dayNumber = dayNumber,
            isCurrentDay = isCurrentDay,
            month = month
        )
        Calendar.THURSDAY -> DayOfWeek.Thursday(
            dayNumber = dayNumber,
            isCurrentDay = isCurrentDay,
            month = month
        )
        Calendar.FRIDAY -> DayOfWeek.Friday(
            dayNumber = dayNumber,
            isCurrentDay = isCurrentDay,
            month = month
        )
        Calendar.SATURDAY -> DayOfWeek.Saturday(
            dayNumber = dayNumber,
            isCurrentDay = isCurrentDay,
            month = month
        )
        Calendar.SUNDAY -> DayOfWeek.Sunday(
            dayNumber = dayNumber,
            isCurrentDay = isCurrentDay,
            month = month
        )

        else -> throw IllegalArgumentException("Unreachable")
    }
}

/*enum class MONTH(val number: Int) {
    JANUARY(Calendar.JANUARY)
}*/
