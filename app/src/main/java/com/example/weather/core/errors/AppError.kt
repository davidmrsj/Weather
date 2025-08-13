package com.example.weather.core.errors

sealed class AppError : Throwable() {
    object NetworkError : AppError()
    data class ApiError(val code: Int, override val message: String? = null) : AppError()
    object ParsingError : AppError()
    object PermissionError : AppError()
    object LocationError : AppError()
    data class UnknownError(val throwable: Throwable? = null) : AppError()
}
