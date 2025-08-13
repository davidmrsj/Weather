package com.example.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    val weather: List<WeatherDescriptionDto>,
    val main: MainDto,
    val wind: WindDto,
    val clouds: CloudsDto,
    val sys: SysDto,
    val name: String,
    @SerialName("dt") val dt: Long
)

@Serializable
data class WeatherDescriptionDto(
    val description: String,
    val icon: String
)

@Serializable
data class MainDto(
    val temp: Float,
    @SerialName("feels_like") val feelsLike: Double,
    @SerialName("temp_min") val tempMin: Double,
    @SerialName("temp_max") val tempMax: Double,
    val pressure: Int,
    val humidity: Int
)

@Serializable
data class WindDto(
    val speed: Float,
    val deg: Int,
    val gust: Double? = null
)

@Serializable
data class CloudsDto(
    val all: Int
)

@Serializable
data class SysDto(
    val sunrise: Long,
    val sunset: Long
)
