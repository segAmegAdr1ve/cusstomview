package com.example.cusstomview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cusstomview.databinding.RecyclerViewCalendarItemBinding
import com.example.cusstomview.helper.CalendarHelper
import com.example.cusstomview.helper.DayOfWeek

class CalendarRecyclerViewAdapter(
    //val calendar: Calendar,
    private val calendarHelper: CalendarHelper
) : RecyclerView.Adapter<CalendarRecyclerViewAdapter.CalendarViewHolder>() {

    var monthList: List<List<DayOfWeek>> = calendarHelper.getMonthList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder(
            RecyclerViewCalendarItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.binding.bindWeek(monthList[position])
    }

    override fun getItemCount() = monthList.size

    class CalendarViewHolder(val binding: RecyclerViewCalendarItemBinding) :
        ViewHolder(binding.root) {

    }
}

fun RecyclerViewCalendarItemBinding.bindWeek(week: List<DayOfWeek>) = with(this) {
    week.onEach { dayOfWeek ->
        when (dayOfWeek) {
            is DayOfWeek.Monday -> {
                mondayItem.dayOfMonth.text = dayOfWeek.dayNumber
                mondayItem.dayOfWeek.text = dayOfWeek.name
                mondayItem.currentDay.isVisible = dayOfWeek.isCurrentDay
            }

            is DayOfWeek.Tuesday -> {
                tuesdayItem.dayOfMonth.text = dayOfWeek.dayNumber
                tuesdayItem.dayOfWeek.text = dayOfWeek.name
                tuesdayItem.currentDay.isVisible = dayOfWeek.isCurrentDay
            }

            is DayOfWeek.Wednesday -> {
                wednesdayItem.dayOfMonth.text = dayOfWeek.dayNumber
                wednesdayItem.dayOfWeek.text = dayOfWeek.name
                wednesdayItem.currentDay.isVisible = dayOfWeek.isCurrentDay
            }

            is DayOfWeek.Thursday -> {
                thursdayItem.dayOfMonth.text = dayOfWeek.dayNumber
                thursdayItem.dayOfWeek.text = dayOfWeek.name
                thursdayItem.currentDay.isVisible = dayOfWeek.isCurrentDay
            }

            is DayOfWeek.Friday -> {
                fridayItem.dayOfMonth.text = dayOfWeek.dayNumber
                fridayItem.dayOfWeek.text = dayOfWeek.name
                fridayItem.currentDay.isVisible = dayOfWeek.isCurrentDay
            }

            is DayOfWeek.Saturday -> {
                saturdayItem.dayOfMonth.text = dayOfWeek.dayNumber
                saturdayItem.dayOfWeek.text = dayOfWeek.name
                saturdayItem.currentDay.isVisible = dayOfWeek.isCurrentDay
            }

            is DayOfWeek.Sunday -> {
                sundayItem.dayOfMonth.text = dayOfWeek.dayNumber
                sundayItem.dayOfWeek.text = dayOfWeek.name
                sundayItem.currentDay.isVisible = dayOfWeek.isCurrentDay
            }
        }
    }
}
