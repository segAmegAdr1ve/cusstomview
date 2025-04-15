package com.example.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.calendar.Constants.DAYS_IN_WEEK
import com.example.calendar.Constants.getLocale
import com.example.calendar.databinding.DayItemBinding
import com.example.calendar.databinding.RecyclerViewCalendarItemBinding
import java.time.LocalDate
import java.time.format.TextStyle

class CalendarRecyclerViewAdapter(
    private val listener: Listener
) : ListAdapter<LocalDate, CalendarRecyclerViewAdapter.CalendarViewHolder>(diffCallback) {

    private val today = LocalDate.now()
    private var selectedDay: LocalDate? = null
    private var selectedView: DayItemBinding? = null

    fun removeSelection() {
        clearSelectedDay()
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

    override fun getItemCount() = currentList.size / DAYS_IN_WEEK

    inner class CalendarViewHolder(private val binding: RecyclerViewCalendarItemBinding) :
        ViewHolder(binding.root) {
        fun bind(weekNumber: Int) {
            val indexExtra = weekNumber * DAYS_IN_WEEK
            binding.weekLayout.children.forEachIndexed { index, dayItem ->
                bindDay(
                    dayBinding = DayItemBinding.bind(dayItem),
                    day = currentList[index + indexExtra]
                )
            }
        }

        private fun bindDay(
            dayBinding: DayItemBinding,
            day: LocalDate
        ) = with(dayBinding) {
            root.tag = day
            dayOfMonth.text = String.format(day.dayOfMonth.toString())
            dayOfWeek.text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, getLocale())
            currentDayMarker.isVisible = day.isEqual(today)
            setSelectedDay(day = day, dayBinding = dayBinding)
            dayLayout.setOnClickListener { view ->
                selectedDay = view.tag as? LocalDate
                selectedDay?.let {
                    setSelectedDay(day = day, dayBinding = dayBinding)
                }
            }
        }
    }

    fun setSelectedDay(day: LocalDate) {
        selectedDay = day
    }

    private fun setSelectedDay(day: LocalDate, dayBinding: DayItemBinding) {
        selectedDay?.let {
            if (it == day) {
                clearSelectedDay()
                listener.onSelect(it)
                selectedView = dayBinding.apply {
                    selectedBackground.visibility = View.VISIBLE
                    dayOfMonth.isSelected = true
                }
            }
        }
    }

    private fun clearSelectedDay() {
        selectedView?.let { view ->
            view.selectedBackground.visibility = View.INVISIBLE
            view.dayOfMonth.isSelected = false
            selectedView = null
        }
        selectedDay = null
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<LocalDate>() {
            override fun areItemsTheSame(oldItem: LocalDate, newItem: LocalDate): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: LocalDate, newItem: LocalDate): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface Listener {
        fun onSelect(day: LocalDate)
    }
}
