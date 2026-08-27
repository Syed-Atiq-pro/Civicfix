package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AmbientOrbsBackground
import com.example.ui.components.GlassCard
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Emerald400
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassCardShadow
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.Orange400
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800

/**
 * Modern Glassmorphic Login Screen for CivicFix
 * Supports Firebase Auth (Email/Password) and Google Sign-In via Credential Manager.
 */
@Composable
fun LoginScreen(
  isLoading: Boolean,
  errorMessage: String?,
  onSignInEmail: (String, String) -> Unit,
  onSignInGoogle: () -> Unit,
  onNavigateToRegister: () -> Unit,
  onContinueAsGuest: () -> Unit,
  onClearError: () -> Unit,
  onBack: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var localValidationError by remember { mutableStateOf<String?>(null) }
  val focusManager = LocalFocusManager.current
  val scrollState = rememberScrollState()

  AmbientOrbsBackground(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .imePadding()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Top Navigation bar if back action available
      if (onBack != null) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
          horizontalArrangement = Arrangement.Start
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(GlassSurface)
              .border(1.dp, GlassBorder, CircleShape)
              .clickable { onBack() }
              .testTag("login_back_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Slate100,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      // App Crest & Branding
      Box(
        modifier = Modifier
          .size(72.dp)
          .shadow(16.dp, CircleShape, ambientColor = Cyan400.copy(alpha = 0.5f), spotColor = Indigo600)
          .clip(CircleShape)
          .background(Brush.linearGradient(listOf(Indigo600, Cyan500)))
          .border(2.dp, Cyan400.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = "CivicFix Crest",
          tint = Color.White,
          modifier = Modifier.size(38.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "CivicFix Portal",
        fontSize = 26.sp,
        fontWeight = FontWeight.Black,
        color = Slate50,
        letterSpacing = 0.5.sp
      )

      Text(
        text = "Sign in to verify civic issues, track municipal resolution & earn community karma",
        fontSize = 13.sp,
        color = Slate300,
        textAlign = TextAlign.Center,
        lineHeight = 18.sp,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Main Glass Card Form Container
      GlassCard(
        shape = RoundedCornerShape(28.dp),
        backgroundColor = MidnightBackground.copy(alpha = 0.82f),
        borderColor = GlassBorder,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Error Message Banner (Firebase or Local)
          val activeError = errorMessage ?: localValidationError
          AnimatedVisibility(
            visible = activeError != null,
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            activeError?.let { err ->
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .background(Rose500.copy(alpha = 0.18f))
                  .border(1.dp, Rose500.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                  .padding(horizontal = 12.dp, vertical = 10.dp)
                  .testTag("auth_error_banner")
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "Error",
                    tint = Rose500,
                    modifier = Modifier.size(18.dp)
                  )
                  Text(
                    text = err,
                    fontSize = 12.sp,
                    color = Slate50,
                    modifier = Modifier.weight(1f),
                    lineHeight = 16.sp
                  )
                  IconButton(
                    onClick = {
                      localValidationError = null
                      onClearError()
                    },
                    modifier = Modifier.size(20.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = "Dismiss",
                      tint = Slate400,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }
              }
            }
          }

          // Email Input Field
          Text(
            text = "Email Address",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate300
          )
          OutlinedTextField(
            value = email,
            onValueChange = {
              email = it
              localValidationError = null
            },
            placeholder = { Text("citizen@example.com", fontSize = 13.sp, color = Slate400) },
            leadingIcon = {
              Icon(Icons.Default.Email, contentDescription = "Email", tint = Cyan400, modifier = Modifier.size(20.dp))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Email,
              imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
              onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Slate50,
              unfocusedTextColor = Slate100,
              focusedBorderColor = Cyan400,
              unfocusedBorderColor = GlassBorder,
              focusedContainerColor = GlassSurface,
              unfocusedContainerColor = GlassSurface
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_email_input")
          )

          // Password Input Field
          Text(
            text = "Password",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate300
          )
          OutlinedTextField(
            value = password,
            onValueChange = {
              password = it
              localValidationError = null
            },
            placeholder = { Text("Enter your password", fontSize = 13.sp, color = Slate400) },
            leadingIcon = {
              Icon(Icons.Default.Lock, contentDescription = "Password", tint = Cyan400, modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
              IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                  imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                  tint = Slate400,
                  modifier = Modifier.size(20.dp)
                )
              }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Password,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
              onDone = {
                focusManager.clearFocus()
                if (email.isBlank() || password.isBlank()) {
                  localValidationError = "Please fill in both email and password."
                } else {
                  onSignInEmail(email.trim(), password)
                }
              }
            ),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Slate50,
              unfocusedTextColor = Slate100,
              focusedBorderColor = Cyan400,
              unfocusedBorderColor = GlassBorder,
              focusedContainerColor = GlassSurface,
              unfocusedContainerColor = GlassSurface
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_password_input")
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Primary Email Sign-In Button
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Indigo600.copy(alpha = 0.5f),
                spotColor = Cyan500.copy(alpha = 0.3f)
              )
              .clip(RoundedCornerShape(16.dp))
              .background(Brush.linearGradient(listOf(Indigo600, Cyan500)))
              .clickable(enabled = !isLoading) {
                focusManager.clearFocus()
                if (email.isBlank() || password.isBlank()) {
                  localValidationError = "Please enter both email and password."
                } else if (!email.contains("@") || !email.contains(".")) {
                  localValidationError = "Please enter a valid email address."
                } else {
                  localValidationError = null
                  onSignInEmail(email.trim(), password)
                }
              }
              .testTag("login_submit_button"),
            contentAlignment = Alignment.Center
          ) {
            if (isLoading) {
              CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
              )
            } else {
              Text(
                text = "Sign In to CivicFix",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 0.3.sp
              )
            }
          }

          // Divider with text
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            HorizontalDivider(
              modifier = Modifier.weight(1f),
              color = GlassBorder
            )
            Text(
              text = "OR SIGN IN WITH",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Slate400,
              modifier = Modifier.padding(horizontal = 10.dp)
            )
            HorizontalDivider(
              modifier = Modifier.weight(1f),
              color = GlassBorder
            )
          }

          // Google Sign-In via Credential Manager Button
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(Color.White)
              .clickable(
                enabled = !isLoading,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Slate800),
                onClick = onSignInGoogle
              )
              .padding(horizontal = 16.dp)
              .testTag("login_google_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(
                text = "G ",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = Color(0xFF4285F4)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Continue with Google",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1F2937)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Bottom Navigation: Register Link & Guest Mode
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Text(
          text = "New to CivicFix? ",
          fontSize = 13.sp,
          color = Slate300
        )
        Text(
          text = "Create Citizen Account",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Cyan400,
          modifier = Modifier
            .clickable(onClick = onNavigateToRegister)
            .testTag("login_to_register_link")
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      TextButton(
        onClick = onContinueAsGuest,
        modifier = Modifier.testTag("login_guest_button")
      ) {
        Text(
          text = "Continue as Guest Citizen →",
          fontSize = 12.sp,
          color = Slate400,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}
