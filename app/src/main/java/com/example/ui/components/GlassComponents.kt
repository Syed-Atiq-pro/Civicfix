package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Cyan600
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassCardShadow
import com.example.ui.theme.GlassDarkBorder
import com.example.ui.theme.GlassDarkSurface
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Indigo600
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.OrbCyan
import com.example.ui.theme.OrbIndigo
import com.example.ui.theme.Slate50

/**
 * Ambient background canvas that renders glowing indigo and cyan orbs over deep midnight slate.
 */
@Composable
fun AmbientOrbsBackground(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
  val pulseOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 25f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_offset"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MidnightBackground)
  ) {
    // Canvas drawing ambient radiant orbs
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Top-Left Indigo Orb
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            OrbIndigo.copy(alpha = 0.45f),
            Indigo600.copy(alpha = 0.25f),
            Color.Transparent
          ),
          center = Offset(-w * 0.05f + pulseOffset, -h * 0.02f),
          radius = w * 0.75f
        ),
        radius = w * 0.75f,
        center = Offset(-w * 0.05f + pulseOffset, -h * 0.02f)
      )

      // Bottom-Right Cyan Orb
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            OrbCyan.copy(alpha = 0.40f),
            Cyan600.copy(alpha = 0.20f),
            Color.Transparent
          ),
          center = Offset(w * 1.05f - pulseOffset, h * 0.95f),
          radius = w * 0.85f
        ),
        radius = w * 0.85f,
        center = Offset(w * 1.05f - pulseOffset, h * 0.95f)
      )

      // Center-Right subtle magenta/indigo glow
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Indigo500.copy(alpha = 0.15f),
            Color.Transparent
          ),
          center = Offset(w * 0.85f, h * 0.45f),
          radius = w * 0.5f
        ),
        radius = w * 0.5f,
        center = Offset(w * 0.85f, h * 0.45f)
      )
    }

    // Main Foreground Content
    content()
  }
}

/**
 * Frosted Glass Card matching the design's `.glass` spec:
 * - background: rgba(255, 255, 255, 0.08)
 * - border: 1px solid rgba(255, 255, 255, 0.12)
 * - box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37)
 */
@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(24.dp),
  backgroundColor: Color = GlassSurface,
  borderColor: Color = GlassBorder,
  elevation: Dp = 8.dp,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit
) {
  val baseModifier = modifier
    .shadow(
      elevation = elevation,
      shape = shape,
      ambientColor = GlassCardShadow,
      spotColor = GlassCardShadow
    )
    .clip(shape)
    .background(backgroundColor)
    .border(
      width = 1.dp,
      brush = Brush.verticalGradient(
        colors = listOf(
          borderColor,
          borderColor.copy(alpha = 0.04f)
        )
      ),
      shape = shape
    )

  val clickableModifier = if (onClick != null) {
    baseModifier.clickable(
      interactionSource = remember { MutableInteractionSource() },
      indication = ripple(color = Cyan400),
      onClick = onClick
    )
  } else {
    baseModifier
  }

  Box(modifier = clickableModifier) {
    content()
  }
}

/**
 * Frosted Glass Dark Badge matching `.glass-dark` spec:
 * - background: rgba(0, 0, 0, 0.4)
 * - border: 1px solid rgba(255, 255, 255, 0.05)
 */
@Composable
fun GlassDarkBadge(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(12.dp),
  contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .clip(shape)
      .background(GlassDarkSurface)
      .border(
        width = 1.dp,
        color = GlassDarkBorder,
        shape = shape
      )
      .padding(contentPadding)
  ) {
    content()
  }
}

/**
 * High-impact Primary Action Button with Indigo->Cyan gradient and subtle glow
 */
@Composable
fun GradientButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  leadingIcon: (@Composable () -> Unit)? = null,
  testTag: String = "report_new_issue_button"
) {
  val gradient = Brush.horizontalGradient(
    colors = listOf(Indigo600, Cyan500)
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(54.dp)
      .shadow(
        elevation = 12.dp,
        shape = RoundedCornerShape(16.dp),
        ambientColor = Indigo600.copy(alpha = 0.4f),
        spotColor = Cyan500.copy(alpha = 0.3f)
      )
      .clip(RoundedCornerShape(16.dp))
      .background(gradient)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = Slate50),
        onClick = onClick
      )
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (leadingIcon != null) {
        leadingIcon()
        Spacer(modifier = Modifier.width(6.dp))
      }
      Text(
        text = text,
        color = Slate50,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        letterSpacing = 0.2.sp
      )
    }
  }
}
