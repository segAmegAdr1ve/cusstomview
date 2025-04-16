package com.nc.calendar

import android.R.layout.simple_list_item_1
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nc.calendar.Constants.FIRST_DAY_OF_MONTH
import com.nc.calendar.databinding.FragmentCalendarBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.format.TextStyle


class CalendarFragment : Fragment(), CalendarRecyclerViewAdapter.Listener {
    private val viewModel: CalendarViewModel by viewModels()
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter(savedInstanceState)

        setupMonthPicker()
    }

    private fun setupAdapter(savedInstanceState: Bundle?) {
        val calendarAdapter = CalendarRecyclerViewAdapter(this)
        calendarAdapter.setSelectedDay(
            getSelectedDay(savedInstanceState)
        )
        binding.recyclerView.adapter = calendarAdapter

        lifecycleScope.launch {
            viewModel.currentMonth.collect { monthList ->
                calendarAdapter.submitList(monthList)
            }
        }

        binding.recyclerView.scrollToPosition(CENTER_OF_FIVE_WEEKS_LIST)
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun getSelectedDay(savedState: Bundle?) =
        if (savedState != null) LocalDate.ofEpochDay(savedState.getLong(SELECTED_DATE))
        else LocalDate.now()


    private fun setupMonthPicker() {
        val locale = Constants.getLocale()
        val monthList = Month.entries.map { month ->
            month.getDisplayName(TextStyle.SHORT, locale)
        }

        val monthPickerAdapter = ArrayAdapter(
            requireContext(),
            support_simple_spinner_dropdown_item,
            monthList
        )
        binding.monthSpinner.adapter = monthPickerAdapter
        binding.monthSpinner.setSelection(viewModel.calendarHelper.selectedDate.month.value - FIRST_DAY_OF_MONTH)

        binding.monthSpinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                showMonthPickerDialog(monthList)
            }
            true
        }
    }

    private fun showMonthPickerDialog(monthList: List<String>) {
        val adapter = ArrayAdapter(requireContext(), simple_list_item_1, monthList)
        val listView = ListView(requireContext())
        listView.adapter = adapter
        val alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.month_picker_title))
            .setView(listView)
            .show()

        listView.setOnItemClickListener { _, _, position, _ ->
            binding.monthSpinner.setSelection(position)
            viewModel.onSelectedMonthChanged(Month.of(position + FIRST_DAY_OF_MONTH))
            (binding.recyclerView.adapter as CalendarRecyclerViewAdapter).removeSelection()
            alertDialog.dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(
            SELECTED_DATE,
            binding.dayTimelineView.selectedDateTime.toLocalDate().toEpochDay()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSelect(day: LocalDate) = with(binding) {
        dayTimelineView.selectedDateTime = LocalDateTime.of(day, LocalTime.now())
    }

    companion object {
        private const val SELECTED_DATE = "SELECTED_DATE_LIST_POSITION"
        private const val CENTER_OF_FIVE_WEEKS_LIST = 2
    }
}