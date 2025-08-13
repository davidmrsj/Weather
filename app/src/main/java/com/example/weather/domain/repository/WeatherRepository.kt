package com.example.weather.domain.repository

import com.example.weather.data.remote.Result
import com.example.weather.domain.model.Weather

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): Result<Weather>
}
