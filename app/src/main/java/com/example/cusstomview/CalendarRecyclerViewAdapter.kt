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
import com.example.cusstomview.Constants.locale
import com.example.cusstomview.databinding.DayItemBinding
import com.example.cusstomview.databinding.RecyclerViewCalendarItemBinding
import java.time.LocalDate
import java.time.format.TextStyle

class CalendarRecyclerViewAdapter(private val selectionOwner: SelectionOwner) :
    ListAdapter<LocalDate, CalendarRecyclerViewAdapter.CalendarViewHolder>(diffCallback),
    View.OnClickListener {

    private val today = LocalDate.now()

    fun removeSelection() {
        selectionResolver.removePreviousSelection()
    }

    override fun onClick(v: View) {
        val selectedDate = v.tag as LocalDate
        selectionOwner.onSelectionChanged(selectedDate)
        notifyItemChanged(currentList.indexOf(selectedDate) / DAYS_IN_WEEK)
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
                )
            }
        }

        private fun bindDay(
            dayBinding: DayItemBinding,
            day: LocalDate,
        ) = with(dayBinding) {
            dayBinding.root.tag = day
            dayOfMonth.text = day.dayOfMonth.toString()
            dayOfWeek.text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
            currentDayMarker.isVisible = day.isEqual(today)
            if (selectionOwner.selectedDate == day) selectionResolver.select(dayBinding)
            dayLayout.setOnClickListener(this@CalendarRecyclerViewAdapter)

        }
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

        private val selectionResolver = object : SelectionResolver<DayItemBinding> {
            override var selectedView: DayItemBinding? = null

            override fun select(view: DayItemBinding) {
                removePreviousSelection()
                view.selectedBackground.visibility = View.VISIBLE
                view.dayOfMonth.isSelected = true
                selectedView = view
            }

            override fun removePreviousSelection() {
                selectedView?.let { view ->
                    view.selectedBackground.visibility = View.INVISIBLE
                    view.dayOfMonth.isSelected = false
                }
                selectedView = null
            }

        }
    }
}

private interface SelectionResolver<T> {
    var selectedView: T?
    fun select(view: T)
    fun removePreviousSelection()
}

