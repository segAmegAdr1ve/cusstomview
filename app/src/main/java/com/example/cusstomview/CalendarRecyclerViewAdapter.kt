package com.example.cusstomview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cusstomview.databinding.DayItemBinding
import com.example.cusstomview.databinding.RecyclerViewCalendarItemBinding
import com.example.cusstomview.helper.CalendarHelper
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class CalendarRecyclerViewAdapter() :
    RecyclerView.Adapter<CalendarRecyclerViewAdapter.CalendarViewHolder>() {

    private var selectedView: DayItemBinding? = null
    var onSelectedDateChanged: (date: LocalDate) -> Unit = {}

    var monthList: List<LocalDate> = CalendarHelper().createListOfDaysFromToday()
    private val today = LocalDate.now()

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
        holder.bind(monthList, position, today, onSelectedDateChanged) { dayItemView ->
            selectedView?.let { view ->
                view.selectedBackground.visibility = View.INVISIBLE
                view.dayOfMonth.setTextColor(Color.BLACK)
            }
            dayItemView.selectedBackground.visibility = View.VISIBLE
            dayItemView.dayOfMonth.setTextColor(Color.WHITE)
            selectedView = dayItemView
        }
    }

    override fun getItemCount() = monthList.size / 7

    fun removeSelectionAndViewReference() {
        selectedView?.let { view ->
            view.selectedBackground.visibility = View.INVISIBLE
            view.dayOfMonth.setTextColor(Color.BLACK)
        }
        selectedView = null
    }

    class CalendarViewHolder(val binding: RecyclerViewCalendarItemBinding) :
        ViewHolder(binding.root) {
        fun bind(
            itemList: List<LocalDate>,
            weekNumber: Int,
            today: LocalDate,
            onSelectedDateChanged: (date: LocalDate) -> Unit,
            onSelectionChanged: (selectedView: DayItemBinding) -> Unit
        ) {
            val indexExtra = weekNumber * 7
            bindDay(binding.mondayItem, itemList[0 + indexExtra], today, onSelectionChanged, onSelectedDateChanged)
            bindDay(binding.tuesdayItem, itemList[1 + indexExtra], today, onSelectionChanged, onSelectedDateChanged)
            bindDay(binding.wednesdayItem, itemList[2 + indexExtra], today, onSelectionChanged, onSelectedDateChanged)
            bindDay(binding.thursdayItem, itemList[3 + indexExtra], today, onSelectionChanged, onSelectedDateChanged)
            bindDay(binding.fridayItem, itemList[4 + indexExtra], today, onSelectionChanged, onSelectedDateChanged)
            bindDay(binding.saturdayItem, itemList[5 + indexExtra], today, onSelectionChanged, onSelectedDateChanged)
            bindDay(binding.sundayItem, itemList[6 + indexExtra], today, onSelectionChanged, onSelectedDateChanged)
        }

        private fun bindDay(
            dayBinding: DayItemBinding,
            day: LocalDate,
            today: LocalDate,
            onSelectionChanged: (selectedView: DayItemBinding) -> Unit,
            onSelectedDateChanged: (date: LocalDate) -> Unit,
        ) {
            dayBinding.dayOfMonth.text = day.dayOfMonth.toString()
            dayBinding.dayOfWeek.text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru"))
            dayBinding.currentDay.isVisible = day.isEqual(today)
            dayBinding.dayLayout.setOnClickListener {
                onSelectionChanged(dayBinding)
                onSelectedDateChanged(day)
            }
        }
    }
}

