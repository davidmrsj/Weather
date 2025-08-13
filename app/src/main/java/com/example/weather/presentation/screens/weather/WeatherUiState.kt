package com.example.weather.presentation.screens.weather

import com.example.weather.domain.model.Weather

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val weather: Weather) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
