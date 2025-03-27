package com.example.cusstomview

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

private const val NO_SELECTION = -1

class CalendarRecyclerViewAdapter() :
    RecyclerView.Adapter<CalendarRecyclerViewAdapter.CalendarViewHolder>() {

    var onSelectedDateChanged: (date: LocalDate) -> Unit = {}
    var monthList: List<LocalDate> = CalendarHelper().createListOfDaysFromToday()

    private val today = LocalDate.now()
    var selectedDateListPosition = monthList.indexOf(today)

    fun removeSelection() {
        selectedDateListPosition = NO_SELECTION
    }

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
        holder.bind(position)
    }

    override fun getItemCount() = monthList.size / DAYS_IN_WEEK_NUMBER

    inner class CalendarViewHolder(val binding: RecyclerViewCalendarItemBinding) :
        ViewHolder(binding.root) {
        fun bind(
            weekNumber: Int,
        ) {
            val indexExtra = weekNumber * DAYS_IN_WEEK_NUMBER
            binding.weekLayout.children.forEachIndexed { index, dayItem ->
                bindDay(
                    DayItemBinding.bind(dayItem),
                    monthList[index + indexExtra],
                    index + indexExtra
                )
            }
        }

        private fun bindDay(
            dayBinding: DayItemBinding,
            day: LocalDate,
            itemIndex: Int
        ) {
            dayBinding.dayOfMonth.text = day.dayOfMonth.toString()
            dayBinding.dayOfWeek.text =
                day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            dayBinding.currentDayMarker.isVisible = day.isEqual(today)
            if (itemIndex == selectedDateListPosition) {
                dayBinding.selectedBackground.visibility = View.VISIBLE
                dayBinding.dayOfMonth.isSelected = true
            } else {
                dayBinding.selectedBackground.visibility = View.INVISIBLE
                dayBinding.dayOfMonth.isSelected = false
            }
            dayBinding.dayLayout.setOnClickListener {
                val oldSelectedAdapterPosition = selectedDateListPosition / DAYS_IN_WEEK_NUMBER
                val newSelectedAdapterPosition = itemIndex / DAYS_IN_WEEK_NUMBER
                selectedDateListPosition = itemIndex
                notifyItemChanged(newSelectedAdapterPosition)
                if (oldSelectedAdapterPosition != newSelectedAdapterPosition) {
                    notifyItemChanged(oldSelectedAdapterPosition)
                }
                onSelectedDateChanged(day)
            }
        }
    }
}

