package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FeedPost
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.SocialViewModel
import kotlinx.coroutines.delay

@Composable
fun PostCard(
  post: FeedPost,
  viewModel: SocialViewModel,
  onCommentClick: () -> Unit
) {
  var showMenu by remember { mutableStateOf(false) }
  var showEditDialog by remember { mutableStateOf(false) }
  var editedContent by remember { mutableStateOf(post.content) }
  var showHeartAnimation by remember { mutableStateOf(false) }

  val isOwnPost = post.userId == "user_me"

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag("post_card_${post.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // 1. Author Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (post.authorAvatarRes != null) {
          AsyncImage(
            model = post.authorAvatarRes,
            contentDescription = post.authorName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
          )
        } else {
          Box(
            modifier = Modifier
              .size(44.dp)
              .background(SaffronPrimary.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = post.authorName.take(1),
              fontWeight = FontWeight.Bold,
              color = SaffronPrimary,
              fontSize = 18.sp
            )
          }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = post.authorName,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Filled.CheckCircle,
              contentDescription = "Verified",
              tint = EmeraldSuccess,
              modifier = Modifier.size(14.dp)
            )
          }
          Text(
            text = "${post.authorHandle} • ${post.timestamp}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 11.sp
          )
          if (post.location.isNotEmpty()) {
            Text(
              text = "📍 ${post.location}",
              style = MaterialTheme.typography.labelSmall,
              color = SaffronPrimary,
              fontSize = 10.sp
            )
          }
        }

        if (!isOwnPost) {
          OutlinedButton(
            onClick = { viewModel.repository.toggleFollowUser(post.userId) },
            modifier = Modifier.height(32.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
            shape = RoundedCornerShape(20.dp)
          ) {
            Text(
              text = if (post.isFollowed) viewModel.getString("following") else viewModel.getString("follow"),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Box {
          IconButton(onClick = { showMenu = true }) {
            Icon(
              imageVector = Icons.Filled.MoreVert,
              contentDescription = "Options",
              tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
          }

          DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
          ) {
            if (isOwnPost) {
              DropdownMenuItem(
                text = { Text("Edit Caption") },
                onClick = {
                  showMenu = false
                  showEditDialog = true
                },
                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) }
              )
              DropdownMenuItem(
                text = { Text("Delete Post", color = MaterialTheme.colorScheme.error) },
                onClick = {
                  showMenu = false
                  viewModel.repository.deletePost(post.id)
                  viewModel.showNotice("Post deleted")
                },
                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
              )
            } else {
              DropdownMenuItem(
                text = { Text("Report Post") },
                onClick = {
                  showMenu = false
                  viewModel.showNotice("Post reported to moderators")
                },
                leadingIcon = { Icon(Icons.Filled.Report, contentDescription = null) }
              )
            }
          }
        }
      }

      // 2. Post Content
      Text(
        text = post.content,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
        lineHeight = 20.sp
      )

      // 3. Post Image (if available) with Double-Tap to Like
      if (post.imageRes != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp, max = 340.dp)
            .pointerInput(Unit) {
              detectTapGestures(
                onDoubleTap = {
                  if (!post.isLiked) {
                    viewModel.repository.toggleLikePost(post.id)
                  }
                  showHeartAnimation = true
                }
              )
            },
          contentAlignment = Alignment.Center
        ) {
          AsyncImage(
            model = post.imageRes,
            contentDescription = "Post photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxWidth()
              .height(300.dp)
              .clip(RoundedCornerShape(8.dp))
          )

          // Floating double-tap heart animation
          if (showHeartAnimation) {
            LaunchedEffect(showHeartAnimation) {
              delay(800)
              showHeartAnimation = false
            }
            Icon(
              imageVector = Icons.Filled.Favorite,
              contentDescription = null,
              tint = Color.White.copy(alpha = 0.95f),
              modifier = Modifier.size(90.dp)
            )
          }
        }
      }

      // 4. Action Row (Like, Comment, Share, Save)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Like Button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clickable { viewModel.repository.toggleLikePost(post.id) }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("like_button_${post.id}")
        ) {
          Icon(
            imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Like",
            tint = if (post.isLiked) NeonPink else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "${post.likesCount}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Comment Button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clickable(onClick = onCommentClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("comment_button_${post.id}")
        ) {
          Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = "Comment",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "${post.commentsCount}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Share Button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clickable {
              viewModel.showNotice("Link copied to clipboard!")
            }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("share_button_${post.id}")
        ) {
          Icon(
            imageVector = Icons.Outlined.Share,
            contentDescription = "Share",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "${post.sharesCount}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save / Bookmark Button
        IconButton(
          onClick = { viewModel.repository.toggleSavePost(post.id) },
          modifier = Modifier.testTag("save_button_${post.id}")
        ) {
          Icon(
            imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = "Save",
            tint = if (post.isSaved) SaffronPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
          )
        }
      }
    }
  }

  // Edit Caption Dialog
  if (showEditDialog) {
    AlertDialog(
      onDismissRequest = { showEditDialog = false },
      title = { Text("Edit Caption") },
      text = {
        OutlinedTextField(
          value = editedContent,
          onValueChange = { editedContent = it },
          label = { Text("Caption") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 4
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.repository.editPost(post.id, editedContent)
            showEditDialog = false
            viewModel.showNotice("Caption updated!")
          }
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
