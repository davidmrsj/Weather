package com.example.weather.core.utils

import android.util.Log
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeFormatter {

    const val TIME_24 = "HH:mm"
    const val UPDATED = "d 'de' MMMM | HH:mm"

    fun formatMillis(
        seconds: Long?,
        pattern: String,
        locale: Locale = Locale.getDefault(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): String {
        return try {
            if (seconds == null || seconds <= 0L) return "--"
            val formatter = DateTimeFormatter.ofPattern(pattern, locale)
            Instant.ofEpochSecond(seconds).atZone(zoneId).format(formatter)
        } catch (e: Exception) {
            Log.e("TimeFormatter", "Error formatting time", e)
            "--/--"
        }
    }
}