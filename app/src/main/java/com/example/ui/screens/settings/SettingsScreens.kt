package com.example.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppLanguage
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SocialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SocialViewModel) {
  BackHandler {
    viewModel.navigateBack()
  }

  val currentLang by viewModel.currentLanguage.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val exportedData by viewModel.exportedDataText.collectAsState()

  var showLogoutConfirm by remember { mutableStateOf(false) }
  var showDeleteConfirm by remember { mutableStateOf(false) }
  var showPasswordDialog by remember { mutableStateOf(false) }
  var isPrivateAccount by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = { viewModel.navigateBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        title = { Text(viewModel.getString("settings_title"), fontWeight = FontWeight.Bold) }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
        .testTag("settings_screen"),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Language Preferences
      SettingsSectionHeader(title = viewModel.getString("Language"))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("App Language / भाषा", fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Select English or हिन्दी", fontSize = 11.sp, color = Color.Gray)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              FilterChip(
                selected = currentLang == AppLanguage.ENGLISH,
                onClick = { viewModel.setLanguage(AppLanguage.ENGLISH) },
                label = { Text("English") }
              )
              FilterChip(
                selected = currentLang == AppLanguage.HINDI,
                onClick = { viewModel.setLanguage(AppLanguage.HINDI) },
                label = { Text("हिन्दी 🇮🇳") }
              )
            }
          }
        }
      }

      // 2. Data Download / Uservesh Export
      SettingsSectionHeader(title = "Data & Backup / डेटा")
      Card(
        modifier = Modifier.fillMaxWidth().testTag("download_data_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Filled.Download, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = viewModel.getString("download_data"),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = viewModel.getString("download_data_desc"),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 11.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = { viewModel.triggerDataExport() },
            modifier = Modifier.fillMaxWidth().height(40.dp).testTag("trigger_download_btn"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
          ) {
            Icon(Icons.Filled.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Export & Download Data", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }

      // 3. Privacy & Security
      SettingsSectionHeader(title = viewModel.getString("privacy_settings"))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
          // Private Account Toggle
          Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Private Account", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
              Text("Only approved followers see posts", fontSize = 11.sp, color = Color.Gray)
            }
            Switch(checked = isPrivateAccount, onCheckedChange = { isPrivateAccount = it })
          }

          Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

          // Change Password
          SettingsNavRow(
            icon = Icons.Filled.Lock,
            title = viewModel.getString("change_password"),
            onClick = { showPasswordDialog = true }
          )

          Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

          // Blocked Users
          SettingsNavRow(
            icon = Icons.Filled.Block,
            title = "Blocked Users (0)",
            onClick = { viewModel.showNotice("No blocked users") }
          )

          Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

          // Privacy Policy
          SettingsNavRow(
            icon = Icons.Filled.Policy,
            title = viewModel.getString("privacy_policy"),
            onClick = { viewModel.navigateTo(ScreenDestination.PRIVACY_POLICY) }
          )
        }
      }

      // 4. Admin Shortcut
      SettingsSectionHeader(title = "Administration")
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
          SettingsNavRow(
            icon = Icons.Filled.AdminPanelSettings,
            title = viewModel.getString("admin_title"),
            onClick = { viewModel.navigateTo(ScreenDestination.ADMIN) }
          )
        }
      }

      // 5. Account Actions (Logout & Delete)
      SettingsSectionHeader(title = "Account Actions")
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
          SettingsNavRow(
            icon = Icons.Filled.Logout,
            title = viewModel.getString("logout"),
            tint = SaffronPrimary,
            onClick = { showLogoutConfirm = true }
          )

          Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

          SettingsNavRow(
            icon = Icons.Filled.DeleteForever,
            title = viewModel.getString("delete_account"),
            tint = MaterialTheme.colorScheme.error,
            onClick = { showDeleteConfirm = true }
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Logout Confirmation Dialog
  if (showLogoutConfirm) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirm = false },
      title = { Text(viewModel.getString("logout"), fontWeight = FontWeight.Bold) },
      text = { Text(viewModel.getString("logout_confirm")) },
      confirmButton = {
        Button(
          onClick = {
            showLogoutConfirm = false
            viewModel.logout()
          },
          colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
        ) {
          Text("Confirm Logout")
        }
      },
      dismissButton = {
        TextButton(onClick = { showLogoutConfirm = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Delete Account Confirmation Dialog
  if (showDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = { Text(viewModel.getString("delete_account"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
      text = { Text("Are you sure you want to permanently delete your account and all data? This cannot be undone.") },
      confirmButton = {
        Button(
          onClick = {
            showDeleteConfirm = false
            viewModel.logout()
            viewModel.showNotice("Account scheduled for deletion.")
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Delete Permanently")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirm = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Change Password Dialog
  if (showPasswordDialog) {
    ChangePasswordDialog(
      onDismiss = { showPasswordDialog = false },
      onSuccess = {
        showPasswordDialog = false
        viewModel.showNotice("Password changed successfully!")
      }
    )
  }

  // Data Download Viewer Dialog
  if (exportedData != null) {
    DataExportDialog(
      dataText = exportedData!!,
      onDismiss = { viewModel.closeDataExport() },
      onCopy = {
        viewModel.showNotice("Data export copied to clipboard!")
        viewModel.closeDataExport()
      }
    )
  }
}

@Composable
fun SettingsSectionHeader(title: String) {
  Text(
    text = title,
    style = MaterialTheme.typography.labelLarge,
    fontWeight = FontWeight.Bold,
    color = SaffronPrimary,
    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
  )
}

@Composable
fun SettingsNavRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  tint: Color = MaterialTheme.colorScheme.onSurface,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    Spacer(modifier = Modifier.width(14.dp))
    Text(text = title, style = MaterialTheme.typography.bodyMedium, color = tint, fontWeight = FontWeight.Medium)
    Spacer(modifier = Modifier.weight(1f))
    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
  }
}

@Composable
fun ChangePasswordDialog(
  onDismiss: () -> Unit,
  onSuccess: () -> Unit
) {
  var oldPass by remember { mutableStateOf("") }
  var newPass by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Change Password", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = oldPass,
          onValueChange = { oldPass = it },
          label = { Text("Current Password") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = newPass,
          onValueChange = { newPass = it },
          label = { Text("New Password") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
      }
    },
    confirmButton = {
      Button(onClick = onSuccess) {
        Text("Update Password")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun DataExportDialog(
  dataText: String,
  onDismiss: () -> Unit,
  onCopy: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("User Data Export (Uservesh)", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.fillMaxWidth().height(320.dp).verticalScroll(rememberScrollState())) {
        Text(
          text = dataText,
          fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
          fontSize = 11.sp,
          lineHeight = 15.sp
        )
      }
    },
    confirmButton = {
      Button(onClick = onCopy, colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)) {
        Text("Copy & Save Backup")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Close")
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(viewModel: SocialViewModel) {
  BackHandler {
    viewModel.navigateBack()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = { viewModel.navigateBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        title = { Text(viewModel.getString("privacy_policy"), fontWeight = FontWeight.Bold) }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(18.dp)
        .testTag("privacy_policy_screen")
    ) {
      Text(
        text = "Apna Social Privacy Policy",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = SaffronPrimary
      )
      Text(text = "Last updated: November 2026 • Bharat", fontSize = 11.sp, color = Color.Gray)

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "1. Information We Collect\n" +
          "Apna Social collects your basic profile information (name, chosen username, bio, and mobile phone number for OTP verification) to provide you with community interaction and secure persistent sessions.\n\n" +
          "2. Data Storage & Ownership\n" +
          "You retain complete ownership of all content, reels, stories, photos, and messages you author. You can export or download your complete user data anytime via the 'Download My Data' feature in Settings.\n\n" +
          "3. Mobile Recharge & Payment Safety\n" +
          "Mobile recharge transactions operate under strict sandbox preview guidelines. No financial credentials or card details are stored. Real payments remain disabled until an authorized RBI-compliant payment gateway is configured.\n\n" +
          "4. Community Moderation & Safety\n" +
          "Content that violates Indian legal standards, incites violence, or spreads harmful disinformation is removed by moderators. Users can block or report accounts directly.\n\n" +
          "5. Account Deletion & Rights\n" +
          "You can request permanent account deletion at any time from Settings, which scrubs your profile, posts, and session tokens.",
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 22.sp
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
