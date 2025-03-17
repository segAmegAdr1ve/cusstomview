package com.example.cusstomview

import java.util.Calendar

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
