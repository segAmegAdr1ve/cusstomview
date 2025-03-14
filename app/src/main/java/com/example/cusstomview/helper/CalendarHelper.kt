package com.example.cusstomview.helper

import android.util.Log
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
    private val CalendarUiStateRepresentation = Calendar
        .getInstance().apply { time = currentDate }

    //
    private val temporalCalendar = Calendar.getInstance()
    private val currentDateCalendar = Calendar.getInstance().apply { time = currentDate }

    fun getMonthList(): List<List<DayOfWeek>> {
        val resultMonth: MutableList<List<DayOfWeek>> = mutableListOf()

        /*val localDate = LocalDate.now()
        LocalDate.*/

        temporalCalendar.time = currentDate
        temporalCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (temporalCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            temporalCalendar.add(Calendar.DAY_OF_MONTH, -1)
        }
        //java.time.DayOfWeek
        //LocalDate.now().get(java.time.temporal.ChronoField.)
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
        /*while (temporalCalendar.get(Calendar.MONTH) == currentDateCalendar.get(Calendar.MONTH)) {
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
        }*/
        Log.d("taggg", resultMonth.size.toString())
        return resultMonth.toList()

        //temporalCalendar.get(Calendar.DAY_OF_WEEK)

    }

    /*fun getCurrentWeek(): List<DayOfWeek> {
        temporalCalendar.time = currentDate

        val currentDateInt = temporalCalendar.get(Calendar.DAY_OF_MONTH)

        temporalCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val week = buildList {
            add(
                DayOfWeek.Monday(
                    dayNumber = temporalCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                    isCurrentDay = temporalCalendar.get(Calendar.DAY_OF_MONTH) == currentDateInt
                )
            )
            temporalCalendar.add(Calendar.DAY_OF_MONTH, 1)
            add(
                DayOfWeek.Tuesday(
                    dayNumber = temporalCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                    isCurrentDay = temporalCalendar.get(Calendar.DAY_OF_MONTH) == currentDateInt
                )
            )
            temporalCalendar.add(Calendar.DAY_OF_MONTH, 1)
            add(
                DayOfWeek.Wednesday(
                    dayNumber = temporalCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                    isCurrentDay = temporalCalendar.get(Calendar.DAY_OF_MONTH) == currentDateInt
                )
            )
            temporalCalendar.add(Calendar.DAY_OF_MONTH, 1)
            add(
                DayOfWeek.Thursday(
                    dayNumber = temporalCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                    isCurrentDay = temporalCalendar.get(Calendar.DAY_OF_MONTH) == currentDateInt
                )
            )
            temporalCalendar.add(Calendar.DAY_OF_MONTH, 1)
            add(
                DayOfWeek.Friday(
                    dayNumber = temporalCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                    isCurrentDay = temporalCalendar.get(Calendar.DAY_OF_MONTH) == currentDateInt
                )
            )
            temporalCalendar.add(Calendar.DAY_OF_MONTH, 1)
            add(
                DayOfWeek.Saturday(
                    dayNumber = temporalCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                    isCurrentDay = temporalCalendar.get(Calendar.DAY_OF_MONTH) == currentDateInt
                )
            )
            temporalCalendar.add(Calendar.DAY_OF_MONTH, 1)
            add(
                DayOfWeek.Sunday(
                    dayNumber = temporalCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                    isCurrentDay = temporalCalendar.get(Calendar.DAY_OF_MONTH) == currentDateInt
                )
            )
        }
        val w: DayOfWeek = DayOfWeek.Sunday(dayNumber = "5", isCurrentDay = false)
        w.return week
    }*/

}

sealed interface DayOfWeek {
    val isCurrentDay: Boolean
    val month: Int

    data class Sunday(
        val name: String = "Вс",
        val dayNumber: String,
        val numberOfWeek: Int = Calendar.SUNDAY,
        override val isCurrentDay: Boolean,
        override val month: Int
    ) : DayOfWeek

    data class Monday(
        val name: String = "Пн",
        val dayNumber: String,
        val numberOfWeek: Int = Calendar.MONDAY,
        override val isCurrentDay: Boolean,
        override val month: Int
    ) : DayOfWeek

    data class Tuesday(
        val name: String = "Вт",
        val dayNumber: String,
        val numberOfWeek: Int = Calendar.TUESDAY,
        override val isCurrentDay: Boolean,
        override val month: Int
    ) : DayOfWeek

    data class Wednesday(
        val name: String = "Ср",
        val dayNumber: String,
        val numberOfWeek: Int = Calendar.WEDNESDAY,
        override val isCurrentDay: Boolean,
        override val month: Int
    ) : DayOfWeek

    data class Thursday(
        val name: String = "Чт",
        val dayNumber: String,
        val numberOfWeek: Int = Calendar.THURSDAY,
        override val isCurrentDay: Boolean,
        override val month: Int
    ) : DayOfWeek

    data class Friday(
        val name: String = "Пт",
        val dayNumber: String,
        val numberOfWeek: Int = Calendar.FRIDAY,
        override val isCurrentDay: Boolean,
        override val month: Int
    ) : DayOfWeek

    data class Saturday(
        val name: String = "Сб",
        val dayNumber: String,
        val numberOfWeek: Int = Calendar.SATURDAY,
        override val isCurrentDay: Boolean,
        override val month: Int
    ) : DayOfWeek
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
