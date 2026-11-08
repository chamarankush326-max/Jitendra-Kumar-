package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FeedPost
import com.example.data.model.StoryItem
import com.example.ui.components.Logo3DAnimation
import com.example.ui.components.PostCard
import com.example.ui.components.StoriesBar
import com.example.ui.theme.PeacockCyan
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SocialViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: SocialViewModel) {
  val posts by viewModel.posts.collectAsState()
  val stories by viewModel.stories.collectAsState()
  val notifications by viewModel.notifications.collectAsState()
  val unreadNotifications = notifications.count { !it.isRead }

  var selectedFeedTab by remember { mutableStateOf(0) } // 0: For You, 1: Following, 2: Trending
  var viewedStory by remember { mutableStateOf<StoryItem?>(null) }
  var show3DShowcase by remember { mutableStateOf(false) }

  val filteredPosts = when (selectedFeedTab) {
    1 -> posts.filter { it.isFollowed || it.userId == "user_me" }
    2 -> posts.sortedByDescending { it.likesCount }
    else -> posts
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { show3DShowcase = true }
          ) {
            Logo3DAnimation(
              size = 36.dp,
              speedMultiplier = 1.0f,
              modifier = Modifier.padding(end = 8.dp)
            )
            Column {
              Text(
                text = "Apna Social",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = SaffronPrimary,
                letterSpacing = 0.5.sp
              )
              Text(
                text = "अपना सोशल 🇮🇳",
                style = MaterialTheme.typography.labelSmall,
                color = PeacockCyan,
                fontSize = 10.sp
              )
            }
          }
        },
        actions = {
          // Quick Language Switch
          TextButton(
            onClick = { viewModel.toggleLanguage() },
            modifier = Modifier.testTag("home_lang_toggle")
          ) {
            Text(
              text = if (viewModel.currentLanguage.collectAsState().value.code == "hi") "🇮🇳 HI" else "EN",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.primary
            )
          }

          // Search Button
          IconButton(
            onClick = { viewModel.navigateTo(ScreenDestination.SEARCH) },
            modifier = Modifier.testTag("home_search_btn")
          ) {
            Icon(Icons.Filled.Search, contentDescription = "Search")
          }

          // Recharge Shortcut
          IconButton(
            onClick = { viewModel.navigateTo(ScreenDestination.RECHARGE) },
            modifier = Modifier.testTag("home_recharge_btn")
          ) {
            Icon(
              Icons.Filled.Bolt,
              contentDescription = "Recharge",
              tint = SaffronPrimary
            )
          }

          // Notifications Badge
          IconButton(
            onClick = { viewModel.navigateTo(ScreenDestination.NOTIFICATIONS) },
            modifier = Modifier.testTag("home_notif_btn")
          ) {
            BadgedBox(
              badge = {
                if (unreadNotifications > 0) {
                  Badge { Text("$unreadNotifications") }
                }
              }
            ) {
              Icon(Icons.Outlined.Notifications, contentDescription = "Alerts")
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { viewModel.navigateTo(ScreenDestination.CREATE_POST) },
        containerColor = SaffronPrimary,
        contentColor = Color.White,
        modifier = Modifier.testTag("home_create_post_fab")
      ) {
        Icon(Icons.Filled.Edit, contentDescription = "New Post")
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("home_feed_list"),
      contentPadding = PaddingValues(bottom = 72.dp)
    ) {
      // 1. Stories Tray
      item {
        StoriesBar(
          stories = stories,
          viewModel = viewModel,
          onStoryClick = { story ->
            if (story.userId == "user_me") {
              viewModel.navigateTo(ScreenDestination.CREATE_POST)
            } else {
              viewedStory = story
            }
          }
        )
        Divider(
          thickness = 0.5.dp,
          color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
      }

      // 2. Feed Tabs (For You / Following / Trending)
      item {
        TabRow(
          selectedTabIndex = selectedFeedTab,
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = SaffronPrimary,
          modifier = Modifier.fillMaxWidth().testTag("home_feed_tabs")
        ) {
          Tab(
            selected = selectedFeedTab == 0,
            onClick = { selectedFeedTab = 0 },
            text = { Text(viewModel.getString("tab_for_you"), fontWeight = FontWeight.Bold, fontSize = 13.sp) }
          )
          Tab(
            selected = selectedFeedTab == 1,
            onClick = { selectedFeedTab = 1 },
            text = { Text(viewModel.getString("tab_following"), fontWeight = FontWeight.Bold, fontSize = 13.sp) }
          )
          Tab(
            selected = selectedFeedTab == 2,
            onClick = { selectedFeedTab = 2 },
            text = { Text(viewModel.getString("tab_trending"), fontWeight = FontWeight.Bold, fontSize = 13.sp) }
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
      }

      // 3. Post Feed
      if (filteredPosts.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(48.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                Icons.Outlined.DynamicFeed,
                contentDescription = null,
                modifier = Modifier.size(54.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "No posts found in this tab",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
              )
            }
          }
        }
      } else {
        items(filteredPosts, key = { it.id }) { post ->
          PostCard(
            post = post,
            viewModel = viewModel,
            onCommentClick = { viewModel.openComments(post.id) }
          )
        }
      }
    }
  }

  // Story Viewer Modal Dialog
  if (viewedStory != null) {
    StoryViewerModal(story = viewedStory!!) {
      viewedStory = null
    }
  }

  // 3D Logo Showcase Modal Dialog
  if (show3DShowcase) {
    AlertDialog(
      onDismissRequest = { show3DShowcase = false },
      title = {
        Text("Apna Social 3D Emblem", fontWeight = FontWeight.Bold)
      },
      text = {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          Logo3DAnimation(size = 180.dp, speedMultiplier = 1.3f)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "3D Gyroscope Engine running with smooth isometric hardware acceleration.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
          )
        }
      },
      confirmButton = {
        Button(onClick = { show3DShowcase = false }) {
          Text("Awesome!")
        }
      }
    )
  }
}

@Composable
fun StoryViewerModal(
  story: StoryItem,
  onDismiss: () -> Unit
) {
  var progress by remember { mutableStateOf(0f) }

  LaunchedEffect(Unit) {
    while (progress < 1f) {
      delay(40)
      progress += 0.015f
    }
    onDismiss()
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
      ) {
        // Progress bar
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier.fillMaxWidth().height(4.dp),
          color = SaffronPrimary,
          trackColor = Color.LightGray
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (story.userAvatarRes != null) {
            AsyncImage(
              model = story.userAvatarRes,
              contentDescription = null,
              modifier = Modifier.size(36.dp).clip(CircleShape)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(story.userName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(story.timestamp, fontSize = 11.sp, color = Color.Gray)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (story.imageRes != null) {
          AsyncImage(
            model = story.imageRes,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(12.dp))
          )
        } else {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp)
              .background(SaffronPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(story.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SaffronPrimary)
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Close")
      }
    }
  )
}
