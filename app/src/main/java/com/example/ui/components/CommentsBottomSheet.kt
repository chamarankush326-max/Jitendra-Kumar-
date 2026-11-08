package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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
import com.example.data.model.Comment
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.SocialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
  postId: String,
  viewModel: SocialViewModel,
  onDismiss: () -> Unit
) {
  val post = viewModel.posts.collectAsState().value.find { it.id == postId }
  var commentText by remember { mutableStateOf("") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.7f)
        .padding(horizontal = 16.dp)
        .testTag("comments_bottom_sheet")
    ) {
      Text(
        text = "${viewModel.getString("comments_header")} (${post?.comments?.size ?: 0})",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
      )

      Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

      if (post == null || post.comments.isEmpty()) {
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No comments yet. Be the first to share your thoughts!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(post.comments, key = { it.id }) { comment ->
            CommentRowItem(comment = comment)
          }
        }
      }

      // Input Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp)
          .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = commentText,
          onValueChange = { commentText = it },
          placeholder = { Text(viewModel.getString("write_comment")) },
          modifier = Modifier
            .weight(1f)
            .testTag("comment_input_field"),
          shape = RoundedCornerShape(24.dp),
          maxLines = 3
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = {
            if (commentText.isNotBlank()) {
              viewModel.repository.addComment(postId, commentText)
              commentText = ""
            }
          },
          enabled = commentText.isNotBlank(),
          modifier = Modifier
            .background(
              if (commentText.isNotBlank()) SaffronPrimary else Color.Gray.copy(alpha = 0.3f),
              CircleShape
            )
            .testTag("submit_comment_button")
        ) {
          Icon(
            imageVector = Icons.Filled.Send,
            contentDescription = "Send",
            tint = Color.White
          )
        }
      }
    }
  }
}

@Composable
fun CommentRowItem(comment: Comment) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top
  ) {
    if (comment.userAvatarRes != null) {
      AsyncImage(
        model = comment.userAvatarRes,
        contentDescription = comment.userName,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
      )
    } else {
      Box(
        modifier = Modifier
          .size(36.dp)
          .background(SaffronPrimary.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = comment.userName.take(1),
          fontWeight = FontWeight.Bold,
          color = SaffronPrimary,
          fontSize = 14.sp
        )
      }
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = comment.userName,
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = comment.timestamp,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
          fontSize = 10.sp
        )
      }

      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = comment.text,
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 13.sp
      )
    }
  }
}
