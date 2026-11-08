package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.components.AppBottomNav
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.screens.admin.AdminPanelScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.chat.ChatDetailScreen
import com.example.ui.screens.chat.ChatListScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.post.CreatePostScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.recharge.RechargeHistoryScreen
import com.example.ui.screens.recharge.RechargeScreen
import com.example.ui.screens.reels.ReelsScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.settings.PrivacyPolicyScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SocialViewModel

@Composable
fun ApnaSocialApp(viewModel: SocialViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val activeCommentPostId by viewModel.activeCommentPostId.collectAsState()
  val uiNotice by viewModel.uiNotice.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  // Show snackbar when uiNotice changes
  LaunchedEffect(uiNotice) {
    if (uiNotice != null) {
      snackbarHostState.showSnackbar(uiNotice!!)
      viewModel.clearNotice()
    }
  }

  val showBottomNav = currentScreen in listOf(
    ScreenDestination.HOME,
    ScreenDestination.REELS,
    ScreenDestination.CREATE_POST,
    ScreenDestination.CHAT_LIST,
    ScreenDestination.PROFILE
  )

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    bottomBar = {
      if (showBottomNav) {
        AppBottomNav(
          viewModel = viewModel,
          currentScreen = currentScreen
        )
      }
    },
    modifier = Modifier
      .fillMaxSize()
      .testTag("apna_social_root")
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(if (showBottomNav) PaddingValues(bottom = innerPadding.calculateBottomPadding()) else PaddingValues())
    ) {
      when (currentScreen) {
        ScreenDestination.SPLASH -> SplashScreen(viewModel = viewModel)
        ScreenDestination.AUTH -> AuthScreen(viewModel = viewModel)
        ScreenDestination.HOME -> HomeScreen(viewModel = viewModel)
        ScreenDestination.REELS -> ReelsScreen(viewModel = viewModel)
        ScreenDestination.CREATE_POST -> CreatePostScreen(viewModel = viewModel)
        ScreenDestination.CHAT_LIST -> ChatListScreen(viewModel = viewModel)
        ScreenDestination.CHAT_DETAIL -> ChatDetailScreen(viewModel = viewModel)
        ScreenDestination.NOTIFICATIONS -> NotificationsScreen(viewModel = viewModel)
        ScreenDestination.RECHARGE -> RechargeScreen(viewModel = viewModel)
        ScreenDestination.RECHARGE_HISTORY -> RechargeHistoryScreen(viewModel = viewModel)
        ScreenDestination.SEARCH -> SearchScreen(viewModel = viewModel)
        ScreenDestination.PROFILE -> ProfileScreen(viewModel = viewModel)
        ScreenDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
        ScreenDestination.ADMIN -> AdminPanelScreen(viewModel = viewModel)
        ScreenDestination.PRIVACY_POLICY -> PrivacyPolicyScreen(viewModel = viewModel)
      }
    }
  }

  // Comments Bottom Sheet
  if (activeCommentPostId != null) {
    CommentsBottomSheet(
      postId = activeCommentPostId!!,
      viewModel = viewModel,
      onDismiss = { viewModel.closeComments() }
    )
  }
}
