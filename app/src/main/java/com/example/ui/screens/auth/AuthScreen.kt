package com.example.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.Logo3DAnimation
import com.example.ui.theme.PeacockCyan
import com.example.ui.theme.RoyalIndigo
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.SocialViewModel
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(viewModel: SocialViewModel) {
  var selectedTab by remember { mutableStateOf(0) } // 0: Mobile OTP, 1: Google / Email
  var phoneNumber by remember { mutableStateOf("9876543210") }
  var otpCode by remember { mutableStateOf("") }
  var isOtpSent by remember { mutableStateOf(false) }
  var userName by remember { mutableStateOf("Jitendra Kumar") }
  var emailAddress by remember { mutableStateOf("chamarankush326@gmail.com") }
  var countdown by remember { mutableStateOf(30) }

  LaunchedEffect(isOtpSent) {
    if (isOtpSent) {
      countdown = 30
      while (countdown > 0) {
        delay(1000)
        countdown--
      }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("auth_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp)
        .statusBarsPadding()
        .navigationBarsPadding(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Language Switcher Chip at top right
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        AssistChip(
          onClick = { viewModel.toggleLanguage() },
          label = {
            Text(
              if (viewModel.currentLanguage.collectAsState().value.code == "hi") "🇮🇳 हिन्दी" else "🇬🇧 English",
              fontWeight = FontWeight.Bold
            )
          },
          leadingIcon = { Icon(Icons.Filled.Language, contentDescription = null, modifier = Modifier.size(16.dp)) }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3D Mini Rotating Logo
      Logo3DAnimation(
        size = 110.dp,
        speedMultiplier = 1.0f,
        modifier = Modifier.testTag("auth_logo")
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = viewModel.getString("login_title"),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )

      Text(
        text = viewModel.getString("login_sub"),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Auth Mode Tab Switcher
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = SaffronPrimary,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_tabs")
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text(viewModel.getString("phone_login"), fontSize = 12.sp, fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(18.dp)) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text(viewModel.getString("google_login"), fontSize = 12.sp, fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp)) }
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      if (selectedTab == 0) {
        // PHONE + OTP FLOW
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Full Name",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = userName,
              onValueChange = { userName = it },
              modifier = Modifier.fillMaxWidth().testTag("auth_name_input"),
              shape = RoundedCornerShape(12.dp),
              leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = viewModel.getString("enter_mobile"),
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = phoneNumber,
              onValueChange = { if (it.length <= 10) phoneNumber = it },
              modifier = Modifier.fillMaxWidth().testTag("auth_phone_input"),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              shape = RoundedCornerShape(12.dp),
              prefix = { Text("+91 ", fontWeight = FontWeight.Bold) },
              leadingIcon = { Icon(Icons.Filled.PhoneAndroid, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!isOtpSent) {
              Button(
                onClick = {
                  if (phoneNumber.length == 10) {
                    isOtpSent = true
                    otpCode = "123456" // autofill demo for friction-free testing
                    viewModel.showNotice("OTP sent to +91 $phoneNumber")
                  } else {
                    viewModel.showNotice("Please enter a valid 10-digit number")
                  }
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("send_otp_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
              ) {
                Text(viewModel.getString("get_otp"), fontWeight = FontWeight.Bold)
              }
            } else {
              // OTP Input View
              Text(
                text = viewModel.getString("enter_otp"),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6) otpCode = it },
                modifier = Modifier.fillMaxWidth().testTag("auth_otp_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("123456") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) }
              )

              Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = viewModel.getString("demo_otp_hint"),
                  style = MaterialTheme.typography.labelSmall,
                  color = SaffronPrimary,
                  fontWeight = FontWeight.Bold
                )

                if (countdown > 0) {
                  Text(
                    text = "Resend in ${countdown}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                  )
                } else {
                  TextButton(onClick = { countdown = 30 }) {
                    Text("Resend OTP", fontSize = 11.sp, color = RoyalIndigo)
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Button(
                onClick = {
                  if (otpCode.isNotEmpty()) {
                    viewModel.login(userName, "+91 $phoneNumber")
                  } else {
                    viewModel.showNotice("Please enter 6-digit OTP")
                  }
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("verify_otp_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
              ) {
                Text(viewModel.getString("verify_continue"), fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      } else {
        // GOOGLE / EMAIL FLOW
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Google Account / Email",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = emailAddress,
              onValueChange = { emailAddress = it },
              modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
              shape = RoundedCornerShape(12.dp),
              leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
              onClick = {
                viewModel.login("Jitendra Kumar", "+91 98765 43210")
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("google_login_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = RoyalIndigo)
            ) {
              Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(viewModel.getString("google_login"), fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Informational note
      Text(
        text = "By signing in, you agree to our Privacy Policy and Community Guidelines. Your session stays logged in securely on this device.",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        textAlign = TextAlign.Center,
        fontSize = 11.sp
      )

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
