package com.example.weather.presentation.screens.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.core.errors.AppError
import com.example.weather.core.utils.DispatcherProvider
import com.example.weather.data.remote.Result
import com.example.weather.domain.usecase.GetCurrentWeatherUseCase
import com.example.weather.domain.usecase.GetLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getLocationUseCase: GetLocationUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun loadWeather() {
        viewModelScope.launch(dispatcherProvider.io) {
            _uiState.value = WeatherUiState.Loading
            when (val locationResult = getLocationUseCase()) {
                is Result.Error -> _uiState.value =
                    WeatherUiState.Error(mapError(locationResult.error))

                is Result.Success -> {
                    val loc = locationResult.data
                    when (val weatherResult =
                        getCurrentWeatherUseCase(loc.latitude, loc.longitude)) {
                        is Result.Error -> _uiState.value =
                            WeatherUiState.Error(mapError(weatherResult.error))

                        is Result.Success -> _uiState.value =
                            WeatherUiState.Success(weatherResult.data)
                    }
                }
            }
        }
    }

    private fun mapError(error: AppError): String = when (error) {
        AppError.PermissionError -> "Location permission required"
        AppError.LocationError -> "Unable to obtain location"
        AppError.NetworkError -> "Network error"
        is AppError.ApiError -> "Api error ${error.code}"
        AppError.ParsingError -> "Parsing error"
        is AppError.UnknownError -> "Unknown error"
    }
}

class WeatherViewModelFactory(
    private val getLocationUseCase: GetLocationUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(
                getLocationUseCase,
                getCurrentWeatherUseCase,
                dispatcherProvider
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
