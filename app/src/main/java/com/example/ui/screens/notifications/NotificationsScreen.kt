package com.example.ui.screens.notifications

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.AppNotification
import com.example.data.model.NotificationType
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonPink
import com.example.ui.theme.RoyalIndigo
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SocialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(viewModel: SocialViewModel) {
  BackHandler {
    viewModel.navigateBack()
  }

  val notifications by viewModel.notifications.collectAsState()
  var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: Likes, 2: Comments, 3: Follows

  val filteredList = when (selectedTab) {
    1 -> notifications.filter { it.type == NotificationType.LIKE }
    2 -> notifications.filter { it.type == NotificationType.COMMENT }
    3 -> notifications.filter { it.type == NotificationType.FOLLOW }
    else -> notifications
  }

  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = { viewModel.navigateBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        title = { Text(viewModel.getString("nav_notifications"), fontWeight = FontWeight.Bold) },
        actions = {
          TextButton(onClick = { viewModel.repository.markNotificationsRead() }) {
            Text("Mark Read", fontSize = 12.sp, color = SaffronPrimary)
          }
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("notifications_screen")
    ) {
      // Filter Tabs
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = SaffronPrimary,
        modifier = Modifier.fillMaxWidth()
      ) {
        listOf("All", "Likes ❤️", "Comments 💬", "Follows 👤").forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = index },
            text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
          )
        }
      }

      if (filteredList.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Text("No notifications in this category", color = Color.Gray)
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredList, key = { it.id }) { notif ->
            NotificationCard(
              notification = notif,
              onClick = {
                if (notif.targetId.isNotEmpty()) {
                  viewModel.navigateTo(ScreenDestination.HOME)
                }
              }
            )
          }
        }
      }
    }
  }
}

@Composable
fun NotificationCard(
  notification: AppNotification,
  onClick: () -> Unit
) {
  val iconColor = when (notification.type) {
    NotificationType.LIKE -> NeonPink
    NotificationType.COMMENT -> RoyalIndigo
    NotificationType.FOLLOW -> EmeraldSuccess
    NotificationType.RECHARGE -> SaffronPrimary
    NotificationType.SYSTEM -> Color.Gray
  }

  val iconVector = when (notification.type) {
    NotificationType.LIKE -> Icons.Filled.Favorite
    NotificationType.COMMENT -> Icons.Filled.ChatBubble
    NotificationType.FOLLOW -> Icons.Filled.PersonAdd
    NotificationType.RECHARGE -> Icons.Filled.Bolt
    NotificationType.SYSTEM -> Icons.Filled.Notifications
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("notification_card_${notification.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (!notification.isRead) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
      } else {
        MaterialTheme.colorScheme.surface
      }
    )
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .background(iconColor.copy(alpha = 0.18f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = iconVector,
          contentDescription = null,
          tint = iconColor,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = notification.title,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = notification.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
          fontSize = 12.sp
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Text(
        text = notification.timeAgo,
        style = MaterialTheme.typography.labelSmall,
        color = Color.Gray,
        fontSize = 10.sp
      )
    }
  }
}
