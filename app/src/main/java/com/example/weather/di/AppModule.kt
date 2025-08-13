package com.example.weather.di

import android.content.Context
import com.example.weather.BuildConfig
import com.example.weather.core.utils.DefaultDispatcherProvider
import com.example.weather.core.utils.DispatcherProvider
import com.example.weather.data.location.LocationProvider
import com.example.weather.data.remote.OpenWeatherService
import com.example.weather.data.repository.WeatherRepositoryImpl
import com.example.weather.domain.repository.LocationRepository
import com.example.weather.domain.repository.WeatherRepository
import com.example.weather.domain.usecase.GetCurrentWeatherUseCase
import com.example.weather.domain.usecase.GetLocationUseCase
import com.example.weather.presentation.screens.weather.WeatherViewModelFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object AppModule {
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider

    private val json = Json { ignoreUnknownKeys = true }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.OPENWEATHER_BASE_URL)
        .client(okHttp)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val weatherService: OpenWeatherService = retrofit.create(OpenWeatherService::class.java)

    private fun provideWeatherRepository(): WeatherRepository =
        WeatherRepositoryImpl(weatherService, BuildConfig.OPENWEATHER_API_KEY, dispatcherProvider)

    private fun provideLocationRepository(context: Context): LocationRepository =
        LocationProvider(context, dispatcherProvider)

    fun provideWeatherViewModelFactory(context: Context): WeatherViewModelFactory {
        val locationRepo = provideLocationRepository(context)
        val weatherRepo = provideWeatherRepository()
        val getLocation = GetLocationUseCase(locationRepo)
        val getWeather = GetCurrentWeatherUseCase(weatherRepo)
        return WeatherViewModelFactory(getLocation, getWeather, dispatcherProvider)
    }
}
