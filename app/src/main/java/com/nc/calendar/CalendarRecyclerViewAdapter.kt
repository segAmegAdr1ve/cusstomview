package com.nc.calendar

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nc.calendar.Constants.DAYS_IN_WEEK
import com.nc.calendar.Constants.locale
import com.nc.calendar.databinding.DayItemBinding
import com.nc.calendar.databinding.RecyclerViewCalendarItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class CalendarRecyclerViewAdapter(
    private val listener: Listener
) : ListAdapter<LocalDate, CalendarRecyclerViewAdapter.CalendarViewHolder>(diffCallback) {

    private val today = LocalDate.now()
    private var lastSelectedDay: LocalDate = today
    private var selectedView: DayItemBinding? = null

    fun clearVisibleSelectedDay() {
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
            if (lastSelectedDay == day) {
                setLastSelectedDay(day = day, dayBinding = dayBinding)
            }
            dayOfMonth.text = String.format(day.dayOfMonth.toString())
            dayOfWeek.text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
            currentDayMarker.isVisible = day.isEqual(today)
            dayLayout.setOnClickListener {
                setLastSelectedDay(day = day, dayBinding = dayBinding)
            }
        }
    }

    fun setLastSelectedDay(day: LocalDate) {
        lastSelectedDay = day
    }

    private fun setLastSelectedDay(day: LocalDate, dayBinding: DayItemBinding) {
        clearSelectedDay()
        lastSelectedDay = day
        listener.onSelect(lastSelectedDay)
        selectedView = dayBinding.apply {
            selectedBackground.visibility = View.VISIBLE
            dayOfMonth.isSelected = true
        }
    }

    private fun clearSelectedDay() {
        selectedView?.let { view ->
            view.selectedBackground.visibility = View.INVISIBLE
            view.dayOfMonth.isSelected = false
            selectedView = null
        }
    }

    interface Listener {
        fun onSelect(day: LocalDate)
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
}

suspend fun main() {
    var counter = 0
    var counterA = AtomicInteger(0)
    massiveRun { counter++ }
    massiveRun { counterA.incrementAndGet() }
    println(counter)
    println(counterA)
}
suspend fun massiveRun(action: suspend () -> Unit) {
    withContext(Dispatchers.Default) {
        val mutex = Mutex()

        repeat(100) {
            launch {
                mutex.withLock {
                    repeat(100) { action() }
                }
            }
        }
    }
}
/*fun main() {
    var counter = 0
    var listOfThreads: MutableList<Thread> = emptyList<Thread>().toMutableList()
    repeat(1000) {
        listOfThreads.add(
            thread {
                counter++
            }
        )
    }
    //listOfThreads.forEach {it.join()}
    println(counter)
}*/
