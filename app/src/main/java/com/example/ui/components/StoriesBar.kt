package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.StoryItem
import com.example.ui.theme.PeacockCyan
import com.example.ui.theme.SaffronLight
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.SocialViewModel

@Composable
fun StoriesBar(
  stories: List<StoryItem>,
  viewModel: SocialViewModel,
  onStoryClick: (StoryItem) -> Unit
) {
  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 10.dp)
      .testTag("stories_bar"),
    horizontalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(horizontal = 16.dp)
  ) {
    items(stories, key = { it.id }) { story ->
      StoryCircleItem(story = story, viewModel = viewModel, onClick = { onStoryClick(story) })
    }
  }
}

@Composable
fun StoryCircleItem(
  story: StoryItem,
  viewModel: SocialViewModel,
  onClick: () -> Unit
) {
  val isMe = story.userId == "user_me"
  val borderBrush = if (!story.isViewed) {
    Brush.sweepGradient(
      listOf(SaffronPrimary, PeacockCyan, SaffronLight, SaffronPrimary)
    )
  } else {
    Brush.linearGradient(listOf(Color.Gray.copy(alpha = 0.5f), Color.LightGray.copy(alpha = 0.5f)))
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .width(72.dp)
      .clickable(onClick = onClick)
      .testTag("story_item_${story.id}")
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.size(68.dp)
    ) {
      Box(
        modifier = Modifier
          .size(68.dp)
          .border(width = 2.5.dp, brush = borderBrush, shape = CircleShape)
          .padding(4.dp)
      ) {
        if (story.userAvatarRes != null) {
          AsyncImage(
            model = story.userAvatarRes,
            contentDescription = story.userName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxSize()
              .clip(CircleShape)
          )
        } else {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(SaffronPrimary.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = story.userName.take(1).uppercase(),
              fontWeight = FontWeight.Bold,
              color = SaffronPrimary,
              fontSize = 20.sp
            )
          }
        }
      }

      if (isMe) {
        Box(
          modifier = Modifier
            .size(22.dp)
            .align(Alignment.BottomEnd)
            .background(SaffronPrimary, CircleShape)
            .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add Story",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = if (isMe) viewModel.getString("add_story") else story.userName,
      style = MaterialTheme.typography.labelSmall,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      fontSize = 11.sp,
      fontWeight = if (!story.isViewed) FontWeight.SemiBold else FontWeight.Normal
    )
  }
}
