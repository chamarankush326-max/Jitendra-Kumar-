package com.example.ui.screens.post

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.R
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.SocialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(viewModel: SocialViewModel) {
  BackHandler {
    viewModel.navigateBack()
  }

  val currentUser by viewModel.currentUser.collectAsState()
  val uploadProgress by viewModel.uploadProgress.collectAsState()

  var postContent by remember { mutableStateOf("") }
  var selectedLocation by remember { mutableStateOf("New Delhi, India") }
  var selectedImageRes by remember { mutableStateOf<Int?>(R.drawable.post_scenery) }

  val samplePhotos = listOf(
    Pair("Taj Mahal Sunset", R.drawable.post_scenery),
    Pair("Connected Bharat Banner", R.drawable.hero_social_banner),
    Pair("Community Portrait", R.drawable.avatar_user_two)
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(viewModel.getString("create_post_title"), fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = { viewModel.navigateBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          Button(
            onClick = {
              if (postContent.isNotBlank()) {
                viewModel.createPostWithProgress(postContent, selectedLocation)
              } else {
                viewModel.showNotice("Please write something to post")
              }
            },
            enabled = postContent.isNotBlank() && uploadProgress == null,
            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(end = 8.dp).testTag("publish_post_btn")
          ) {
            Text(viewModel.getString("publish_post"), fontWeight = FontWeight.Bold)
          }
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
        .testTag("create_post_screen")
    ) {
      // Upload progress indicator if uploading
      if (uploadProgress != null) {
        Card(
          modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
          colors = CardDefaults.cardColors(containerColor = SaffronPrimary.copy(alpha = 0.12f)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.5.dp,
                color = SaffronPrimary
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "${viewModel.getString("uploading_progress")} (${(uploadProgress!! * 100).toInt()}%)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = SaffronPrimary
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
              progress = { uploadProgress!! },
              modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
              color = SaffronPrimary
            )
          }
        }
      }

      // User Identity Card
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (currentUser.avatarResId != null) {
          AsyncImage(
            model = currentUser.avatarResId,
            contentDescription = currentUser.name,
            modifier = Modifier.size(48.dp).clip(CircleShape)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(currentUser.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          AssistChip(
            onClick = {},
            label = { Text("Public 🌐", fontSize = 11.sp) },
            modifier = Modifier.height(28.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Caption Input
      OutlinedTextField(
        value = postContent,
        onValueChange = { postContent = it },
        placeholder = { Text(viewModel.getString("post_something")) },
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 120.dp)
          .testTag("create_post_caption_input"),
        shape = RoundedCornerShape(14.dp),
        maxLines = 8
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Selected Image Preview
      if (selectedImageRes != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(14.dp))
        ) {
          AsyncImage(
            model = selectedImageRes,
            contentDescription = "Selected photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )

          IconButton(
            onClick = { selectedImageRes = null },
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(8.dp)
              .background(Color.Black.copy(alpha = 0.6f), CircleShape)
          ) {
            Icon(Icons.Filled.Close, contentDescription = "Remove photo", tint = Color.White)
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }

      // Choose Photo from Gallery / Samples
      Text("Attach Photo / Gallery", fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Spacer(modifier = Modifier.height(8.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(samplePhotos) { (label, resId) ->
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(RoundedCornerShape(10.dp))
              .border(
                width = if (selectedImageRes == resId) 2.5.dp else 1.dp,
                color = if (selectedImageRes == resId) SaffronPrimary else Color.LightGray,
                shape = RoundedCornerShape(10.dp)
              )
              .clickable { selectedImageRes = resId }
          ) {
            AsyncImage(
              model = resId,
              contentDescription = label,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Location Input
      Text("Location", fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = selectedLocation,
        onValueChange = { selectedLocation = it },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null, tint = SaffronPrimary) }
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
