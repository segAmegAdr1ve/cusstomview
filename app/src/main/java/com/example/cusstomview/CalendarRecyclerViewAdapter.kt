package com.example.cusstomview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cusstomview.databinding.DayItemBinding
import com.example.cusstomview.databinding.RecyclerViewCalendarItemBinding
import com.example.cusstomview.helper.CalendarHelper
import com.example.cusstomview.helper.Day
import com.example.cusstomview.helper.Week
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class CalendarRecyclerViewAdapter() :
    RecyclerView.Adapter<CalendarRecyclerViewAdapter.CalendarViewHolder>() {
    //ListAdapter<List<Day>, CalendarRecyclerViewAdapter.CalendarViewHolder>(diffCallback = diffCallback) {

    //var monthList: List<List<Day>> = emptyList()// = calendarHelper.makeMonth()
    private var selectedView: DayItemBinding? = null

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
        /*holder.binding.bindWeek(monthList[position], {}) { newSelectedView: View, newTextView: TextView ->
            selectedView?.visibility = View.INVISIBLE
            selectedTextView?.setTextColor(Color.BLACK)
            newSelectedView.visibility = View.VISIBLE
            newTextView.setTextColor(Color.WHITE)
            selectedView = newSelectedView
            selectedTextView = newTextView
        }*/
        holder.bind(monthList, position, today) { dayItemView ->
            selectedView?.let { view ->
                view.selectedBackground.visibility = View.INVISIBLE
                view.dayOfMonth.setTextColor(Color.BLACK)
            }
            dayItemView.selectedBackground.visibility = View.VISIBLE
            dayItemView.dayOfMonth.setTextColor(Color.WHITE)
            selectedView = dayItemView
        }
    }

    override fun getItemCount() = monthList.size / 7//monthList.size

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
            onSelectionChanged: (selectedView: DayItemBinding) -> Unit
        ) {
            val indexExtra = weekNumber * 7
            bindDay(binding.mondayItem, itemList[0 + indexExtra], today, onSelectionChanged)
            bindDay(binding.tuesdayItem, itemList[1 + indexExtra], today, onSelectionChanged)
            bindDay(binding.wednesdayItem, itemList[2 + indexExtra], today, onSelectionChanged)
            bindDay(binding.thursdayItem, itemList[3 + indexExtra], today, onSelectionChanged)
            bindDay(binding.fridayItem, itemList[4 + indexExtra], today, onSelectionChanged)
            bindDay(binding.saturdayItem, itemList[5 + indexExtra], today, onSelectionChanged)
            bindDay(binding.sundayItem, itemList[6 + indexExtra], today, onSelectionChanged)
        }

        private fun bindDay(
            dayBinding: DayItemBinding,
            day: LocalDate,
            today: LocalDate,
            onSelectionChanged: (selectedView: DayItemBinding) -> Unit,
        ) {
            dayBinding.dayOfMonth.text = day.dayOfMonth.toString()
            dayBinding.dayOfWeek.text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru"))
            dayBinding.currentDay.isVisible = day.isEqual(today)
            dayBinding.dayLayout.setOnClickListener {
                onSelectionChanged(dayBinding)
            }
        }

        /*enum class WeekViewDisposableCoroutine(view: DayItemBinding) {
            MONDAY()
        }*/
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Week>() {
            override fun areItemsTheSame(oldItem: Week, newItem: Week): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Week, newItem: Week): Boolean {
                return oldItem === newItem
            }
        }
    }


}

fun RecyclerViewCalendarItemBinding.bindWeek(
    week: List<Day>,
    onDayClicked: () -> Unit,
    onSelectionChanged: (selectedView: View, selectedTextView: TextView) -> Unit
) = with(this) {
    week.onEach { dayOfWeek ->
        when (dayOfWeek.numberOfWeek) {
            DayOfWeek.MONDAY -> {
                mondayItem.dayOfMonth.text = dayOfWeek.dayNumber
                mondayItem.dayOfWeek.text = dayOfWeek.weekName
                mondayItem.currentDay.isVisible = dayOfWeek.isCurrentDay
                mondayItem.selectedBackground.visibility = View.INVISIBLE
                mondayItem.dayOfMonth.setTextColor(Color.BLACK)
                mondayItem.dayLayout.setOnClickListener {
                    onSelectionChanged(mondayItem.selectedBackground, mondayItem.dayOfMonth)
                    //TODO() -> ADD onDayClicked callback and pass arguments to it
                }

                /*if (dayOfWeek.isCurrentDay) mondayItem.dayOfMonth.setTextColor(Color.RED)
                else mondayItem.dayOfMonth.setTextColor(Color.BLACK)*/
            }

            DayOfWeek.TUESDAY -> {
                tuesdayItem.dayOfMonth.text = dayOfWeek.dayNumber
                tuesdayItem.dayOfWeek.text = dayOfWeek.weekName
                tuesdayItem.currentDay.isVisible = dayOfWeek.isCurrentDay
                tuesdayItem.selectedBackground.visibility = View.INVISIBLE
                tuesdayItem.dayOfMonth.setTextColor(Color.BLACK)
                tuesdayItem.dayLayout.setOnClickListener {
                    onSelectionChanged(tuesdayItem.selectedBackground, tuesdayItem.dayOfMonth)
                    //TODO() -> ADD onDayClicked callback and pass arguments to it
                }

                /* if (dayOfWeek.isCurrentDay) tuesdayItem.dayOfMonth.setTextColor(Color.RED)
                 else tuesdayItem.dayOfMonth.setTextColor(Color.BLACK)*/
            }

            DayOfWeek.WEDNESDAY -> {
                wednesdayItem.dayOfMonth.text = dayOfWeek.dayNumber
                wednesdayItem.dayOfWeek.text = dayOfWeek.weekName
                wednesdayItem.currentDay.isVisible = dayOfWeek.isCurrentDay

                wednesdayItem.selectedBackground.visibility = View.INVISIBLE
                wednesdayItem.dayOfMonth.setTextColor(Color.BLACK)
                wednesdayItem.dayLayout.setOnClickListener {
                    onSelectionChanged(wednesdayItem.selectedBackground, wednesdayItem.dayOfMonth)
                    //TODO() -> ADD onDayClicked callback and pass arguments to it
                }

                /*if (dayOfWeek.isCurrentDay) wednesdayItem.dayOfMonth.setTextColor(Color.RED)
                else wednesdayItem.dayOfMonth.setTextColor(Color.BLACK)*/
            }

            DayOfWeek.THURSDAY -> {
                thursdayItem.dayOfMonth.text = dayOfWeek.dayNumber
                thursdayItem.dayOfWeek.text = dayOfWeek.weekName
                thursdayItem.currentDay.isVisible = dayOfWeek.isCurrentDay

                thursdayItem.selectedBackground.visibility = View.INVISIBLE
                thursdayItem.dayOfMonth.setTextColor(Color.BLACK)
                thursdayItem.dayLayout.setOnClickListener {
                    onSelectionChanged(thursdayItem.selectedBackground, thursdayItem.dayOfMonth)
                    //TODO() -> ADD onDayClicked callback and pass arguments to it
                }

                /*if (dayOfWeek.isCurrentDay) thursdayItem.dayOfMonth.setTextColor(Color.RED)
                else thursdayItem.dayOfMonth.setTextColor(Color.BLACK)*/
            }

            DayOfWeek.FRIDAY -> {
                fridayItem.dayOfMonth.text = dayOfWeek.dayNumber
                fridayItem.dayOfWeek.text = dayOfWeek.weekName
                fridayItem.currentDay.isVisible = dayOfWeek.isCurrentDay

                fridayItem.selectedBackground.visibility = View.INVISIBLE
                fridayItem.dayOfMonth.setTextColor(Color.BLACK)
                fridayItem.dayLayout.setOnClickListener {
                    onSelectionChanged(fridayItem.selectedBackground, fridayItem.dayOfMonth)
                    //TODO() -> ADD onDayClicked callback and pass arguments to it
                }

                /*if (dayOfWeek.isCurrentDay) fridayItem.dayOfMonth.setTextColor(Color.RED)
                else fridayItem.dayOfMonth.setTextColor(Color.BLACK)*/
            }

            DayOfWeek.SATURDAY -> {
                saturdayItem.dayOfMonth.text = dayOfWeek.dayNumber
                saturdayItem.dayOfWeek.text = dayOfWeek.weekName
                saturdayItem.currentDay.isVisible = dayOfWeek.isCurrentDay

                saturdayItem.selectedBackground.visibility = View.INVISIBLE
                saturdayItem.dayOfMonth.setTextColor(Color.BLACK)
                saturdayItem.dayLayout.setOnClickListener {
                    onSelectionChanged(saturdayItem.selectedBackground, saturdayItem.dayOfMonth)
                    //TODO() -> ADD onDayClicked callback and pass arguments to it
                }

                /*if (dayOfWeek.isCurrentDay) saturdayItem.dayOfMonth.setTextColor(Color.RED)
                else saturdayItem.dayOfMonth.setTextColor(Color.BLACK)*/
            }

            DayOfWeek.SUNDAY -> {
                sundayItem.dayOfMonth.text = dayOfWeek.dayNumber
                sundayItem.dayOfWeek.text = dayOfWeek.weekName
                sundayItem.currentDay.isVisible = dayOfWeek.isCurrentDay

                sundayItem.selectedBackground.visibility = View.INVISIBLE
                sundayItem.dayOfMonth.setTextColor(Color.BLACK)
                sundayItem.dayLayout.setOnClickListener {
                    onSelectionChanged(sundayItem.selectedBackground, sundayItem.dayOfMonth)
                    //TODO() -> ADD onDayClicked callback and pass arguments to it
                }

                /*if (dayOfWeek.isCurrentDay) sundayItem.dayOfMonth.setTextColor(Color.RED)
                else sundayItem.dayOfMonth.setTextColor(Color.BLACK)*/
            }
        }
    }
}

/*fun RecyclerViewCalendarItemBinding.unselectAll() {
    for (childIndex in 0 until root.childCount) {
        root.getChildAt(childIndex)
    }
    this.root.childCount
    this.root.getChildAt()
}*/
/*fun traverseView(view: View, ) {

    if (view is ViewGroup) {
        for (childIndex in 0 until view.childCount) {
            val childView = view.getChildAt(childIndex)
            if (childView is ViewGroup) {
                traverseView(childView)
            } else {
                if (childView.)
            }
        }
    }
}*/
fun traverseAndHideSelectedBackground(view: View) {

    if (view.id == R.id.selected_background) {
        view.visibility = View.INVISIBLE
    }

    if (view.id == R.id.day_of_month) {
        (view as? TextView)?.setTextColor(Color.BLACK)
    }

    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val childView = view.getChildAt(i)
            traverseAndHideSelectedBackground(childView)
        }
    }
}