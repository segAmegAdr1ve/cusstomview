package com.example.cusstomview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cusstomview.Constants.DAYS_IN_WEEK
import com.example.cusstomview.Constants.getLocale
import com.example.cusstomview.databinding.DayItemBinding
import com.example.cusstomview.databinding.RecyclerViewCalendarItemBinding
import java.time.LocalDate
import java.time.format.TextStyle

class CalendarRecyclerViewAdapter() :
    ListAdapter<LocalDate, CalendarRecyclerViewAdapter.CalendarViewHolder>(diffCallback) {

    var onSelectedDateChanged: (date: LocalDate) -> Unit = {}

    private val today = LocalDate.now()
    var selectedDateListPosition = currentList.indexOf(today)

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

    override fun getItemCount() = currentList.size / DAYS_IN_WEEK

    inner class CalendarViewHolder(val binding: RecyclerViewCalendarItemBinding) :
        ViewHolder(binding.root) {
        fun bind(
            weekNumber: Int,
        ) {
            val indexExtra = weekNumber * DAYS_IN_WEEK
            binding.weekLayout.children.forEachIndexed { index, dayItem ->
                bindDay(
                    DayItemBinding.bind(dayItem),
                    currentList[index + indexExtra],
                    index + indexExtra
                )
            }
        }

        private fun bindDay(
            dayBinding: DayItemBinding,
            day: LocalDate,
            itemIndex: Int
        ) = with(dayBinding) {
            dayOfMonth.text = day.dayOfMonth.toString()
            dayOfWeek.text =
                day.dayOfWeek.getDisplayName(TextStyle.SHORT, getLocale())
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

    companion object {
        private const val NO_SELECTION = -1
        val diffCallback = object : DiffUtil.ItemCallback<LocalDate>() {
            override fun areItemsTheSame(oldItem: LocalDate, newItem: LocalDate): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: LocalDate, newItem: LocalDate): Boolean {
                return oldItem == newItem
            }
        }
    }
}

