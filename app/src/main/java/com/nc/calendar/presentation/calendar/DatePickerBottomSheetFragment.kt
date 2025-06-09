package com.nc.calendar.presentation.calendar

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nc.calendar.R
import com.nc.calendar.databinding.FragmentDatePickerBottomSheetBinding
import com.nc.calendar.databinding.MonthChipBinding
import com.nc.calendar.format
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month

class DatePickerBottomSheetFragment() : BottomSheetDialogFragment() {
    private lateinit var selectedDate: MutableStateFlow<LocalDate>
    private var _binding: FragmentDatePickerBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var selectedChip: Chip? = null
    private var selectedChipId: Int? = null

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

    private fun showYearPicker() {
        val numberPicker = NumberPicker(
            ContextThemeWrapper(
                requireContext(),
                R.style.NumberPickerStyle
            )
        ).apply {
            minValue = 1900
            maxValue = 2100
            value = selectedDate.value.year
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setView(numberPicker)
        dialog.setPositiveButton(getString(R.string.select_button_text)) { _, _ ->
            selectedDate.update { date ->
                date.withYear(numberPicker.value)
            }
        }
        dialog.setNegativeButton(getString(R.string.cancel_button_text)) { _, _ -> }
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createChips()
        setupButtons()

        binding.selectedYear.setOnClickListener {
            showYearPicker()
        }

        lifecycleScope.launch {
            selectedDate.collect { selectedDate ->
                binding.selectedMonth.text = selectedDate.month.format()
                binding.selectedYear.text = String.format(selectedDate.year.toString())
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
                root.setOnCheckedChangeListener { _chip, isChecked ->
                    val chip = _chip as Chip
                    selectedChip = if (isChecked) {
                        selectedChipId = chip.id
                        selectedChip?.isChecked = false
                        chip
                    } else if (selectedChipId == chip.id) {
                        selectedChip?.isChecked = true
                        chip
                    } else {
                        null
                    }
                    selectedChipId = selectedChip?.id
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
            selectedDate.update { it.plusMonths(MONTH_STEP) }
        }
        binding.arrowBack.setOnClickListener {
            selectedDate.update { it.minusMonths(MONTH_STEP) }
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
        const val MONTH_STEP = 1L
    }
}
