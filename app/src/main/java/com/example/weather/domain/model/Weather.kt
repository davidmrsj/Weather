package com.example.weather.domain.model

data class Weather(
    val city: String,
    val description: String,
    val icon: String,
    val temperature: Float,
    val tempMin: Double,
    val tempMax: Double,
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    val clouds: Int,
    val windSpeed: Float,
    val windDeg: Int,
    val windGust: Double?,
    val sunrise: Long,
    val sunset: Long,
    val lastUpdated: Long,
    val rainOneHour: Float?
)
