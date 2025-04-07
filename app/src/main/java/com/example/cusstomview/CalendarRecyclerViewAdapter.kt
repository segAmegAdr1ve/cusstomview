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

    override fun getItemCount() = monthList.size / DAYS_IN_WEEK

    inner class CalendarViewHolder(val binding: RecyclerViewCalendarItemBinding) :
        ViewHolder(binding.root) {
        fun bind(
            weekNumber: Int,
        ) {
            val indexExtra = weekNumber * DAYS_IN_WEEK
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
            with(dayBinding) {
                dayOfMonth.text = day.dayOfMonth.toString()
                dayOfWeek.text =
                    day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                currentDayMarker.isVisible = day.isEqual(today)
                selectedBackground.visibility =
                    if (itemIndex == selectedDateListPosition) View.VISIBLE else View.INVISIBLE
                dayOfMonth.isSelected = itemIndex == selectedDateListPosition
                dayLayout.setOnClickListener {
                    val oldSelectedAdapterPosition = selectedDateListPosition / DAYS_IN_WEEK
                    val newSelectedAdapterPosition = itemIndex / DAYS_IN_WEEK
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
}

