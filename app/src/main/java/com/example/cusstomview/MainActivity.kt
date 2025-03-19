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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalendarViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

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

        lifecycleScope.launch {
            //repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.currentMonth.collect { monthList ->
                rvAdapter.monthList = monthList
                rvAdapter.notifyDataSetChanged()
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            //repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.scrollEvent.collect { index ->
                if (!viewModel.scrolled) {
                    binding.recyclerView.scrollToPosition(index)
                    viewModel.scrolled = true
                }
            }
            //}
        }

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
        binding.monthSpinner.setSelection(viewModel.calendarHelper.selectedMonth.value - 1)

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
            Log.d("tag", layoutParams.height.toString())
            layoutParams.height = 1000
            window.attributes = layoutParams
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            binding.monthSpinner.setSelection(position)
            viewModel.onSelectedMonthChanged(Month.of(position + 1))
            alertDialog.dismiss()
        }
    }

    /*private fun setupSpinner() {
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
        binding.monthSpinner.setSelection(viewModel.calendarHelper.selectedMonth.value - 1)
        binding.monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.onSelectedMonthChanged(
                    Month.of(position + 1)
                )
                Log.d("TAG", "onItemSelected: ")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }*/
}