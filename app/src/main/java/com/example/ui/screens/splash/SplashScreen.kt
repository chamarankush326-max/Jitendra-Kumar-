package com.example.ui.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.Logo3DAnimation
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PeacockCyan
import com.example.ui.theme.RoyalIndigo
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SocialViewModel

@Composable
fun SplashScreen(viewModel: SocialViewModel) {
  var visible by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    visible = true
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(
            DarkBackground,
            Color(0xFF1B1630),
            DarkBackground
          )
        )
      )
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(24.dp)
    ) {
      // 3D Animated Rotating Logo
      Logo3DAnimation(
        size = 190.dp,
        speedMultiplier = 1.2f,
        modifier = Modifier.testTag("splash_3d_logo")
      )

      Spacer(modifier = Modifier.height(24.dp))

      AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 40 })
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Apna Social",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            letterSpacing = 1.sp,
            fontSize = 34.sp
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "भारत का अपना सोशल नेटवर्क 🇮🇳",
            style = MaterialTheme.typography.bodyMedium,
            color = SaffronPrimary,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Bharat's Own Connected Community",
            style = MaterialTheme.typography.bodySmall,
            color = PeacockCyan.copy(alpha = 0.8f),
            fontSize = 12.sp
          )
        }
      }
    }

    // Version label at bottom
    Text(
      text = "v1.0 • Secure & Made in India",
      style = MaterialTheme.typography.labelSmall,
      color = Color.White.copy(alpha = 0.4f),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 32.dp)
    )
  }
}
