package com.ekichochan.app.ui.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

const val MAX_LOCATION_ACCURACY_METERS = 50f

internal fun isLocationAccuracyAcceptable(hasAccuracy: Boolean, accuracyMeters: Float): Boolean =
    hasAccuracy && accuracyMeters.isFinite() && accuracyMeters in 0f..MAX_LOCATION_ACCURACY_METERS

@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context): Location? = suspendCancellableCoroutine { continuation ->
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val provider = when {
        manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
        manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
        else -> {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
    }
    val cancellationSignal = CancellationSignal()
    continuation.invokeOnCancellation { cancellationSignal.cancel() }
    try {
        LocationManagerCompat.getCurrentLocation(
            manager, provider, cancellationSignal, ContextCompat.getMainExecutor(context)
        ) { location -> if (continuation.isActive) continuation.resume(location) }
    } catch (_: SecurityException) {
        if (continuation.isActive) continuation.resume(null)
    }
}
