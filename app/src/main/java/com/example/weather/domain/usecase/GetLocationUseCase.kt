package com.example.weather.domain.usecase

import com.example.weather.data.remote.Result
import com.example.weather.domain.model.Location
import com.example.weather.domain.repository.LocationRepository

class GetLocationUseCase(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(): Result<Location> {
        return repository.getLocation()
    }
}
