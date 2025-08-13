package com.example.weather.data.remote

import com.example.weather.core.errors.AppError

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: AppError) : Result<Nothing>()
}