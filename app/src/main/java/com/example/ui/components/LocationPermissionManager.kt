package com.example.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Emerald400
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.Orange400
import com.example.ui.theme.Orange500
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate800

/**
 * State holder managing Android runtime location permission with rationale and permanent denial handling.
 */
@Stable
class LocationPermissionState(
  val context: Context,
  val onPermissionGranted: () -> Unit
) {
  var isGranted by mutableStateOf(checkPermissionGranted(context))
    internal set

  var showRationaleDialog by mutableStateOf(false)
  var showSettingsDialog by mutableStateOf(false)

  internal var requestLauncher: (() -> Unit)? = null

  fun requestLocationPermission() {
    if (checkPermissionGranted(context)) {
      isGranted = true
      onPermissionGranted()
      return
    }

    val activity = context.findActivity()
    val shouldShowFineRationale = activity?.let {
      ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_FINE_LOCATION)
    } ?: false

    val shouldShowCoarseRationale = activity?.let {
      ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_COARSE_LOCATION)
    } ?: false

    if (shouldShowFineRationale || shouldShowCoarseRationale) {
      showRationaleDialog = true
    } else {
      requestLauncher?.invoke()
    }
  }

  fun launchSystemSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
      data = Uri.fromParts("package", context.packageName, null)
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
    showSettingsDialog = false
  }

  companion object {
    fun checkPermissionGranted(context: Context): Boolean {
      val fine = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED

      val coarse = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED

      return fine || coarse
    }
  }
}

private fun Context.findActivity(): Activity? {
  var currentContext = this
  while (currentContext is ContextWrapper) {
    if (currentContext is Activity) return currentContext
    currentContext = currentContext.baseContext
  }
  return null
}

/**
 * Compose-compatible permission request manager hook.
 */
@Composable
fun rememberLocationPermissionState(
  onPermissionGranted: () -> Unit = {}
): LocationPermissionState {
  val context = LocalContext.current
  val permissionState = remember(context) {
    LocationPermissionState(context, onPermissionGranted)
  }

  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

    if (fineGranted || coarseGranted) {
      permissionState.isGranted = true
      permissionState.showRationaleDialog = false
      permissionState.showSettingsDialog = false
      permissionState.onPermissionGranted()
    } else {
      permissionState.isGranted = false
      val activity = context.findActivity()
      val fineRationale = activity?.let {
        ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_FINE_LOCATION)
      } ?: false
      val coarseRationale = activity?.let {
        ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_COARSE_LOCATION)
      } ?: false

      if (!fineRationale && !coarseRationale) {
        // User checked "Don't ask again" or permanently denied
        permissionState.showSettingsDialog = true
      } else {
        permissionState.showRationaleDialog = true
      }
    }
  }

  permissionState.requestLauncher = {
    launcher.launch(
      arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
      )
    )
  }

  return permissionState
}

/**
 * Educational Rationale Dialog explaining why location coordinates are required for civic reporting.
 */
@Composable
fun LocationRationaleDialog(
  onProceed: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .background(MidnightBackground)
        .border(1.dp, Cyan400.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
        .padding(20.dp)
        .testTag("location_rationale_dialog")
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Icon header
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Cyan500.copy(alpha = 0.15f))
            .border(1.dp, Cyan400.copy(alpha = 0.4f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.GpsFixed,
            contentDescription = null,
            tint = Cyan400,
            modifier = Modifier.size(28.dp)
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Enable Precise Location",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Slate50
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "CivicFix AI utilizes Play Services GPS to pinpoint municipal hazards with high accuracy.",
            fontSize = 12.sp,
            color = Slate300,
            lineHeight = 16.sp
          )
        }

        // Feature Rationale Badges
        Column(
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          RationaleFeatureRow(
            icon = Icons.Default.NearMe,
            title = "Sub-Meter Geocoding",
            description = "Directs municipal road and drainage teams to the exact hazard spot."
          )
          RationaleFeatureRow(
            icon = Icons.Default.CheckCircle,
            title = "Verified AI Validation",
            description = "Google Gemini cross-references location data to prevent duplicate tickets."
          )
          RationaleFeatureRow(
            icon = Icons.Default.Security,
            title = "Citizen Privacy First",
            description = "Coordinates are only collected when actively creating a report or opening the map."
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Action Buttons
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          GradientButton(
            text = "Grant Location Permission",
            onClick = onProceed,
            modifier = Modifier.fillMaxWidth(),
            testTag = "grant_location_permission_button"
          )

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(GlassSurface)
              .clickable { onDismiss() }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Continue with Manual Pinning",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = Slate400
            )
          }
        }
      }
    }
  }
}

/**
 * Denied Settings Dialog for users who permanently denied permission.
 */
@Composable
fun LocationSettingsDialog(
  onOpenSettings: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .background(MidnightBackground)
        .border(1.dp, Orange500.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
        .padding(20.dp)
        .testTag("location_settings_dialog")
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Warning Icon
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Orange500.copy(alpha = 0.15f))
            .border(1.dp, Orange500.copy(alpha = 0.4f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.LocationOff,
            contentDescription = null,
            tint = Orange400,
            modifier = Modifier.size(28.dp)
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Location Access Needed",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Slate50
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Location permission is currently blocked. To automatically capture coordinates for issues, please enable Location in Android App Settings.",
            fontSize = 12.sp,
            color = Slate300,
            lineHeight = 16.sp
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Action Buttons
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(Indigo600)
              .clickable { onOpenSettings() }
              .padding(vertical = 12.dp)
              .testTag("open_app_settings_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
              Text(
                text = "Open App Settings",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(GlassSurface)
              .clickable { onDismiss() }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Dismiss & Enter Location Manually",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = Slate400
            )
          }
        }
      }
    }
  }
}

@Composable
private fun RationaleFeatureRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  description: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(GlassSurface)
      .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(Indigo600.copy(alpha = 0.3f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = Cyan400, modifier = Modifier.size(16.dp))
    }
    Column(modifier = Modifier.weight(1f)) {
      Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate50)
      Text(description, fontSize = 10.sp, color = Slate400, lineHeight = 13.sp)
    }
  }
}
