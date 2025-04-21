package com.nc.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import com.nc.calendar.Constants.today
import com.nc.calendar.DatePickerBottomSheetFragment.Companion.DIALOG_RESULT_KEY
import com.nc.calendar.databinding.FragmentCalendarBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CalendarFragment : Fragment(), CalendarRecyclerViewAdapter.Listener {
    private val viewModel: CalendarViewModel by viewModels()
    private val calendarAdapter by lazy { CalendarRecyclerViewAdapter(this) }
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        setupAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCurrentDateField()

        setFragmentResultListener(DatePickerBottomSheetFragment.DIALOG_REQUEST_KEY) { _, bundle ->
            val result = bundle.getLong(DIALOG_RESULT_KEY)
            val date = LocalDate.ofEpochDay(result)
            viewModel.onSelectedDateChanged(date)
            calendarAdapter.clearVisibleSelectedDay()
        }
    }

    private fun setupAdapter() = with(binding) {
        recyclerView.adapter = calendarAdapter

        lifecycleScope.launch {
            viewModel.lastSelectedDay.collect { day ->
                calendarAdapter.setLastSelectedDay(day)
                dayTimelineView.selectedDateTime = LocalDateTime.of(day, LocalTime.now())
            }
        }

        lifecycleScope.launch {
            viewModel.currentMonth.collect { monthList ->
                calendarAdapter.clearVisibleSelectedDay()
                calendarAdapter.submitList(monthList) {
                    recyclerView.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.down_from_top
                        )
                    )
                    recyclerView.scrollToPosition(viewModel.calculatePositionToScroll())
                }
            }
        }
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setupCurrentDateField() = with(binding) {
        currentDate.setOnClickListener {
            DatePickerBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putLong(DIALOG_RESULT_KEY, viewModel.selectedDate.value.toEpochDay())
                }
            }.show(parentFragmentManager, DIALOG_TAG)
        }

        lifecycleScope.launch {
            viewModel.selectedDate.collect { date ->
                if (date.year == today.year) {
                    month.text = date.month.format()
                    year.text = getString(R.string.empty)
                } else {
                    month.text = date.month.formatShort()
                    year.text = date.formatYear()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSelect(day: LocalDate) = viewModel.setLastSelectedDay(day)

    companion object {
        private const val DIALOG_TAG = "DIALOG_TAG"
    }
}