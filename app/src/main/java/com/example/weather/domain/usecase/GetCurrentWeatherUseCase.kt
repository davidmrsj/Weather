package com.example.weather.domain.usecase

import com.example.weather.data.remote.Result
import com.example.weather.domain.model.Weather
import com.example.weather.domain.repository.WeatherRepository

class GetCurrentWeatherUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<Weather> {
        return repository.getCurrentWeather(lat, lon)
    }
}
