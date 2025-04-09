package com.example.cusstomview

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.cusstomview.databinding.FragmentDatePickerBottomSheetBinding
import com.example.cusstomview.databinding.MonthChipBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
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
        /*val monthNames = listOf(
            "Январь",
            "Февраль",
            "Март",
            "Апрель",
            "Май",
            "Июнь",
            "Июль",
            "Август",
            "Сентябрь",
            "Октябрь",
            "Ноябрь",
            "Декабрь"
        )*/

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
            selectedDate.update { it.plusMonths(1)}
        }
        binding.arrowBack.setOnClickListener {
            selectedDate.update { it.plusMonths(-1)}
        }

        binding.select.setOnClickListener {
            setFragmentResult("requestKey", Bundle().apply {
                putLong("MY_KEY", selectedDate.value.toEpochDay())
            })
            /*lifecycleScope.launch(Dispatchers.Default) {
                selectedDate.collect { date ->
                    viewModel.onSelectedDateChanged(date)
                }
            }*/
            /*GlobalScope.launch {
                while (true) {
                    Log.d("onSelectedDateChangedJob", "is active = ${onSelectedDateChangedJob.isActive}, isCancelled = ${onSelectedDateChangedJob.isCancelled}, isCompleted = ${onSelectedDateChangedJob.isCompleted} ")
                    delay(300)
                }
            }*/
            this@DatePickerBottomSheetFragment.dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        selectedChip = null
    }
}

fun Month.format(): String {
    return DateTimeFormatter.ofPattern("LLLL").format(this)
        .replaceFirstChar { it.titlecase() }
}

//fun TextView.setTextWith

enum class NominativeMonth() {
    JANUARY,
    FEBRUARY,
    /*MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;*/
}