package com.example.weather.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.weather.core.errors.AppError
import com.example.weather.core.utils.DispatcherProvider
import com.example.weather.data.remote.Result
import com.example.weather.domain.model.Location
import com.example.weather.domain.repository.LocationRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class LocationProvider(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider
) : LocationRepository {

    override suspend fun getLocation(): Result<Location> = withContext(dispatcherProvider.io) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            return@withContext Result.Error(AppError.PermissionError)
        }

        val fused = LocationServices.getFusedLocationProviderClient(context)

        suspendCancellableCoroutine<Result<Location>> { cont ->
            val cts = CancellationTokenSource()

            fused.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cts.token
            ).addOnSuccessListener { loc ->
                if (loc != null && !cont.isCompleted) {
                    cont.resume(Result.Success(Location(loc.latitude, loc.longitude)))
                } else {
                    fused.lastLocation
                        .addOnSuccessListener { last ->
                            if (last != null && !cont.isCompleted) {
                                cont.resume(Result.Success(Location(last.latitude, last.longitude)))
                            } else if (!cont.isCompleted) {
                                cont.resume(Result.Error(AppError.LocationError))
                            }
                        }
                        .addOnFailureListener {
                            if (!cont.isCompleted) cont.resume(Result.Error(AppError.LocationError))
                        }
                }
            }.addOnFailureListener {
                fused.lastLocation
                    .addOnSuccessListener { last ->
                        if (last != null && !cont.isCompleted) {
                            cont.resume(Result.Success(Location(last.latitude, last.longitude)))
                        } else if (!cont.isCompleted) {
                            cont.resume(Result.Error(AppError.LocationError))
                        }
                    }
                    .addOnFailureListener {
                        if (!cont.isCompleted) cont.resume(Result.Error(AppError.LocationError))
                    }
            }

            cont.invokeOnCancellation { cts.cancel() }
        }
    }
}
