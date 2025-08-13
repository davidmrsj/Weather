package com.example.weather.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeFormatter {
    fun formatTime(seconds: Long): String {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.format(Date(seconds * 1000))
        } catch (e: Exception) {
            "--:--"
        }
    }
}
