package com.example.weather.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.weather.core.errors.AppError
import com.example.weather.core.utils.DispatcherProvider
import com.example.weather.data.remote.Result
import com.example.weather.domain.model.Location
import com.example.weather.domain.repository.LocationRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class LocationProvider(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getLocation(): Result<Location> = withContext(dispatcherProvider.io) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@withContext Result.Error(AppError.PermissionError)
        }
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider = when {
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> return@withContext Result.Error(AppError.LocationError)
        }

        manager.getLastKnownLocation(provider)?.let {
            return@withContext Result.Success(Location(it.latitude, it.longitude))
        }

        suspendCancellableCoroutine { cont ->
            val listener = object : LocationListener {
                override fun onLocationChanged(loc: android.location.Location) {
                    cont.resume(Result.Success(Location(loc.latitude, loc.longitude)))
                    manager.removeUpdates(this)
                }
            }
            try {
                manager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
            } catch (e: Exception) {
                cont.resume(Result.Error(AppError.LocationError))
            }
            cont.invokeOnCancellation { manager.removeUpdates(listener) }
        }
    }
}
