package com.example.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.example.calendar.Constants.NOMINATIVE_MONTH_FORMAT_PATTERN
import com.example.calendar.databinding.FragmentDatePickerBottomSheetBinding
import com.example.calendar.databinding.MonthChipBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

class DatePickerBottomSheetFragment() : BottomSheetDialogFragment() {
    private lateinit var selectedDate: MutableStateFlow<LocalDate>
    private var _binding: FragmentDatePickerBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var selectedChip: Chip? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val date = LocalDate.ofEpochDay(requireArguments().getLong(DIALOG_RESULT_KEY))
        selectedDate = MutableStateFlow(date)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDatePickerBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createChips()
        setupButtons()

        lifecycleScope.launch {
            selectedDate.collect { selectedDate ->
                binding.selectedMonth.text = selectedDate.month.format()
                binding.selectedYear.text = selectedDate.year.toString()
            }
        }

        lifecycleScope.launch {
            selectedDate.collect { selectedDate ->
                binding.root.findViewWithTag<Chip>(selectedDate.month).run {
                    isChecked = true
                    selectedChip = this
                }
            }
        }

    }

    private fun createChips() {
        Month.entries.forEach { month ->
            val chip = MonthChipBinding.inflate(LayoutInflater.from(view?.context)).apply {
                root.id = View.generateViewId()
                root.tag = month
                root.text = month.format()
                root.setOnCheckedChangeListener { chip, isChecked ->
                    selectedChip?.isChecked = false
                    selectedChip = if (isChecked) chip as Chip
                    else null
                    selectedDate.update { date ->
                        date.withMonth(month.value)
                    }
                }
            }
            binding.constraintLayout.addView(chip.root)
            binding.customFlow.referencedIds += chip.root.id
        }
    }

    private fun setupButtons() {
        binding.arrowForward.setOnClickListener {
            selectedDate.update { it.plusMonths(1) }
        }
        binding.arrowBack.setOnClickListener {
            selectedDate.update { it.plusMonths(-1) }
        }

        binding.select.setOnClickListener {
            setFragmentResult(DIALOG_REQUEST_KEY, Bundle().apply {
                putLong(DIALOG_RESULT_KEY, selectedDate.value.toEpochDay())
            })
            this@DatePickerBottomSheetFragment.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        selectedChip = null
    }

    companion object {
        const val DIALOG_REQUEST_KEY = "DIALOG_FRAGMENT_REQUEST_KEY"
        const val DIALOG_RESULT_KEY = "DIALOG_RESULT_KEY"
    }
}

fun Month.format(): String {
    return DateTimeFormatter.ofPattern(NOMINATIVE_MONTH_FORMAT_PATTERN).format(this)
        .replaceFirstChar { it.titlecase() }
}