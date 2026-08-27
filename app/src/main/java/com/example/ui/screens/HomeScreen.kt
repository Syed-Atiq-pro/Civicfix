package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.location.UserLocationResult
import com.example.data.model.ChatMessage
import com.example.data.model.CivicIssueItem
import com.example.data.model.CivicUser
import com.example.data.model.MessageSender
import com.example.ui.components.AmbientOrbsBackground
import com.example.ui.components.CivicGoogleMapView
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassDarkBadge
import com.example.ui.components.GradientButton
import com.example.ui.components.LocationRationaleDialog
import com.example.ui.components.LocationSettingsDialog
import com.example.ui.components.rememberLocationPermissionState
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.theme.Cyan300
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassCardShadow
import com.example.ui.theme.GlassDarkBorder
import com.example.ui.theme.GlassDarkSurface
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.Orange400
import com.example.ui.theme.Orange500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.viewmodel.CivicViewModel

enum class NavTab {
  HOME, MAP, AI_ASSISTANT, ALERTS, PROFILE
}

enum class AuthScreenState {
  NONE,
  LOGIN,
  REGISTER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  modifier: Modifier = Modifier,
  viewModel: CivicViewModel = viewModel()
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableStateOf(NavTab.HOME) }
  var authScreenState by remember { mutableStateOf(AuthScreenState.NONE) }
  var showReportSheet by remember { mutableStateOf(false) }
  var showGoogleAuthSheet by remember { mutableStateOf(false) }
  var selectedIssueForDetail by remember { mutableStateOf<CivicIssueItem?>(null) }

  val issues by viewModel.issues.collectAsState()
  val user by viewModel.user.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val chatMessages by viewModel.chatMessages.collectAsState()
  val isAiThinking by viewModel.isAiThinking.collectAsState()
  val isAnalyzingIssue by viewModel.isAnalyzingIssue.collectAsState()
  val aiAnalysisResult by viewModel.aiAnalysisResult.collectAsState()
  val userLocation by viewModel.userLocation.collectAsState()
  val isLocating by viewModel.isLocating.collectAsState()
  val isAuthLoading by viewModel.isAuthLoading.collectAsState()
  val authErrorMessage by viewModel.authErrorMessage.collectAsState()

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  // Render Login Screen if user selected Login
  if (authScreenState == AuthScreenState.LOGIN) {
    LoginScreen(
      isLoading = isAuthLoading,
      errorMessage = authErrorMessage,
      onSignInEmail = { email, pass ->
        viewModel.signInWithEmail(
          email = email,
          pass = pass,
          onSuccess = { authScreenState = AuthScreenState.NONE }
        )
      },
      onSignInGoogle = {
        viewModel.signInWithGoogleCredentialManager(
          activityContext = context,
          onSuccess = { authScreenState = AuthScreenState.NONE }
        )
      },
      onNavigateToRegister = {
        viewModel.clearAuthError()
        authScreenState = AuthScreenState.REGISTER
      },
      onContinueAsGuest = {
        viewModel.clearAuthError()
        authScreenState = AuthScreenState.NONE
      },
      onClearError = { viewModel.clearAuthError() },
      onBack = {
        viewModel.clearAuthError()
        authScreenState = AuthScreenState.NONE
      }
    )
    return
  }

  // Render Register Screen if user selected Register
  if (authScreenState == AuthScreenState.REGISTER) {
    RegisterScreen(
      isLoading = isAuthLoading,
      errorMessage = authErrorMessage,
      onRegisterEmail = { name, email, pass, _ ->
        viewModel.registerWithEmail(
          email = email,
          pass = pass,
          name = name,
          onSuccess = { authScreenState = AuthScreenState.NONE }
        )
      },
      onSignInGoogle = {
        viewModel.signInWithGoogleCredentialManager(
          activityContext = context,
          onSuccess = { authScreenState = AuthScreenState.NONE }
        )
      },
      onNavigateToLogin = {
        viewModel.clearAuthError()
        authScreenState = AuthScreenState.LOGIN
      },
      onClearError = { viewModel.clearAuthError() },
      onBack = {
        viewModel.clearAuthError()
        authScreenState = AuthScreenState.NONE
      }
    )
    return
  }

  AmbientOrbsBackground(modifier = modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .statusBarsPadding()
      ) {
        // Top App Header with Google Profile Badge
        HeaderSection(
          user = user,
          onProfileClick = { showGoogleAuthSheet = true }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Main View Port
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
          when (selectedTab) {
            NavTab.HOME -> {
              HomeDashboardContent(
                user = user,
                selectedCategory = selectedCategory,
                onSelectCategory = { viewModel.setCategory(it) },
                issues = issues.filter {
                  selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
                },
                onUpvote = { viewModel.upvoteIssue(it) },
                onReportClick = { showReportSheet = true },
                onIssueClick = { selectedIssueForDetail = it },
                onOpenAiAssistant = { selectedTab = NavTab.AI_ASSISTANT }
              )
            }
            NavTab.MAP -> {
              MapPreviewTab(
                issues = issues,
                userLocation = userLocation,
                isLocating = isLocating,
                onFetchLocation = { onResult -> viewModel.fetchCurrentLocation(onResult) },
                onSelectIssue = { selectedIssueForDetail = it }
              )
            }
            NavTab.AI_ASSISTANT -> {
              GeminiChatAssistantTab(
                messages = chatMessages,
                isThinking = isAiThinking,
                onSendMessage = { viewModel.sendChatMessageToGemini(it) }
              )
            }
            NavTab.ALERTS -> {
              AlertsTab(
                issues = issues,
                onClaimReward = { viewModel.upvoteIssue(issues.firstOrNull()?.id ?: "") }
              )
            }
            NavTab.PROFILE -> {
              ProfileTab(
                user = user,
                onOpenAuthSheet = { showGoogleAuthSheet = true },
                onOpenLogin = { authScreenState = AuthScreenState.LOGIN },
                onOpenRegister = { authScreenState = AuthScreenState.REGISTER },
                onSignOut = { viewModel.signOutUser() }
              )
            }
          }
        }

        // Frosted Glass Bottom Navigation Bar with 5 Tabs
        GlassBottomNavigationBar(
          currentTab = selectedTab,
          onTabSelected = { selectedTab = it }
        )
      }

      // 1. New Report Sheet with Real-time Google Gemini 3.5 Flash Auto-Analysis
      if (showReportSheet) {
        ModalBottomSheet(
          onDismissRequest = {
            showReportSheet = false
            viewModel.clearAiAnalysis()
          },
          sheetState = sheetState,
          containerColor = MidnightBackground.copy(alpha = 0.96f),
          scrimColor = Color.Black.copy(alpha = 0.65f),
          dragHandle = {
            Box(
              modifier = Modifier
                .padding(vertical = 12.dp)
                .width(48.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(Slate700)
            )
          }
        ) {
          NewReportSheetContent(
            isAnalyzing = isAnalyzingIssue,
            analysisResult = aiAnalysisResult,
            userLocation = userLocation,
            isLocating = isLocating,
            onFetchLocation = { onResult -> viewModel.fetchCurrentLocation(onResult) },
            onTriggerGeminiAnalysis = { rawText, cat ->
              viewModel.analyzeIssueWithGemini(rawText, cat)
            },
            onClose = {
              showReportSheet = false
              viewModel.clearAiAnalysis()
            },
            onSubmit = { title, category, desc, severity, dept, loc, lat, lng ->
              viewModel.submitReport(title, category, desc, severity, dept, loc, lat, lng)
              showReportSheet = false
            }
          )
        }
      }

      // 2. Google Sign-In & Citizen Account Sheet
      if (showGoogleAuthSheet) {
        ModalBottomSheet(
          onDismissRequest = { showGoogleAuthSheet = false },
          sheetState = sheetState,
          containerColor = MidnightBackground.copy(alpha = 0.96f),
          scrimColor = Color.Black.copy(alpha = 0.65f),
          dragHandle = {
            Box(
              modifier = Modifier
                .padding(vertical = 12.dp)
                .width(48.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(Slate700)
            )
          }
        ) {
          GoogleAuthSheetContent(
            user = user,
            onSignInGoogle = {
              viewModel.signInWithGoogleCredentialManager(
                activityContext = context,
                onSuccess = { showGoogleAuthSheet = false }
              )
            },
            onOpenLogin = {
              showGoogleAuthSheet = false
              authScreenState = AuthScreenState.LOGIN
            },
            onOpenRegister = {
              showGoogleAuthSheet = false
              authScreenState = AuthScreenState.REGISTER
            },
            onSignOut = {
              viewModel.signOutUser()
              showGoogleAuthSheet = false
            },
            onClose = { showGoogleAuthSheet = false }
          )
        }
      }

      // 3. Issue Detail Inspector Modal
      if (selectedIssueForDetail != null) {
        val detail = selectedIssueForDetail!!
        ModalBottomSheet(
          onDismissRequest = { selectedIssueForDetail = null },
          sheetState = sheetState,
          containerColor = MidnightBackground.copy(alpha = 0.96f),
          scrimColor = Color.Black.copy(alpha = 0.65f),
          dragHandle = {
            Box(
              modifier = Modifier
                .padding(vertical = 12.dp)
                .width(48.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(Slate700)
            )
          }
        ) {
          IssueDetailContent(
            issue = detail,
            onUpvote = {
              viewModel.upvoteIssue(detail.id)
              selectedIssueForDetail = detail.copy(upvotes = detail.upvotes + 1)
            },
            onClose = { selectedIssueForDetail = null }
          )
        }
      }
    }
  }
}

/**
 * Top App Header with App Name, Subtitle, and Google Avatar / Points Badge
 */
@Composable
fun HeaderSection(
  user: CivicUser,
  onProfileClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = "CivicFix AI",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = (-0.5).sp,
          color = Slate50
        )
        // Gemini powered badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Indigo600.copy(alpha = 0.3f))
            .border(1.dp, Cyan400.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "Gemini 3.5",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Cyan300
          )
        }
      }
      Text(
        text = if (user.isGoogleAuthenticated) "Welcome, ${user.name.split(" ").firstOrNull() ?: "Citizen"}" else "Guest Mode (Sign in with Google)",
        fontSize = 12.sp,
        color = Slate400
      )
    }

    // Google User / Points Badge
    GlassCard(
      shape = RoundedCornerShape(18.dp),
      onClick = onProfileClick,
      modifier = Modifier.testTag("google_profile_header_badge")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Gradient Google Avatar
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
              Brush.linearGradient(
                colors = listOf(Indigo500, Cyan400)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = user.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.White
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.padding(end = 4.dp)) {
          Text(
            text = "POINTS",
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate400,
            letterSpacing = 1.sp
          )
          Text(
            text = String.format("%,d", user.points),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Cyan400
          )
        }
      }
    }
  }
}

/**
 * Home Dashboard View
 */
@Composable
fun HomeDashboardContent(
  user: CivicUser,
  selectedCategory: String,
  onSelectCategory: (String) -> Unit,
  issues: List<CivicIssueItem>,
  onUpvote: (String) -> Unit,
  onReportClick: () -> Unit,
  onIssueClick: (CivicIssueItem) -> Unit,
  onOpenAiAssistant: () -> Unit
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Stats Row
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        GlassCard(
          modifier = Modifier
            .weight(1f)
            .testTag("stat_my_reports"),
          shape = RoundedCornerShape(24.dp)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(text = "My Reports", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate400)
            Text(text = "${user.reportsCount}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Slate50)
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(Indigo500.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(text = "${user.resolvedCount} Resolved", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Indigo400)
            }
          }
        }

        GlassCard(
          modifier = Modifier
            .weight(1f)
            .testTag("stat_nearby_issues"),
          shape = RoundedCornerShape(24.dp)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(text = "Active Issues", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate400)
            Text(text = "${issues.size}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Slate50)
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(Emerald500.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(text = "Live Sync", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Emerald400)
            }
          }
        }
      }
    }

    // 2. Google Gemini AI Banner Card
    item {
      GlassCard(
        shape = RoundedCornerShape(22.dp),
        backgroundColor = GlassSurface,
        onClick = onOpenAiAssistant,
        modifier = Modifier.testTag("gemini_ai_assistant_banner")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(
                Brush.linearGradient(
                  colors = listOf(Indigo600, Cyan400)
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Gemini AI",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Google Gemini Civic Helpdesk",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Slate50
            )
            Text(
              text = "Ask questions, track municipal bylaws & get AI issue diagnosis",
              fontSize = 11.sp,
              color = Slate300,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          Icon(
            imageVector = Icons.Default.SmartToy,
            contentDescription = null,
            tint = Cyan400,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // 3. Category Filter Chips
    item {
      val categories = listOf("All", "Roads", "Waste", "Drainage", "Lighting")
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(categories) { cat ->
          val isSelected = selectedCategory == cat
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(if (isSelected) Indigo600 else GlassSurface)
              .border(
                1.dp,
                if (isSelected) Cyan400.copy(alpha = 0.6f) else GlassBorder,
                RoundedCornerShape(14.dp)
              )
              .clickable { onSelectCategory(cat) }
              .padding(horizontal = 14.dp, vertical = 7.dp)
              .testTag("category_${cat.lowercase()}")
          ) {
            Text(
              text = cat,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Slate50 else Slate400
            )
          }
        }
      }
    }

    // 4. Featured Hero Issue Card
    if (issues.isNotEmpty()) {
      item {
        val featured = issues.first()
        FeaturedIssueCard(
          issue = featured,
          onUpvote = { onUpvote(featured.id) },
          onClick = { onIssueClick(featured) }
        )
      }
    }

    // 5. Action Gradient Button
    item {
      GradientButton(
        text = "Report New Issue (AI Assisted)",
        onClick = onReportClick,
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Slate50,
            modifier = Modifier.size(20.dp)
          )
        }
      )
    }

    // 6. Community Reports List
    if (issues.size > 1) {
      item {
        Text(
          text = "Community Reports Nearby",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Slate50,
          modifier = Modifier.padding(top = 4.dp)
        )
      }

      items(issues.drop(1), key = { it.id }) { issue ->
        SecondaryIssueCard(
          issue = issue,
          onUpvote = { onUpvote(issue.id) },
          onClick = { onIssueClick(issue) }
        )
      }
    }
  }
}

/**
 * Featured Hero Issue Card
 */
@Composable
fun FeaturedIssueCard(
  issue: CivicIssueItem,
  onUpvote: () -> Unit,
  onClick: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_badge")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  GlassCard(
    shape = RoundedCornerShape(30.dp),
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("featured_issue_card")
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Banner Image with Gradient
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(140.dp)
          .background(Slate800)
      ) {
        AsyncImage(
          model = issue.imageUrl,
          contentDescription = issue.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
        )

        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, MidnightBackground.copy(alpha = 0.85f))
              )
            )
        )

        // Status Badge Top Right
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(12.dp)
        ) {
          GlassDarkBadge(
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Orange500.copy(alpha = pulseAlpha))
              )
              Text(
                text = issue.status.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Orange400,
                letterSpacing = 0.5.sp
              )
            }
          }
        }
      }

      // Content Body
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = issue.title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Slate50,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = issue.timeAgo,
            fontSize = 11.sp,
            color = Slate400
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = issue.description,
          fontSize = 13.sp,
          color = Slate300,
          lineHeight = 18.sp,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(14.dp))

        // AI Confidence Pill + Upvote Action
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          GlassDarkBadge(
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Column {
              Text(
                text = "GEMINI AI CONF.",
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate400,
                letterSpacing = 0.8.sp
              )
              Text(
                text = "${issue.aiAccuracy}% Accuracy",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Cyan400
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(GlassDarkSurface)
              .border(1.dp, GlassDarkBorder, RoundedCornerShape(20.dp))
              .clickable(onClick = onUpvote)
              .padding(horizontal = 10.dp, vertical = 6.dp)
              .testTag("upvote_featured_btn")
          ) {
            Icon(
              imageVector = Icons.Default.ThumbUp,
              contentDescription = "Upvote",
              tint = Cyan400,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "+${issue.upvotes} Up",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Slate100
            )
          }
        }
      }
    }
  }
}

/**
 * Secondary list item card for community reports
 */
@Composable
fun SecondaryIssueCard(
  issue: CivicIssueItem,
  onUpvote: () -> Unit,
  onClick: () -> Unit
) {
  GlassCard(
    shape = RoundedCornerShape(20.dp),
    onClick = onClick,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      AsyncImage(
        model = issue.imageUrl,
        contentDescription = issue.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(56.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Slate800)
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = issue.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Slate50,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          if (issue.isVerified) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Verified",
              tint = Emerald400,
              modifier = Modifier.size(14.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Slate500,
            modifier = Modifier.size(12.dp)
          )
          Text(
            text = issue.locationName,
            fontSize = 11.sp,
            color = Slate400,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Box(
        modifier = Modifier
          .clip(CircleShape)
          .background(Indigo600.copy(alpha = 0.25f))
          .clickable(onClick = onUpvote)
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Text(
          text = "+${issue.upvotes}",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Cyan300
        )
      }
    }
  }
}

/**
 * Real-time Google Gemini AI Civic Assistant Tab
 */
@Composable
fun GeminiChatAssistantTab(
  messages: List<ChatMessage>,
  isThinking: Boolean,
  onSendMessage: (String) -> Unit
) {
  var inputQuery by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 6.dp)
  ) {
    // Header Banner
    GlassCard(
      shape = RoundedCornerShape(18.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Indigo600),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text("Gemini 3.5 Flash Civic Intelligence", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate50)
          Text("Official municipal rules, grievance tracking & point rewards", fontSize = 10.sp, color = Slate400)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Quick Prompts
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      val samplePrompts = listOf(
        "How fast are potholes repaired?",
        "How many points for reporting waste?",
        "What is illegal dumping penalty?",
        "How to track my complaint?"
      )
      items(samplePrompts) { prompt ->
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GlassSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
            .clickable { onSendMessage(prompt) }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(prompt, fontSize = 11.sp, color = Cyan300)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Messages List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        val isUser = msg.sender == MessageSender.USER
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
          Box(
            modifier = Modifier
              .widthIn(max = 280.dp)
              .clip(
                RoundedCornerShape(
                  topStart = 16.dp,
                  topEnd = 16.dp,
                  bottomStart = if (isUser) 16.dp else 4.dp,
                  bottomEnd = if (isUser) 4.dp else 16.dp
                )
              )
              .background(if (isUser) Indigo600 else GlassSurface)
              .border(
                1.dp,
                if (isUser) Cyan400.copy(alpha = 0.5f) else GlassBorder,
                RoundedCornerShape(16.dp)
              )
              .padding(12.dp)
          ) {
            Text(
              text = msg.text,
              fontSize = 13.sp,
              color = if (isUser) Slate50 else Slate100,
              lineHeight = 18.sp
            )
          }
        }
      }

      if (isThinking) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(GlassSurface)
                .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = Cyan400,
                  strokeWidth = 2.dp
                )
                Text("Gemini is thinking...", fontSize = 12.sp, color = Slate400)
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Query Input Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedTextField(
        value = inputQuery,
        onValueChange = { inputQuery = it },
        placeholder = { Text("Ask Gemini a civic question...", fontSize = 12.sp) },
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Slate50,
          unfocusedTextColor = Slate100,
          focusedBorderColor = Cyan400,
          unfocusedBorderColor = GlassBorder,
          focusedContainerColor = GlassSurface,
          unfocusedContainerColor = GlassSurface
        )
      )

      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(Brush.linearGradient(listOf(Indigo600, Cyan500)))
          .clickable {
            if (inputQuery.isNotBlank()) {
              val text = inputQuery
              inputQuery = ""
              onSendMessage(text)
            }
          }
          .testTag("send_chat_query_btn"),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
      }
    }
  }
}

/**
 * Interactive Google Maps Live View
 */
@Composable
fun MapPreviewTab(
  issues: List<CivicIssueItem>,
  userLocation: UserLocationResult?,
  isLocating: Boolean,
  onFetchLocation: ((UserLocationResult?) -> Unit) -> Unit,
  onSelectIssue: (CivicIssueItem) -> Unit
) {
  val permissionState = rememberLocationPermissionState(
    onPermissionGranted = {
      onFetchLocation { /* updates userLocation */ }
    }
  )

  if (permissionState.showRationaleDialog) {
    LocationRationaleDialog(
      onProceed = {
        permissionState.showRationaleDialog = false
        permissionState.requestLauncher?.invoke()
      },
      onDismiss = {
        permissionState.showRationaleDialog = false
      }
    )
  }

  if (permissionState.showSettingsDialog) {
    LocationSettingsDialog(
      onOpenSettings = {
        permissionState.launchSystemSettings()
      },
      onDismiss = {
        permissionState.showSettingsDialog = false
      }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    CivicGoogleMapView(
      modifier = Modifier.fillMaxSize(),
      issues = issues,
      userLocation = userLocation,
      isLocating = isLocating,
      isLocationPermissionGranted = permissionState.isGranted,
      onRequestLocationPermission = {
        if (!permissionState.isGranted) {
          permissionState.requestLocationPermission()
        } else {
          onFetchLocation { /* updates userLocation */ }
        }
      },
      onSelectIssue = onSelectIssue
    )
  }
}

/**
 * Notifications and Municipal Alerts View
 */
@Composable
fun AlertsTab(
  issues: List<CivicIssueItem>,
  onClaimReward: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Text(
        text = "Municipal Updates & AI Alerts",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Slate50
      )
    }

    item {
      GlassCard(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Emerald500.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald400, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
              Text("Report Verified (+50 Pts)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate50)
              Text("Municipal PWD inspector verified 'Large Pothole' on Main Street.", fontSize = 12.sp, color = Slate400)
            }
          }
        }
      }
    }

    item {
      GlassCard(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Cyan500.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Notifications, contentDescription = null, tint = Cyan400, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
              Text("Sanitation Truck Dispatched", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate50)
              Text("Solid Waste Management has assigned Truck #14 to Oakridge Park Ave.", fontSize = 12.sp, color = Slate400)
            }
          }
        }
      }
    }
  }
}

/**
 * Citizen Profile Tab
 */
@Composable
fun ProfileTab(
  user: CivicUser,
  onOpenAuthSheet: () -> Unit,
  onOpenLogin: () -> Unit,
  onOpenRegister: () -> Unit,
  onSignOut: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    GlassCard(
      shape = RoundedCornerShape(28.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
              Brush.linearGradient(colors = listOf(Indigo500, Cyan400))
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = user.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = user.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate50)
        Text(text = user.email, fontSize = 12.sp, color = Slate400)

        Spacer(modifier = Modifier.height(8.dp))

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Indigo600.copy(alpha = 0.3f))
            .border(1.dp, Cyan400.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(text = user.level, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Cyan300)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${user.points}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Slate50)
            Text(text = "Civic Points", fontSize = 11.sp, color = Slate400)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${user.reportsCount}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Slate50)
            Text(text = "Total Reports", fontSize = 11.sp, color = Slate400)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Top 5%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Emerald400)
            Text(text = "Leaderboard", fontSize = 11.sp, color = Slate400)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Auth management buttons
        if (user.isGoogleAuthenticated) {
          ElevatedButton(
            onClick = onSignOut,
            colors = ButtonDefaults.elevatedButtonColors(
              containerColor = Slate800,
              contentColor = Orange400
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("profile_sign_out_button")
          ) {
            Text("Sign Out (${user.email})")
          }
        } else {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            ElevatedButton(
              onClick = onOpenLogin,
              colors = ButtonDefaults.elevatedButtonColors(
                containerColor = Indigo600,
                contentColor = Slate50
              ),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("profile_login_button")
            ) {
              Text("Sign In")
            }
            ElevatedButton(
              onClick = onOpenRegister,
              colors = ButtonDefaults.elevatedButtonColors(
                containerColor = GlassSurface,
                contentColor = Cyan400
              ),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("profile_register_button")
            ) {
              Text("Register")
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ElevatedButton(
          onClick = onOpenAuthSheet,
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = GlassSurface,
            contentColor = Slate50
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_manage_account_button")
        ) {
          Text("Manage Google Citizen Account")
        }
      }
    }
  }
}

/**
 * New Report Sheet with Real-time Google Gemini 3.5 Flash Auto-Analysis & GPS Location Tracking
 */
@Composable
fun NewReportSheetContent(
  isAnalyzing: Boolean,
  analysisResult: com.example.data.model.CivicAiAnalysisResult?,
  userLocation: UserLocationResult?,
  isLocating: Boolean,
  onFetchLocation: ((UserLocationResult?) -> Unit) -> Unit,
  onTriggerGeminiAnalysis: (String, String) -> Unit,
  onClose: () -> Unit,
  onSubmit: (String, String, String, String, String, String, Double?, Double?) -> Unit
) {
  val context = LocalContext.current
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Roads") }
  var description by remember { mutableStateOf("") }
  var severity by remember { mutableStateOf("High") }
  var department by remember { mutableStateOf("Public Works Department (PWD)") }
  var locationName by remember { mutableStateOf(userLocation?.addressName ?: "Sector 4, City Center") }
  var detectedLatitude by remember { mutableStateOf<Double?>(userLocation?.latitude) }
  var detectedLongitude by remember { mutableStateOf<Double?>(userLocation?.longitude) }

  val permissionState = rememberLocationPermissionState(
    onPermissionGranted = {
      onFetchLocation { loc ->
        loc?.let {
          locationName = it.addressName
          detectedLatitude = it.latitude
          detectedLongitude = it.longitude
        }
      }
    }
  )

  if (permissionState.showRationaleDialog) {
    LocationRationaleDialog(
      onProceed = {
        permissionState.showRationaleDialog = false
        permissionState.requestLauncher?.invoke()
      },
      onDismiss = {
        permissionState.showRationaleDialog = false
      }
    )
  }

  if (permissionState.showSettingsDialog) {
    LocationSettingsDialog(
      onOpenSettings = {
        permissionState.launchSystemSettings()
      },
      onDismiss = {
        permissionState.showSettingsDialog = false
      }
    )
  }

  // Update fields when Gemini analysis arrives
  LaunchedEffect(analysisResult) {
    analysisResult?.let {
      title = it.title
      category = it.category
      severity = it.severity
      department = it.department
      description = it.formalDescription
    }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 8.dp)
      .navigationBarsPadding(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text("Report Civic Problem", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate50)
        Text("Google Gemini AI & Play Services GPS Location", fontSize = 11.sp, color = Slate400)
      }
      IconButton(onClick = onClose) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
      }
    }

    // AI Photo & Smart Analysis Trigger Area
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(90.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(GlassSurface)
        .border(1.dp, if (isAnalyzing) Cyan400 else GlassBorder, RoundedCornerShape(20.dp))
        .clickable {
          onTriggerGeminiAnalysis(
            if (description.isNotBlank()) description else "Severe deep pothole and cavity on main tarmac, blocking lane traffic",
            category
          )
        },
      contentAlignment = Alignment.Center
    ) {
      if (isAnalyzing) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Cyan400, strokeWidth = 2.dp)
          Text("Gemini 3.5 Flash Analyzing Hazard...", fontSize = 12.sp, color = Cyan300, fontWeight = FontWeight.Bold)
        }
      } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Cyan400, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.height(4.dp))
          Text("Tap for Instant Google Gemini AI Analysis", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate100)
          Text("Auto-detects severity, department & estimated fix time", fontSize = 10.sp, color = Slate400)
        }
      }
    }

    // AI Detected Summary Badge
    AnimatedVisibility(visible = analysisResult != null) {
      analysisResult?.let { res ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Indigo600.copy(alpha = 0.25f))
            .border(1.dp, Cyan400.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(10.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("AI Confidence: ${res.aiConfidence}% • Assigned: ${res.department}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Cyan300)
            Text("Estimated Resolution: ${res.estimatedResolutionTime}", fontSize = 10.sp, color = Slate300)
          }
        }
      }
    }

    // GPS Location Tracking Card (Google Play Services Location API)
    GlassCard(
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (detectedLatitude != null) Emerald500.copy(alpha = 0.2f) else Cyan500.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (detectedLatitude != null) Icons.Default.GpsFixed else Icons.Default.LocationOn,
              contentDescription = null,
              tint = if (detectedLatitude != null) Emerald400 else Cyan400,
              modifier = Modifier.size(18.dp)
            )
          }
          Column {
            Text(
              text = if (detectedLatitude != null) "GPS Coordinates Locked" else "Hazard Location",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (detectedLatitude != null) Emerald400 else Slate400
            )
            Text(
              text = if (detectedLatitude != null && detectedLongitude != null) {
                String.format(java.util.Locale.US, "%.4f° N, %.4f° E", detectedLatitude, detectedLongitude)
              } else {
                locationName
              },
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = Slate100,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        // GPS Fetch Trigger Button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (detectedLatitude != null) Emerald500.copy(alpha = 0.25f) else Indigo600)
            .clickable {
              if (!permissionState.isGranted) {
                permissionState.requestLocationPermission()
              } else {
                onFetchLocation { loc ->
                  loc?.let {
                    locationName = it.addressName
                    detectedLatitude = it.latitude
                    detectedLongitude = it.longitude
                  }
                }
              }
            }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          if (isLocating) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
          } else {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.MyLocation, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
              Text(
                text = if (detectedLatitude != null) "Re-detect" else "Detect GPS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
    }

    // Title Input
    OutlinedTextField(
      value = title,
      onValueChange = { title = it },
      label = { Text("Issue Title") },
      placeholder = { Text("e.g. Broken water pipeline") },
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Slate50,
        unfocusedTextColor = Slate100,
        focusedBorderColor = Cyan400,
        unfocusedBorderColor = GlassBorder,
        focusedLabelColor = Cyan400,
        unfocusedLabelColor = Slate400
      )
    )

    // Description Input
    OutlinedTextField(
      value = description,
      onValueChange = { description = it },
      label = { Text("Description & Landmarks") },
      placeholder = { Text("Details for municipal field crew...") },
      modifier = Modifier.fillMaxWidth(),
      minLines = 2,
      shape = RoundedCornerShape(16.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Slate50,
        unfocusedTextColor = Slate100,
        focusedBorderColor = Cyan400,
        unfocusedBorderColor = GlassBorder,
        focusedLabelColor = Cyan400,
        unfocusedLabelColor = Slate400
      )
    )

    // Submit Button
    GradientButton(
      text = "Submit Report (Earn +20 Pts)",
      onClick = {
        if (title.isNotBlank()) {
          onSubmit(title, category, description, severity, department, locationName, detectedLatitude, detectedLongitude)
        }
      },
      testTag = "submit_report_button"
    )

    Spacer(modifier = Modifier.height(6.dp))
  }
}

/**
 * Google Sign-In & Citizen Account Sheet
 */
@Composable
fun GoogleAuthSheetContent(
  user: CivicUser,
  onSignInGoogle: () -> Unit,
  onOpenLogin: () -> Unit,
  onOpenRegister: () -> Unit,
  onSignOut: () -> Unit,
  onClose: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 12.dp)
      .navigationBarsPadding(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Text(
      text = "Citizen Account & Authentication",
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      color = Slate50
    )

    Text(
      text = "Sign in or register to report community hazards, verify civic resolutions, and earn leaderboard reputation points.",
      fontSize = 12.sp,
      color = Slate400,
      textAlign = TextAlign.Center,
      lineHeight = 17.sp
    )

    // Google Sign-In Action Button
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White)
        .clickable {
          onSignInGoogle()
        }
        .padding(horizontal = 16.dp)
        .testTag("auth_sheet_google_button"),
      contentAlignment = Alignment.Center
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Text(
          text = "G ",
          fontWeight = FontWeight.Black,
          fontSize = 18.sp,
          color = Color(0xFF4285F4)
        )
        Text(
          text = if (user.isGoogleAuthenticated) "Google Citizen: ${user.name}" else "Continue with Google",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = Color(0xFF1F2937)
        )
      }
    }

    if (user.isGoogleAuthenticated) {
      ElevatedButton(
        onClick = onSignOut,
        colors = ButtonDefaults.elevatedButtonColors(
          containerColor = Slate800,
          contentColor = Orange400
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_sheet_sign_out_button")
      ) {
        Text("Switch / Sign Out Account")
      }
    } else {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        ElevatedButton(
          onClick = onOpenLogin,
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Indigo600,
            contentColor = Slate50
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("auth_sheet_email_login_button")
        ) {
          Text("Email Login")
        }
        ElevatedButton(
          onClick = onOpenRegister,
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = GlassSurface,
            contentColor = Cyan400
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("auth_sheet_register_button")
        ) {
          Text("Register")
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))
  }
}

/**
 * Issue Detail Modal Content
 */
@Composable
fun IssueDetailContent(
  issue: CivicIssueItem,
  onUpvote: () -> Unit,
  onClose: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 8.dp)
      .navigationBarsPadding(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(issue.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate50, modifier = Modifier.weight(1f))
      IconButton(onClick = onClose) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
      }
    }

    AsyncImage(
      model = issue.imageUrl,
      contentDescription = issue.title,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .clip(RoundedCornerShape(18.dp))
    )

    Text(issue.description, fontSize = 13.sp, color = Slate300, lineHeight = 19.sp)

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      GlassDarkBadge {
        Text("Dept: ${issue.municipalDepartment}", fontSize = 11.sp, color = Cyan300)
      }
      GlassDarkBadge {
        Text("Status: ${issue.status}", fontSize = 11.sp, color = Orange400, fontWeight = FontWeight.Bold)
      }
    }

    GradientButton(
      text = "Upvote Hazard (+${issue.upvotes} Upvotes)",
      onClick = onUpvote,
      leadingIcon = {
        Icon(Icons.Default.ThumbUp, contentDescription = null, tint = Slate50, modifier = Modifier.size(18.dp))
      }
    )

    Spacer(modifier = Modifier.height(4.dp))
  }
}

/**
 * Bottom Navigation Bar
 */
@Composable
fun GlassBottomNavigationBar(
  currentTab: NavTab,
  onTabSelected: (NavTab) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(
        elevation = 16.dp,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        ambientColor = GlassCardShadow,
        spotColor = GlassCardShadow
      )
      .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
      .background(GlassSurface)
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          colors = listOf(GlassBorder, Color.Transparent)
        ),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
      )
      .navigationBarsPadding()
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      NavItem(
        label = "Home",
        icon = Icons.Default.LocationOn,
        isSelected = currentTab == NavTab.HOME,
        onClick = { onTabSelected(NavTab.HOME) },
        testTag = "nav_home"
      )
      NavItem(
        label = "Map",
        icon = Icons.Default.Map,
        isSelected = currentTab == NavTab.MAP,
        onClick = { onTabSelected(NavTab.MAP) },
        testTag = "nav_map"
      )
      NavItem(
        label = "Gemini",
        icon = Icons.Default.AutoAwesome,
        isSelected = currentTab == NavTab.AI_ASSISTANT,
        onClick = { onTabSelected(NavTab.AI_ASSISTANT) },
        testTag = "nav_ai_assistant"
      )
      NavItem(
        label = "Alerts",
        icon = Icons.Default.Notifications,
        isSelected = currentTab == NavTab.ALERTS,
        onClick = { onTabSelected(NavTab.ALERTS) },
        testTag = "nav_alerts"
      )
      NavItem(
        label = "Profile",
        icon = Icons.Default.Person,
        isSelected = currentTab == NavTab.PROFILE,
        onClick = { onTabSelected(NavTab.PROFILE) },
        testTag = "nav_profile"
      )
    }
  }
}

@Composable
fun NavItem(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(2.dp),
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = false, color = Cyan400),
        onClick = onClick
      )
      .padding(horizontal = 8.dp, vertical = 2.dp)
      .testTag(testTag)
  ) {
    Box(
      modifier = Modifier
        .size(26.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(if (isSelected) Cyan400.copy(alpha = 0.2f) else Color.Transparent),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (isSelected) Cyan400 else Slate500,
        modifier = Modifier.size(18.dp)
      )
    }

    Text(
      text = label,
      fontSize = 9.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      color = if (isSelected) Cyan400 else Slate500
    )
  }
}
