package com.example.ui.screens.reels

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ReelVideo
import com.example.ui.theme.NeonPink
import com.example.ui.theme.PeacockCyan
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.SocialViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelsScreen(viewModel: SocialViewModel) {
  val reels by viewModel.reels.collectAsState()
  var showUploadSheet by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.PlayCircle,
              contentDescription = null,
              tint = SaffronPrimary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = viewModel.getString("reels_title"),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }
        },
        actions = {
          Button(
            onClick = { showUploadSheet = true },
            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
              .padding(end = 8.dp)
              .height(36.dp)
              .testTag("upload_reel_btn")
          ) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(viewModel.getString("upload_reel"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("reels_list"),
      contentPadding = PaddingValues(bottom = 80.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      items(reels, key = { it.id }) { reel ->
        ReelCardItem(reel = reel, viewModel = viewModel)
      }
    }
  }

  if (showUploadSheet) {
    CreateReelBottomSheet(
      viewModel = viewModel,
      onDismiss = { showUploadSheet = false }
    )
  }
}

@Composable
fun ReelCardItem(
  reel: ReelVideo,
  viewModel: SocialViewModel
) {
  var isPlaying by remember { mutableStateOf(true) }
  var showPauseIcon by remember { mutableStateOf(false) }

  // Spinning disc animation for audio track
  val infiniteTransition = rememberInfiniteTransition(label = "DiscRotation")
  val discAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "discAngle"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .height(480.dp)
      .padding(horizontal = 12.dp)
      .clip(RoundedCornerShape(20.dp))
      .testTag("reel_card_${reel.id}"),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            reel.backgroundGradient.map { Color(it) }
          )
        )
        .pointerInput(Unit) {
          detectTapGestures(
            onTap = {
              isPlaying = !isPlaying
              showPauseIcon = true
            }
          )
        }
    ) {
      // Pause / Play overlay icon
      if (showPauseIcon) {
        LaunchedEffect(showPauseIcon) {
          delay(700)
          showPauseIcon = false
        }
        Icon(
          imageVector = if (isPlaying) Icons.Filled.PlayArrow else Icons.Filled.Pause,
          contentDescription = null,
          tint = Color.White.copy(alpha = 0.85f),
          modifier = Modifier
            .size(72.dp)
            .align(Alignment.Center)
            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            .padding(16.dp)
        )
      }

      // Playing indicator badge at top left
      Row(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(14.dp)
          .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .background(if (isPlaying) Color.Green else Color.Red, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isPlaying) "Playing" else "Paused",
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      // Bottom Content Overlay (Author, Caption, Sound)
      Column(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .fillMaxWidth(0.78f)
          .padding(16.dp)
      ) {
        // Author Row
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (reel.authorAvatarRes != null) {
            AsyncImage(
              model = reel.authorAvatarRes,
              contentDescription = reel.authorName,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, Color.White, CircleShape)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = reel.authorName,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = reel.authorHandle,
              color = Color.White.copy(alpha = 0.7f),
              fontSize = 11.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Caption & Tags
        Text(
          text = reel.title,
          color = Color.White,
          fontSize = 13.sp,
          lineHeight = 18.sp,
          maxLines = 3
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = reel.tags.joinToString(" "),
          color = PeacockCyan,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Sound Track
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.MusicNote,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = reel.soundTrack,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp,
            maxLines = 1
          )
        }
      }

      // Right Side Action Column (Likes, Comments, Shares, Spinning Disc)
      Column(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(end = 12.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Like Button
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          IconButton(
            onClick = { viewModel.repository.toggleLikeReel(reel.id) },
            modifier = Modifier
              .background(Color.Black.copy(alpha = 0.35f), CircleShape)
              .size(44.dp)
              .testTag("reel_like_${reel.id}")
          ) {
            Icon(
              imageVector = if (reel.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
              contentDescription = "Like",
              tint = if (reel.isLiked) NeonPink else Color.White
            )
          }
          Text(
            text = "${reel.likesCount}",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Comment Button
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          IconButton(
            onClick = { viewModel.showNotice("Comments enabled for this reel") },
            modifier = Modifier
              .background(Color.Black.copy(alpha = 0.35f), CircleShape)
              .size(44.dp)
          ) {
            Icon(
              imageVector = Icons.Filled.ChatBubbleOutline,
              contentDescription = "Comment",
              tint = Color.White
            )
          }
          Text(
            text = "${reel.commentsCount}",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Share Button
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          IconButton(
            onClick = { viewModel.showNotice("Reel link copied!") },
            modifier = Modifier
              .background(Color.Black.copy(alpha = 0.35f), CircleShape)
              .size(44.dp)
          ) {
            Icon(
              imageVector = Icons.Filled.Share,
              contentDescription = "Share",
              tint = Color.White
            )
          }
          Text(
            text = "${reel.sharesCount}",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Rotating Music Disc
        Box(
          modifier = Modifier
            .size(42.dp)
            .rotate(if (isPlaying) discAngle else 0f)
            .background(Color.Black, CircleShape)
            .border(2.dp, SaffronPrimary, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Album,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReelBottomSheet(
  viewModel: SocialViewModel,
  onDismiss: () -> Unit
) {
  var reelTitle by remember { mutableStateOf("") }
  var soundTrack by remember { mutableStateOf("Desi Beats • Original Audio") }
  var isUploading by remember { mutableStateOf(false) }
  var progress by remember { mutableStateOf(0f) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
        .navigationBarsPadding()
        .testTag("upload_reel_sheet")
    ) {
      Text(
        text = viewModel.getString("upload_reel"),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = reelTitle,
        onValueChange = { reelTitle = it },
        label = { Text("Reel Caption & Description") },
        placeholder = { Text("What is this video about? #reels") },
        modifier = Modifier.fillMaxWidth().testTag("reel_caption_input"),
        shape = RoundedCornerShape(12.dp),
        maxLines = 3
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = soundTrack,
        onValueChange = { soundTrack = it },
        label = { Text("Audio Soundtrack") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(Icons.Filled.Audiotrack, contentDescription = null) }
      )

      Spacer(modifier = Modifier.height(16.dp))

      if (isUploading) {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Uploading Reel (${(progress * 100).toInt()}%)...",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = SaffronPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = SaffronPrimary
          )
        }
      } else {
        Button(
          onClick = {
            if (reelTitle.isNotBlank()) {
              isUploading = true
              progress = 0.2f
              // Coroutine progress simulation
              val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
              scope.launch {
                delay(400)
                progress = 0.65f
                delay(500)
                progress = 1.0f
                delay(300)
                viewModel.repository.addReel(reelTitle, soundTrack)
                viewModel.showNotice("Reel uploaded successfully!")
                onDismiss()
              }
            } else {
              viewModel.showNotice("Please enter a caption")
            }
          },
          modifier = Modifier.fillMaxWidth().height(48.dp).testTag("publish_reel_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
        ) {
          Icon(Icons.Filled.CloudUpload, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Upload Reel Now", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
