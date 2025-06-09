package com.nc.calendar.presentation.detailweather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.nc.calendar.Constants.PROTOCOL
import com.nc.calendar.Constants.TEMPERATURE_FORMAT_PATTERN
import com.nc.calendar.databinding.RecyclerViewWeatherItemBinding
import com.nc.calendar.domain.model.Hour
import com.nc.calendar.utils.format
import javax.inject.Inject

class WeatherRecyclerViewAdapter @Inject constructor() :
    RecyclerView.Adapter<WeatherRecyclerViewAdapter.WeatherViewHolder>() {
    var hourList = listOf<Hour>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(
            RecyclerViewWeatherItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int): Unit =
        with(holder.binding) {
            time.text = hourList[position].time
            temp.text = hourList[position].temp.format(TEMPERATURE_FORMAT_PATTERN)
            Glide.with(root)
                .load("$PROTOCOL${hourList[position].iconUrl}")
                .into(icon)
        }

    override fun getItemCount() = hourList.size

    class WeatherViewHolder(val binding: RecyclerViewWeatherItemBinding) : ViewHolder(binding.root)
}