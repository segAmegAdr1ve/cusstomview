package com.example.cusstomview

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.cusstomview.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding by lazy { _binding!! }
    private val viewModel: CalendarViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)

        setupSpinner()
        setupAdapter()

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupAdapter() {
        val rvAdapter = CalendarRecyclerViewAdapter()
        binding.recyclerView.adapter = rvAdapter
        rvAdapter.onSelectedDateChanged = { date: LocalDate ->
            binding.dayTimelineView.selectedDateTime = LocalDateTime.of(date, LocalTime.now())
        }

        lifecycleScope.launch {
            //repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.currentMonth.collect { monthList ->
                rvAdapter.monthList = monthList
                rvAdapter.notifyDataSetChanged()
            }
        }

        binding.recyclerView.scrollToPosition(2)
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setupSpinner() {
        val locale = Locale.forLanguageTag("ru")
        val monthList = Month.entries.map { month ->
            month.getDisplayName(TextStyle.SHORT, locale)
        }

        val spAdapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            monthList
        )
        binding.monthSpinner.adapter = spAdapter
        binding.monthSpinner.setSelection(viewModel.calendarHelper.selectedDate.month.value - 1)

        binding.monthSpinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                showCustomSpinnerDialog(monthList)
            }
            true
        }
    }

    private fun showCustomSpinnerDialog(monthList: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, monthList)
        val listView = ListView(this)
        listView.adapter = adapter
        val alertDialog = MaterialAlertDialogBuilder(this)
            .setTitle("Выберите месяц")
            .setView(listView)
            .show()

        alertDialog.window?.let { window ->
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(window.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = 1000
            window.attributes = layoutParams
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            binding.monthSpinner.setSelection(position)
            viewModel.onSelectedMonthChanged(Month.of(position + 1))
            (binding.recyclerView.adapter as CalendarRecyclerViewAdapter).removeSelectionAndViewReference()
            alertDialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}