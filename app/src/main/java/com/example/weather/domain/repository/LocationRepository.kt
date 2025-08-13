package com.example.weather.domain.repository

import com.example.weather.data.remote.Result
import com.example.weather.domain.model.Location

interface LocationRepository {
    suspend fun getLocation(): Result<Location>
}
