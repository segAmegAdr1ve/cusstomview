package com.nc.calendar.di

import com.nc.calendar.data.repository.WeatherRepositoryImpl
import com.nc.calendar.domain.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Binds
    fun bindWeatherRepository(weatherRepository: WeatherRepositoryImpl): WeatherRepository
}