package com.example.cusstomview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.cusstomview.Constants.NOMINATIVE_MONTH_FORMAT_PATTERN
import com.example.cusstomview.databinding.FragmentDatePickerBottomSheetBinding
import com.example.cusstomview.databinding.MonthChipBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

class DatePickerBottomSheetFragment() : BottomSheetDialogFragment() {
    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var selectedDate: MutableStateFlow<LocalDate>
    private var _binding: FragmentDatePickerBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var selectedChip: Chip? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        selectedDate = MutableStateFlow(viewModel.selectedDate.value)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDatePickerBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Month.entries.forEach { month ->
            val chip = MonthChipBinding.inflate(LayoutInflater.from(view.context)).apply {
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

        lifecycleScope.launch {
            selectedDate.collect { currentDate ->
                binding.selectedMonth.text = currentDate.month.format()
                binding.selectedYear.text = currentDate.year.toString()
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
    return DateTimeFormatter.ofPattern(NOMINATIVE_MONTH_FORMAT_PATTERN)
        .format(this)
        .replaceFirstChar { it.titlecase() }
}