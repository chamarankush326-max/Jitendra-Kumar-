package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FeedPost
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoyalIndigo
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SocialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: SocialViewModel) {
  val currentUser by viewModel.currentUser.collectAsState()
  val allPosts by viewModel.posts.collectAsState()
  val allReels by viewModel.reels.collectAsState()

  val myPosts = allPosts.filter { it.userId == currentUser.id }
  val savedPosts = allPosts.filter { it.isSaved }
  val myReels = allReels.filter { it.userId == currentUser.id }

  var selectedTab by remember { mutableStateOf(0) } // 0: Posts, 1: Reels, 2: Saved
  var showEditDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(currentUser.username, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        actions = {
          IconButton(
            onClick = { viewModel.navigateTo(ScreenDestination.SETTINGS) },
            modifier = Modifier.testTag("profile_settings_btn")
          ) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings")
          }
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("profile_screen")
    ) {
      // 1. Profile Header
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Avatar
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(CircleShape)
              .border(2.dp, SaffronPrimary, CircleShape)
          ) {
            if (currentUser.avatarResId != null) {
              AsyncImage(
                model = currentUser.avatarResId,
                contentDescription = currentUser.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
            } else {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(SaffronPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Text(currentUser.name.take(1), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
              }
            }
          }

          Spacer(modifier = Modifier.width(20.dp))

          // Stats: Posts, Followers, Following
          Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            ProfileStatColumn(count = "${myPosts.size}", label = viewModel.getString("posts"))
            ProfileStatColumn(count = "${currentUser.followersCount}", label = viewModel.getString("followers"))
            ProfileStatColumn(count = "${currentUser.followingCount}", label = viewModel.getString("tab_following"))
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Name & Bio
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = currentUser.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          if (currentUser.isVerified) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Filled.CheckCircle,
              contentDescription = "Verified",
              tint = EmeraldSuccess,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = currentUser.bio,
          style = MaterialTheme.typography.bodyMedium,
          fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons Row (Edit Profile, Admin Panel if verified)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = { showEditDialog = true },
            modifier = Modifier.weight(1f).height(38.dp).testTag("edit_profile_btn"),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Text(
              text = viewModel.getString("edit_profile"),
              color = MaterialTheme.colorScheme.onSurface,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }

          OutlinedButton(
            onClick = { viewModel.navigateTo(ScreenDestination.ADMIN) },
            modifier = Modifier.height(38.dp).testTag("admin_panel_shortcut"),
            shape = RoundedCornerShape(20.dp)
          ) {
            Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp), tint = SaffronPrimary)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Tab Row: Posts, Reels, Saved
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = SaffronPrimary,
        modifier = Modifier.fillMaxWidth().testTag("profile_tabs")
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          icon = { Icon(Icons.Filled.GridOn, contentDescription = "Posts") }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          icon = { Icon(Icons.Filled.VideoLibrary, contentDescription = "Reels") }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          icon = { Icon(Icons.Filled.BookmarkBorder, contentDescription = "Saved") }
        )
      }

      // Tab Content Grid
      when (selectedTab) {
        0 -> {
          if (myPosts.isEmpty()) {
            EmptyTabNotice("No posts uploaded yet. Tap '+' to share your first post!")
          } else {
            LazyVerticalGrid(
              columns = GridCells.Fixed(3),
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(1.dp)
            ) {
              items(myPosts, key = { it.id }) { post ->
                Box(
                  modifier = Modifier
                    .aspectRatio(1f)
                    .padding(1.dp)
                    .background(Color.DarkGray)
                ) {
                  if (post.imageRes != null) {
                    AsyncImage(
                      model = post.imageRes,
                      contentDescription = null,
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize()
                    )
                  } else {
                    Box(
                      modifier = Modifier
                        .fillMaxSize()
                        .background(SaffronPrimary.copy(alpha = 0.3f))
                        .padding(8.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(post.content, fontSize = 10.sp, maxLines = 4, textAlign = TextAlign.Center)
                    }
                  }
                }
              }
            }
          }
        }
        1 -> {
          if (myReels.isEmpty()) {
            EmptyTabNotice("No reels created yet. Tap 'Reels' to upload!")
          } else {
            LazyVerticalGrid(
              columns = GridCells.Fixed(3),
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(1.dp)
            ) {
              items(myReels, key = { it.id }) { reel ->
                Box(
                  modifier = Modifier
                    .aspectRatio(0.7f)
                    .padding(1.dp)
                    .background(
                      Brush.verticalGradient(reel.backgroundGradient.map { Color(it) })
                    )
                    .padding(8.dp),
                  contentAlignment = Alignment.BottomStart
                ) {
                  Text(reel.title, color = Color.White, fontSize = 10.sp, maxLines = 2)
                }
              }
            }
          }
        }
        2 -> {
          if (savedPosts.isEmpty()) {
            EmptyTabNotice("No saved posts. Bookmark posts from feed to see them here!")
          } else {
            LazyVerticalGrid(
              columns = GridCells.Fixed(3),
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(1.dp)
            ) {
              items(savedPosts, key = { it.id }) { post ->
                Box(
                  modifier = Modifier
                    .aspectRatio(1f)
                    .padding(1.dp)
                    .background(Color.DarkGray)
                ) {
                  if (post.imageRes != null) {
                    AsyncImage(
                      model = post.imageRes,
                      contentDescription = null,
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize()
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Edit Profile Dialog
  if (showEditDialog) {
    EditProfileDialog(
      name = currentUser.name,
      bio = currentUser.bio,
      handle = currentUser.username,
      onDismiss = { showEditDialog = false },
      onSave = { newName, newBio, newHandle ->
        viewModel.repository.updateProfile(newName, newBio, newHandle)
        showEditDialog = false
        viewModel.showNotice("Profile updated successfully!")
      }
    )
  }
}

@Composable
fun ProfileStatColumn(count: String, label: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = count, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
    Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
  }
}

@Composable
fun EmptyTabNotice(text: String) {
  Box(
    modifier = Modifier.fillMaxSize().padding(32.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(text = text, textAlign = TextAlign.Center, color = Color.Gray, fontSize = 13.sp)
  }
}

@Composable
fun EditProfileDialog(
  name: String,
  bio: String,
  handle: String,
  onDismiss: () -> Unit,
  onSave: (String, String, String) -> Unit
) {
  var editName by remember { mutableStateOf(name) }
  var editBio by remember { mutableStateOf(bio) }
  var editHandle by remember { mutableStateOf(handle) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = editName,
          onValueChange = { editName = it },
          label = { Text("Name") },
          modifier = Modifier.fillMaxWidth().testTag("edit_name_input"),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
          value = editHandle,
          onValueChange = { editHandle = it },
          label = { Text("Username") },
          modifier = Modifier.fillMaxWidth().testTag("edit_handle_input"),
          singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
          value = editBio,
          onValueChange = { editBio = it },
          label = { Text("Bio") },
          modifier = Modifier.fillMaxWidth().testTag("edit_bio_input"),
          maxLines = 3
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onSave(editName, editBio, editHandle) },
        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
      ) {
        Text("Save Changes")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
