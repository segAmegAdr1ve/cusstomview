package com.nc.calendar.presentation.detailweather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nc.calendar.Constants.M_PER_SECOND_FORMAT_PATTERN
import com.nc.calendar.Constants.PERCENT_FORMAT_PATTERN
import com.nc.calendar.Constants.PROTOCOL
import com.nc.calendar.Constants.TEMPERATURE_FORMAT_PATTERN
import com.nc.calendar.WeatherState
import com.nc.calendar.databinding.FragmentDetailWeatherBinding
import com.nc.calendar.utils.format
import com.nc.calendar.utils.formatDayOfWeek
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class DetailWeatherFragment : Fragment() {
    private var _binding: FragmentDetailWeatherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailWeatherViewModel by viewModels()

    @Inject
    lateinit var adapter: WeatherRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val epoch = arguments?.getLong(DATE_ARG_KEY) ?: throw IllegalArgumentException()
        if (savedInstanceState == null) viewModel.getWeatherByDate(LocalDate.ofEpochDay(epoch))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        lifecycleScope.launch {
            viewModel.weatherState.collect { state ->
                with(binding) {
                    when (state) {
                        is WeatherState.Error -> {
                            toolbar.visibility = View.INVISIBLE
                            weatherCard.visibility = View.INVISIBLE
                            infoLayout.visibility = View.VISIBLE
                            errorTextField.text = state.message
                            loader.visibility = View.GONE
                        }

                        is WeatherState.Loading -> {
                            toolbar.visibility = View.INVISIBLE
                            infoLayout.visibility = View.VISIBLE
                            loader.visibility = View.VISIBLE
                            weatherCard.visibility = View.INVISIBLE
                        }

                        is WeatherState.Loaded -> {
                            maxTemp.text = state.data.maxTemp.format(TEMPERATURE_FORMAT_PATTERN)
                            midTemp.text = state.data.midTemp.format(TEMPERATURE_FORMAT_PATTERN)
                            minTemp.text = state.data.minTemp.format(TEMPERATURE_FORMAT_PATTERN)
                            windSpeed.text =
                                state.data.windSpeed.format(M_PER_SECOND_FORMAT_PATTERN)
                            humidity.text = state.data.humidity.format(PERCENT_FORMAT_PATTERN)
                            Glide.with(requireContext())
                                .load("$PROTOCOL${state.data.iconUrl}")
                                .into(image)
                            weatherRecyclerView.adapter = adapter
                            adapter.hourList = state.data.hourly
                            toolbar.setTitle(state.data.date.formatDayOfWeek())
                            weatherCard.visibility = View.VISIBLE
                            infoLayout.visibility = View.INVISIBLE
                            toolbar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val DATE_ARG_KEY = "DATE_ARG_KEY"
    }
}