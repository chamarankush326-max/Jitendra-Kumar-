package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SocialViewModel

@Composable
fun AppBottomNav(
  viewModel: SocialViewModel,
  currentScreen: ScreenDestination
) {
  NavigationBar(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("bottom_nav_bar"),
    windowInsets = WindowInsets.navigationBars,
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp
  ) {
    // 1. HOME
    val isHome = currentScreen == ScreenDestination.HOME
    NavigationBarItem(
      selected = isHome,
      onClick = { viewModel.navigateTo(ScreenDestination.HOME) },
      icon = {
        Icon(
          imageVector = if (isHome) Icons.Filled.Home else Icons.Outlined.Home,
          contentDescription = viewModel.getString("nav_home")
        )
      },
      label = { Text(viewModel.getString("nav_home")) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = SaffronPrimary,
        selectedTextColor = SaffronPrimary,
        indicatorColor = SaffronPrimary.copy(alpha = 0.15f)
      ),
      modifier = Modifier.testTag("nav_item_home")
    )

    // 2. REELS
    val isReels = currentScreen == ScreenDestination.REELS
    NavigationBarItem(
      selected = isReels,
      onClick = { viewModel.navigateTo(ScreenDestination.REELS) },
      icon = {
        Icon(
          imageVector = if (isReels) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
          contentDescription = viewModel.getString("nav_reels")
        )
      },
      label = { Text(viewModel.getString("nav_reels")) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = SaffronPrimary,
        selectedTextColor = SaffronPrimary,
        indicatorColor = SaffronPrimary.copy(alpha = 0.15f)
      ),
      modifier = Modifier.testTag("nav_item_reels")
    )

    // 3. CREATE
    val isCreate = currentScreen == ScreenDestination.CREATE_POST
    NavigationBarItem(
      selected = isCreate,
      onClick = { viewModel.navigateTo(ScreenDestination.CREATE_POST) },
      icon = {
        Icon(
          imageVector = Icons.Filled.AddCircle,
          contentDescription = viewModel.getString("nav_create"),
          modifier = Modifier.size(28.dp)
        )
      },
      label = { Text(viewModel.getString("nav_create")) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = SaffronPrimary,
        selectedTextColor = SaffronPrimary,
        indicatorColor = SaffronPrimary.copy(alpha = 0.15f)
      ),
      modifier = Modifier.testTag("nav_item_create")
    )

    // 4. CHAT (with unread badge)
    val isChat = currentScreen == ScreenDestination.CHAT_LIST || currentScreen == ScreenDestination.CHAT_DETAIL
    NavigationBarItem(
      selected = isChat,
      onClick = { viewModel.navigateTo(ScreenDestination.CHAT_LIST) },
      icon = {
        BadgedBox(
          badge = {
            Badge { Text("2") }
          }
        ) {
          Icon(
            imageVector = if (isChat) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
            contentDescription = viewModel.getString("nav_chat")
          )
        }
      },
      label = { Text(viewModel.getString("nav_chat")) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = SaffronPrimary,
        selectedTextColor = SaffronPrimary,
        indicatorColor = SaffronPrimary.copy(alpha = 0.15f)
      ),
      modifier = Modifier.testTag("nav_item_chat")
    )

    // 5. PROFILE
    val isProfile = currentScreen == ScreenDestination.PROFILE
    NavigationBarItem(
      selected = isProfile,
      onClick = { viewModel.navigateTo(ScreenDestination.PROFILE) },
      icon = {
        Icon(
          imageVector = if (isProfile) Icons.Filled.Person else Icons.Outlined.Person,
          contentDescription = viewModel.getString("nav_profile")
        )
      },
      label = { Text(viewModel.getString("nav_profile")) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = SaffronPrimary,
        selectedTextColor = SaffronPrimary,
        indicatorColor = SaffronPrimary.copy(alpha = 0.15f)
      ),
      modifier = Modifier.testTag("nav_item_profile")
    )
  }
}
