package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.location.UserLocationResult
import com.example.data.model.CivicIssueItem
import com.example.ui.theme.Cyan300
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.Orange400
import com.example.ui.theme.Orange500
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

/**
 * Google Maps Composable integrating the Google Maps SDK,
 * auto-centering on user coordinates once fetched, with custom markers for reported civic issues.
 */
@Composable
fun CivicGoogleMapView(
  modifier: Modifier = Modifier,
  issues: List<CivicIssueItem>,
  userLocation: UserLocationResult?,
  isLocating: Boolean = false,
  isLocationPermissionGranted: Boolean = false,
  onRequestLocationPermission: () -> Unit = {},
  onSelectIssue: (CivicIssueItem) -> Unit = {},
  initialZoom: Float = 14.5f
) {
  val defaultCenter = LatLng(17.3850, 78.4867) // Default City Grid
  val coroutineScope = rememberCoroutineScope()

  val cameraPositionState: CameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(
      userLocation?.let { LatLng(it.latitude, it.longitude) }
        ?: issues.firstOrNull()?.let { LatLng(it.latitude, it.longitude) }
        ?: defaultCenter,
      initialZoom
    )
  }

  var selectedMapType by remember { mutableStateOf(MapType.NORMAL) }
  var isTrafficEnabled by remember { mutableStateOf(true) }
  var selectedMarkerIssue by remember { mutableStateOf<CivicIssueItem?>(null) }

  // Auto-center camera on user location whenever a new userLocation is fetched
  LaunchedEffect(userLocation) {
    userLocation?.let { loc ->
      cameraPositionState.animate(
        update = CameraUpdateFactory.newLatLngZoom(
          LatLng(loc.latitude, loc.longitude),
          15.5f
        ),
        durationMs = 1000
      )
    }
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
      .testTag("civic_google_map_container")
  ) {
    // Google Map Canvas
    GoogleMap(
      modifier = Modifier.fillMaxSize(),
      cameraPositionState = cameraPositionState,
      properties = MapProperties(
        mapType = selectedMapType,
        isBuildingEnabled = true,
        isTrafficEnabled = isTrafficEnabled,
        isMyLocationEnabled = isLocationPermissionGranted
      ),
      uiSettings = MapUiSettings(
        zoomControlsEnabled = false,
        compassEnabled = true,
        myLocationButtonEnabled = false,
        mapToolbarEnabled = true
      )
    ) {
      // User Location Marker (if fetched)
      userLocation?.let { loc ->
        Marker(
          state = MarkerState(position = LatLng(loc.latitude, loc.longitude)),
          title = "📍 You are here",
          snippet = loc.addressName,
          icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        )
      }

      // Reported Civic Issue Hazard Markers
      issues.forEach { issue ->
        val markerHue = when (issue.severity.lowercase()) {
          "critical", "high" -> BitmapDescriptorFactory.HUE_RED
          "medium" -> BitmapDescriptorFactory.HUE_ORANGE
          else -> BitmapDescriptorFactory.HUE_YELLOW
        }

        Marker(
          state = MarkerState(position = LatLng(issue.latitude, issue.longitude)),
          title = "⚠️ ${issue.title}",
          snippet = "${issue.category} • ${issue.severity} • ${issue.municipalDepartment}",
          icon = BitmapDescriptorFactory.defaultMarker(markerHue),
          onClick = {
            selectedMarkerIssue = issue
            false // Show default info window while opening custom detail card
          }
        )
      }
    }

    // Top Header Layer: Map Layer Controls & Status
    Row(
      modifier = Modifier
        .align(Alignment.TopStart)
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Live Hazard Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(14.dp))
          .background(MidnightBackground.copy(alpha = 0.85f))
          .border(1.dp, Cyan400.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = Cyan400, modifier = Modifier.size(15.dp))
          Text(
            text = "${issues.size} Hazard Markers Active",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Slate50
          )
        }
      }

      // Layer Controls (Satellite & Traffic)
      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Traffic Toggle
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isTrafficEnabled) Indigo600 else MidnightBackground.copy(alpha = 0.85f))
            .border(1.dp, if (isTrafficEnabled) Cyan400 else GlassBorder, CircleShape)
            .clickable { isTrafficEnabled = !isTrafficEnabled }
            .testTag("toggle_traffic_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Default.Traffic,
            contentDescription = "Toggle Traffic",
            tint = if (isTrafficEnabled) Color.White else Slate400,
            modifier = Modifier.size(18.dp)
          )
        }

        // Map Type Toggle (Standard / Satellite)
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MidnightBackground.copy(alpha = 0.85f))
            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
            .padding(2.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (selectedMapType == MapType.NORMAL) Indigo600 else Color.Transparent)
              .clickable { selectedMapType = MapType.NORMAL }
              .padding(horizontal = 8.dp, vertical = 5.dp)
          ) {
            Text("Map", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate50)
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (selectedMapType == MapType.HYBRID) Indigo600 else Color.Transparent)
              .clickable { selectedMapType = MapType.HYBRID }
              .padding(horizontal = 8.dp, vertical = 5.dp)
          ) {
            Text("Sat", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate50)
          }
        }
      }
    }

    // Right Side: GPS Locate & Zoom Controls
    Column(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // GPS Center Button
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(MidnightBackground.copy(alpha = 0.9f))
          .border(1.dp, if (userLocation != null) Cyan400 else GlassBorder, CircleShape)
          .clickable {
            if (!isLocationPermissionGranted) {
              onRequestLocationPermission()
            } else if (userLocation != null) {
              coroutineScope.launch {
                cameraPositionState.animate(
                  CameraUpdateFactory.newLatLngZoom(
                    LatLng(userLocation.latitude, userLocation.longitude),
                    16f
                  ),
                  800
                )
              }
            } else {
              onRequestLocationPermission()
            }
          }
          .testTag("center_my_location_button"),
        contentAlignment = Alignment.Center
      ) {
        if (isLocating) {
          CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Cyan400, strokeWidth = 2.dp)
        } else {
          Icon(
            imageVector = if (userLocation != null) Icons.Default.GpsFixed else Icons.Default.MyLocation,
            contentDescription = "Center on My Location",
            tint = if (userLocation != null) Cyan400 else Slate300,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Zoom In
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(MidnightBackground.copy(alpha = 0.9f))
          .border(1.dp, GlassBorder, CircleShape)
          .clickable {
            coroutineScope.launch {
              cameraPositionState.animate(CameraUpdateFactory.zoomIn(), 400)
            }
          }
          .testTag("map_zoom_in_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Slate100, modifier = Modifier.size(18.dp))
      }

      // Zoom Out
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(MidnightBackground.copy(alpha = 0.9f))
          .border(1.dp, GlassBorder, CircleShape)
          .clickable {
            coroutineScope.launch {
              cameraPositionState.animate(CameraUpdateFactory.zoomOut(), 400)
            }
          }
          .testTag("map_zoom_out_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Slate100, modifier = Modifier.size(18.dp))
      }
    }

    // Bottom Selected Marker Issue Preview Card
    AnimatedVisibility(
      visible = selectedMarkerIssue != null,
      enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
      exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(12.dp)
    ) {
      selectedMarkerIssue?.let { issue ->
        val severityColor = when (issue.severity.lowercase()) {
          "critical" -> Rose500
          "high" -> Orange500
          "medium" -> Orange400
          else -> Cyan300
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MidnightBackground.copy(alpha = 0.95f))
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .clickable { onSelectIssue(issue) }
            .padding(12.dp)
            .testTag("map_selected_issue_card")
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Thumbnail
            AsyncImage(
              model = issue.imageUrl,
              contentDescription = issue.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Slate800)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Issue Info
            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(severityColor.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = issue.severity.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = severityColor
                  )
                }
                Text(
                  text = issue.category,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = Slate300
                )
              }

              Spacer(modifier = Modifier.height(2.dp))

              Text(
                text = issue.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Slate50,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )

              Text(
                text = issue.locationName,
                fontSize = 11.sp,
                color = Slate400,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Details Button
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Indigo600)
                .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
              Text(
                text = "View Ticket",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
    }
  }
}
