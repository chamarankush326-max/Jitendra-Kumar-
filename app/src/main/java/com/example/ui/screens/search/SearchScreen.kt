package com.example.ui.screens.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FeedPost
import com.example.data.model.UserProfile
import com.example.ui.theme.PeacockCyan
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SocialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SocialViewModel) {
  BackHandler {
    viewModel.navigateBack()
  }

  val searchQuery by viewModel.searchQuery.collectAsState()
  val allPosts by viewModel.posts.collectAsState()
  val allUsers by viewModel.managedUsers.collectAsState()

  var selectedFilter by remember { mutableStateOf(0) } // 0: All, 1: Accounts, 2: Posts

  val trendingTags = listOf(
    "#ApnaIndia", "#TechBharat", "#ReelsViral", "#DesiVibes", "#TajMahal", "#CricketBharat", "#Shayari"
  )

  val filteredUsers = allUsers.filter {
    searchQuery.isEmpty() ||
      it.name.contains(searchQuery, ignoreCase = true) ||
      it.username.contains(searchQuery, ignoreCase = true)
  }

  val filteredPosts = allPosts.filter {
    searchQuery.isEmpty() ||
      it.content.contains(searchQuery, ignoreCase = true) ||
      it.tags.any { tag -> tag.contains(searchQuery, ignoreCase = true) }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = { viewModel.navigateBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        title = {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text(viewModel.getString("search_hint"), fontSize = 13.sp) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("search_input_field"),
            shape = RoundedCornerShape(24.dp),
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                  Icon(Icons.Filled.Clear, contentDescription = "Clear")
                }
              }
            }
          )
        }
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("search_results_list"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Trending Hashtags Row
      item {
        Text(
          text = viewModel.getString("trending_tags"),
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(trendingTags) { tag ->
            SuggestionChip(
              onClick = { viewModel.setSearchQuery(tag) },
              label = { Text(tag, fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
              colors = SuggestionChipDefaults.suggestionChipColors(
                labelColor = SaffronPrimary
              )
            )
          }
        }
      }

      // 2. Filter Tabs
      item {
        TabRow(
          selectedTabIndex = selectedFilter,
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          contentColor = SaffronPrimary,
          modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
          Tab(
            selected = selectedFilter == 0,
            onClick = { selectedFilter = 0 },
            text = { Text("All", fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = selectedFilter == 1,
            onClick = { selectedFilter = 1 },
            text = { Text("People", fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = selectedFilter == 2,
            onClick = { selectedFilter = 2 },
            text = { Text("Posts", fontWeight = FontWeight.Bold) }
          )
        }
      }

      // 3. User Results
      if (selectedFilter == 0 || selectedFilter == 1) {
        item {
          Text(
            text = "Accounts (${filteredUsers.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
        }

        items(filteredUsers, key = { it.id }) { user ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Row(
              modifier = Modifier.padding(12.dp).fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (user.avatarResId != null) {
                AsyncImage(
                  model = user.avatarResId,
                  contentDescription = user.name,
                  modifier = Modifier.size(44.dp).clip(CircleShape)
                )
              } else {
                Box(
                  modifier = Modifier.size(44.dp).background(SaffronPrimary.copy(alpha = 0.2f), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text(user.name.take(1), fontWeight = FontWeight.Bold, color = SaffronPrimary)
                }
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(user.username, fontSize = 12.sp, color = Color.Gray)
                Text(user.bio, fontSize = 11.sp, maxLines = 1)
              }

              Button(
                onClick = { viewModel.repository.toggleFollowUser(user.id) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (user.isFollowedByMe) Color.LightGray else SaffronPrimary
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp)
              ) {
                Text(
                  text = if (user.isFollowedByMe) "Following" else "Follow",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (user.isFollowedByMe) Color.Black else Color.White
                )
              }
            }
          }
        }
      }

      // 4. Posts Results
      if (selectedFilter == 0 || selectedFilter == 2) {
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Posts & Media (${filteredPosts.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
        }

        items(filteredPosts, key = { it.id }) { post ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text(post.content, fontSize = 12.sp, maxLines = 2)
              if (post.imageRes != null) {
                Spacer(modifier = Modifier.height(6.dp))
                AsyncImage(
                  model = post.imageRes,
                  contentDescription = null,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp))
                )
              }
            }
          }
        }
      }
    }
  }
}
