package com.example.weather.data.repository

import com.example.weather.core.errors.AppError
import com.example.weather.core.utils.DispatcherProvider
import com.example.weather.data.mapper.toDomain
import com.example.weather.data.remote.OpenWeatherService
import com.example.weather.data.remote.Result
import com.example.weather.domain.model.Weather
import com.example.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class WeatherRepositoryImpl(
    private val service: OpenWeatherService,
    private val apiKey: String,
    private val dispatcher: DispatcherProvider
) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Result<Weather> =
        withContext(dispatcher.io) {
            try {
                val dto = service.getCurrentWeather(lat, lon, apiKey)
                Result.Success(dto.toDomain())
            } catch (e: IOException) {
                Result.Error(AppError.NetworkError)
            } catch (e: HttpException) {
                Result.Error(AppError.ApiError(e.code(), e.message()))
            } catch (e: SerializationException) {
                Result.Error(AppError.ParsingError)
            } catch (e: SecurityException) {
                Result.Error(AppError.PermissionError)
            } catch (e: Exception) {
                Result.Error(AppError.UnknownError(e))
            }
        }
}
