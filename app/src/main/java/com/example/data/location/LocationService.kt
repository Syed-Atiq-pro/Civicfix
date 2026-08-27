package com.example.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class UserLocationResult(
  val latitude: Double,
  val longitude: Double,
  val addressName: String,
  val isAccurate: Boolean = true
)

class LocationService(private val context: Context) {
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

  fun hasLocationPermission(): Boolean {
    val fineGranted = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseGranted = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return fineGranted || coarseGranted
  }

  @SuppressLint("MissingPermission")
  suspend fun getCurrentLocation(): UserLocationResult? = withContext(Dispatchers.IO) {
    if (!hasLocationPermission()) {
      return@withContext null
    }

    try {
      val cancellationTokenSource = CancellationTokenSource()
      val location: Location? = suspendCancellableCoroutine { continuation ->
        fusedLocationClient.getCurrentLocation(
          Priority.PRIORITY_HIGH_ACCURACY,
          cancellationTokenSource.token
        ).addOnSuccessListener { loc ->
          continuation.resume(loc)
        }.addOnFailureListener {
          // Fallback to last known location if getCurrentLocation fails
          fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
            continuation.resume(lastLoc)
          }.addOnFailureListener {
            continuation.resume(null)
          }
        }

        continuation.invokeOnCancellation {
          cancellationTokenSource.cancel()
        }
      }

      if (location != null) {
        val addressName = reverseGeocode(location.latitude, location.longitude)
        UserLocationResult(
          latitude = location.latitude,
          longitude = location.longitude,
          addressName = addressName,
          isAccurate = true
        )
      } else {
        null
      }
    } catch (e: Exception) {
      null
    }
  }

  private fun reverseGeocode(latitude: Double, longitude: Double): String {
    try {
      if (Geocoder.isPresent()) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          var result: List<Address>? = null
          geocoder.getFromLocation(latitude, longitude, 1) { addrs ->
            result = addrs
          }
          result
        } else {
          @Suppress("DEPRECATION")
          geocoder.getFromLocation(latitude, longitude, 1)
        }

        if (!addresses.isNullOrEmpty()) {
          val addr = addresses[0]
          val locality = addr.locality ?: addr.subLocality ?: addr.subAdminArea
          val street = addr.thoroughfare ?: addr.featureName
          return listOfNotNull(street, locality).joinToString(", ").ifBlank {
            String.format(Locale.US, "Lat: %.4f, Lng: %.4f", latitude, longitude)
          }
        }
      }
    } catch (_: Exception) {
      // Ignore geocoder exception and return formatted coordinates
    }
    return String.format(Locale.US, "GPS: %.4f, %.4f", latitude, longitude)
  }
}
