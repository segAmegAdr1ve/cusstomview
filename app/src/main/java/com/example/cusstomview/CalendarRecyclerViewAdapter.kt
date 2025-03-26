package com.example.cusstomview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cusstomview.databinding.DayItemBinding
import com.example.cusstomview.databinding.RecyclerViewCalendarItemBinding
import com.example.cusstomview.helper.CalendarHelper
import com.example.cusstomview.helper.DAYS_IN_WEEK_NUMBER
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class CalendarRecyclerViewAdapter() :
    RecyclerView.Adapter<CalendarRecyclerViewAdapter.CalendarViewHolder>() {

    private var selectedDayView: DayItemBinding? = null
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
        holder.bind(position) { dayItemView ->
            selectedDayView?.let { view ->
                view.selectedBackground.visibility = View.INVISIBLE
                view.dayOfMonth.setTextColor(Color.BLACK)
            }
            dayItemView.selectedBackground.visibility = View.VISIBLE
            dayItemView.dayOfMonth.setTextColor(Color.WHITE)
            selectedDayView = dayItemView
        }
    }

    override fun getItemCount() = monthList.size / DAYS_IN_WEEK_NUMBER

    fun removeSelectedDayItem() {
        selectedDayView?.let { view ->
            view.selectedBackground.visibility = View.INVISIBLE
            view.dayOfMonth.setTextColor(Color.BLACK)
        }
        selectedDayView = null
    }

    inner class CalendarViewHolder(val binding: RecyclerViewCalendarItemBinding) :
        ViewHolder(binding.root) {
        fun bind(
            weekNumber: Int,
            onSelectionChanged: (selectedView: DayItemBinding) -> Unit
        ) {
            val indexExtra = weekNumber * DAYS_IN_WEEK_NUMBER
            binding.weekLayout.children.forEachIndexed { index, dayItem ->
                bindDay(
                    DayItemBinding.bind(dayItem),
                    monthList[index + indexExtra],
                    onSelectionChanged
                )
            }
        }

        private fun bindDay(
            dayBinding: DayItemBinding,
            day: LocalDate,
            onSelectionChanged: (selectedView: DayItemBinding) -> Unit,
        ) {
            dayBinding.dayOfMonth.text = day.dayOfMonth.toString()
            dayBinding.dayOfWeek.text =
                day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            dayBinding.currentDayMarker.isVisible = day.isEqual(today)
            dayBinding.dayLayout.setOnClickListener {
                onSelectionChanged(dayBinding)
                onSelectedDateChanged(day)
            }
        }
    }
}

