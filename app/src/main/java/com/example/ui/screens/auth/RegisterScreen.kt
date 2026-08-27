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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate800

/**
 * Modern Glassmorphic Register Screen for CivicFix
 * Supports citizen profile creation via Firebase Auth and Google Sign-In with Credential Manager.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
  isLoading: Boolean,
  errorMessage: String?,
  onRegisterEmail: (name: String, email: String, pass: String, neighborhood: String) -> Unit,
  onSignInGoogle: () -> Unit,
  onNavigateToLogin: () -> Unit,
  onClearError: () -> Unit,
  onBack: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var fullName by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var selectedNeighborhood by remember { mutableStateOf("Central District") }
  var localValidationError by remember { mutableStateOf<String?>(null) }

  val focusManager = LocalFocusManager.current
  val scrollState = rememberScrollState()

  val neighborhoods = listOf(
    "Central District",
    "North Ward",
    "Sector 4 Tech",
    "Old Town East",
    "South Green Valley"
  )

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
              .testTag("register_back_button"),
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

      // App Emblem
      Box(
        modifier = Modifier
          .size(68.dp)
          .shadow(16.dp, CircleShape, ambientColor = Emerald400.copy(alpha = 0.5f), spotColor = Cyan500)
          .clip(CircleShape)
          .background(Brush.linearGradient(listOf(Indigo600, Emerald500)))
          .border(2.dp, Cyan400.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.HowToReg,
          contentDescription = "Citizen Registration Emblem",
          tint = Color.White,
          modifier = Modifier.size(34.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Join CivicFix",
        fontSize = 24.sp,
        fontWeight = FontWeight.Black,
        color = Slate50,
        letterSpacing = 0.5.sp
      )

      Text(
        text = "Create your verified citizen profile to report neighborhood hazards and track municipal resolutions",
        fontSize = 12.sp,
        color = Slate300,
        textAlign = TextAlign.Center,
        lineHeight = 17.sp,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Main Registration Form Card
      GlassCard(
        shape = RoundedCornerShape(28.dp),
        backgroundColor = MidnightBackground.copy(alpha = 0.85f),
        borderColor = GlassBorder,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Error Message Banner
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
                  .testTag("register_error_banner")
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

          // Full Name Input
          Text(
            text = "Full Name",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate300
          )
          OutlinedTextField(
            value = fullName,
            onValueChange = {
              fullName = it
              localValidationError = null
            },
            placeholder = { Text("e.g. Sarah Jenkins", fontSize = 13.sp, color = Slate400) },
            leadingIcon = {
              Icon(Icons.Default.Person, contentDescription = "Full Name", tint = Cyan400, modifier = Modifier.size(20.dp))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Text,
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
              .testTag("register_name_input")
          )

          // Email Input
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
              .testTag("register_email_input")
          )

          // Password Input
          Text(
            text = "Password (min. 6 characters)",
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
            placeholder = { Text("Create a secure password", fontSize = 13.sp, color = Slate400) },
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
              .testTag("register_password_input")
          )

          // Confirm Password Input
          Text(
            text = "Confirm Password",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate300
          )
          OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
              confirmPassword = it
              localValidationError = null
            },
            placeholder = { Text("Re-enter password", fontSize = 13.sp, color = Slate400) },
            leadingIcon = {
              Icon(Icons.Default.Lock, contentDescription = "Confirm Password", tint = Cyan400, modifier = Modifier.size(20.dp))
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Password,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
              onDone = { focusManager.clearFocus() }
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
              .testTag("register_confirm_password_input")
          )

          // Neighborhood / Ward Selector
          Text(
            text = "Neighborhood / District",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate300
          )
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            neighborhoods.forEach { district ->
              val isSelected = selectedNeighborhood == district
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) Indigo600 else GlassSurface)
                  .border(1.dp, if (isSelected) Cyan400 else GlassBorder, RoundedCornerShape(12.dp))
                  .clickable { selectedNeighborhood = district }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  if (isSelected) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = null,
                      tint = Cyan300,
                      modifier = Modifier.size(12.dp)
                    )
                  }
                  Text(
                    text = district,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Slate50 else Slate300
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Register Action Button
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Indigo600.copy(alpha = 0.5f),
                spotColor = Emerald500.copy(alpha = 0.3f)
              )
              .clip(RoundedCornerShape(16.dp))
              .background(Brush.linearGradient(listOf(Indigo600, Cyan500)))
              .clickable(enabled = !isLoading) {
                focusManager.clearFocus()
                when {
                  fullName.isBlank() -> localValidationError = "Please enter your full name."
                  email.isBlank() || !email.contains("@") || !email.contains(".") -> localValidationError = "Please enter a valid email address."
                  password.length < 6 -> localValidationError = "Password must be at least 6 characters long."
                  password != confirmPassword -> localValidationError = "Passwords do not match."
                  else -> {
                    localValidationError = null
                    onRegisterEmail(fullName.trim(), email.trim(), password, selectedNeighborhood)
                  }
                }
              }
              .testTag("register_submit_button"),
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
                text = "Create Citizen Account",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 0.3.sp
              )
            }
          }

          // Divider
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            HorizontalDivider(
              modifier = Modifier.weight(1f),
              color = GlassBorder
            )
            Text(
              text = "OR SIGN UP WITH",
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

          // Google Sign In Button
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
              .testTag("register_google_button"),
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
                text = "Sign Up with Google",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1F2937)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Switch to Login Link
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Text(
          text = "Already have a citizen account? ",
          fontSize = 13.sp,
          color = Slate300
        )
        Text(
          text = "Sign In",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Cyan400,
          modifier = Modifier
            .clickable(onClick = onNavigateToLogin)
            .testTag("register_to_login_link")
        )
      }

      Spacer(modifier = Modifier.height(10.dp))
    }
  }
}
