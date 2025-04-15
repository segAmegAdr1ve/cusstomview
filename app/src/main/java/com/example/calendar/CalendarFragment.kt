package com.example.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.calendar.DatePickerBottomSheetFragment.Companion.DIALOG_RESULT_KEY
import com.example.calendar.databinding.FragmentCalendarBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


class CalendarFragment : Fragment() {
    private val viewModel: CalendarViewModel by viewModels()
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private var _selectionOwner: SelectionOwner? = null
    private val selectionOwner get() = _selectionOwner!!

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

        binding.currentDate.setOnClickListener {
            DatePickerBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putLong(DIALOG_RESULT_KEY, viewModel.selectedDate.value.toEpochDay())
                }
            }.show(parentFragmentManager, DIALOG_TAG)
        }

        lifecycleScope.launch {
            viewModel.selectedDate.collect { date ->
                binding.month.text = date.month.format()
                binding.year.text = date.year.toString()
            }
        }

        setFragmentResultListener(DatePickerBottomSheetFragment.DIALOG_REQUEST_KEY) { _, bundle ->
            val result = bundle.getLong(DIALOG_RESULT_KEY)
            val date = LocalDate.ofEpochDay(result)
            viewModel.onSelectedDateChanged(date)
        }

    }

    private fun setupAdapter(savedInstanceState: Bundle?) {
        val dateToSelect = if (savedInstanceState != null) {
            val epoch = savedInstanceState.getLong(SELECTED_DATE)
            LocalDate.ofEpochDay(epoch)
        } else {
            LocalDate.now()
        }
        _selectionOwner = object : SelectionOwner(dateToSelect) {

            override fun onSelectionChanged(date: LocalDate) {
                super.onSelectionChanged(date)
                binding.dayTimelineView.selectedDateTime = LocalDateTime.of(date, LocalTime.now())
            }

        }

        val calendarAdapter = CalendarRecyclerViewAdapter(selectionOwner)
        binding.recyclerView.adapter = calendarAdapter

        lifecycleScope.launch {
            viewModel.currentMonth.collect { monthList ->
                calendarAdapter.submitList(monthList) {
                    calendarAdapter.removeSelection()
                }
            }
        }

        binding.recyclerView.scrollToPosition(CENTER_OF_FIVE_WEEKS_LIST)
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(binding.recyclerView)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(
            SELECTED_DATE,
            selectionOwner.selectedDate.toEpochDay()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _selectionOwner = null
    }

    companion object {
        private const val SELECTED_DATE = "SELECTED_DATE"
        private const val CENTER_OF_FIVE_WEEKS_LIST = 2
        private const val DIALOG_TAG = "DIALOG_TAG"
    }
}