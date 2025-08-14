package com.example.weather.data.mapper

import com.example.weather.data.remote.dto.WeatherResponseDto
import com.example.weather.domain.model.Weather

fun WeatherResponseDto.toDomain(): Weather = Weather(
    city = name,
    description = weather.firstOrNull()?.description ?: "",
    icon = weather.firstOrNull()?.icon ?: "",
    temperature = main.temp,
    tempMin = main.tempMin,
    tempMax = main.tempMax,
    feelsLike = main.feelsLike,
    pressure = main.pressure,
    humidity = main.humidity,
    clouds = clouds.all,
    windSpeed = wind.speed,
    windDeg = wind.deg,
    windGust = wind.gust,
    sunrise = sys.sunrise,
    sunset = sys.sunset,
    lastUpdated = dt,
    rainOneHour = rain?.oneHour
)
