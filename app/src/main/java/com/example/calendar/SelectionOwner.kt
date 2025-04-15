package com.example.calendar

import java.time.LocalDate

abstract class SelectionOwner(date: LocalDate) {
    var selectedDate: LocalDate = date
        private set

    open fun onSelectionChanged(date: LocalDate) {
        selectedDate = date
    }
}