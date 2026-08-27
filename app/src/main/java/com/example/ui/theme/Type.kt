package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Material 3 Typography tailored for Sophisticated Dark UI
val Typography = Typography(
  displayLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 38.sp,
    letterSpacing = (-0.5).sp,
    color = Slate50,
  ),
  headlineLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 30.sp,
    letterSpacing = (-0.25).sp,
    color = Slate50,
  ),
  headlineMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    letterSpacing = 0.sp,
    color = Slate50,
  ),
  titleLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp,
    color = Slate50,
  ),
  titleMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 15.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.15.sp,
    color = Slate50,
  ),
  bodyLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.25.sp,
    color = Slate300,
  ),
  bodyMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.2.sp,
    color = Slate400,
  ),
  bodySmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
    lineHeight = 15.sp,
    letterSpacing = 0.3.sp,
    color = Slate500,
  ),
  labelLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp,
    color = Slate50,
  ),
  labelMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp,
    color = Slate400,
  ),
  labelSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.8.sp,
    color = Slate400,
  )
)

