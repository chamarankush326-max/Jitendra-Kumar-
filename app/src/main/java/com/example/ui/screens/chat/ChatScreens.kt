package com.example.ui.screens.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoyalIndigo
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SocialViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(viewModel: SocialViewModel) {
  val conversations by viewModel.conversations.collectAsState()
  var searchQuery by remember { mutableStateOf("") }

  val filteredConversations = conversations.filter {
    searchQuery.isEmpty() ||
      it.partnerName.contains(searchQuery, ignoreCase = true) ||
      it.lastMessage.contains(searchQuery, ignoreCase = true)
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(viewModel.getString("chat_title"), fontWeight = FontWeight.Bold) },
        actions = {
          IconButton(onClick = { viewModel.showNotice("Direct link invite copied!") }) {
            Icon(Icons.Filled.PersonAdd, contentDescription = "Add Contact")
          }
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("chat_list_screen")
    ) {
      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text(viewModel.getString("chat_search_hint")) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) }
      )

      if (filteredConversations.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Text("No active conversations found", color = Color.Gray)
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(bottom = 72.dp)
        ) {
          items(filteredConversations, key = { it.id }) { conv ->
            ConversationRow(
              conversation = conv,
              onClick = { viewModel.openChat(conv.id) }
            )
            Divider(
              modifier = Modifier.padding(start = 76.dp),
              color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
              thickness = 0.5.dp
            )
          }
        }
      }
    }
  }
}

@Composable
fun ConversationRow(
  conversation: Conversation,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("conversation_row_${conversation.id}"),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box {
      if (conversation.partnerAvatarRes != null) {
        AsyncImage(
          model = conversation.partnerAvatarRes,
          contentDescription = conversation.partnerName,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
        )
      } else {
        Box(
          modifier = Modifier
            .size(52.dp)
            .background(SaffronPrimary.copy(alpha = 0.2f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(conversation.partnerName.take(1), fontWeight = FontWeight.Bold, color = SaffronPrimary, fontSize = 20.sp)
        }
      }

      // Online Indicator
      if (conversation.isOnline) {
        Box(
          modifier = Modifier
            .size(14.dp)
            .background(EmeraldSuccess, CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            .align(Alignment.BottomEnd)
        )
      }
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = conversation.partnerName,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = conversation.lastMessageTime,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = conversation.lastMessage,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
          maxLines = 1,
          modifier = Modifier.weight(1f)
        )

        if (conversation.unreadCount > 0) {
          Box(
            modifier = Modifier
              .background(SaffronPrimary, CircleShape)
              .padding(horizontal = 7.dp, vertical = 2.dp)
          ) {
            Text(
              text = "${conversation.unreadCount}",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(viewModel: SocialViewModel) {
  BackHandler {
    viewModel.navigateBack()
  }

  val activeId by viewModel.activeConversationId.collectAsState()
  val conversations by viewModel.conversations.collectAsState()
  val allMessages by viewModel.messages.collectAsState()

  val conv = conversations.find { it.id == activeId }
  val messageList = allMessages[activeId] ?: emptyList()

  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = { viewModel.navigateBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (conv?.partnerAvatarRes != null) {
              AsyncImage(
                model = conv.partnerAvatarRes,
                contentDescription = null,
                modifier = Modifier.size(38.dp).clip(CircleShape)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = conv?.partnerName ?: "Chat",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (conv?.isOnline == true) viewModel.getString("online") else "Offline",
                style = MaterialTheme.typography.labelSmall,
                color = if (conv?.isOnline == true) EmeraldSuccess else Color.Gray,
                fontSize = 11.sp
              )
            }
          }
        },
        actions = {
          IconButton(onClick = { viewModel.showNotice("Calling ${conv?.partnerName}...") }) {
            Icon(Icons.Filled.Call, contentDescription = "Audio Call")
          }
          IconButton(onClick = { viewModel.showNotice("Starting Video Call...") }) {
            Icon(Icons.Filled.Videocam, contentDescription = "Video Call")
          }
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("chat_detail_screen")
    ) {
      // Message Bubbles
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(messageList, key = { it.id }) { msg ->
          MessageBubble(message = msg)
        }
      }

      // Input Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp)
          .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = { viewModel.showNotice("Photo attach dialog") }) {
          Icon(Icons.Filled.AttachFile, contentDescription = "Attach", tint = Color.Gray)
        }

        OutlinedTextField(
          value = inputText,
          onValueChange = { inputText = it },
          placeholder = { Text(viewModel.getString("chat_input_hint")) },
          modifier = Modifier
            .weight(1f)
            .testTag("chat_message_input"),
          shape = RoundedCornerShape(24.dp),
          maxLines = 4
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = {
            if (inputText.isNotBlank() && activeId != null) {
              viewModel.repository.sendMessage(activeId!!, inputText)
              inputText = ""
              coroutineScope.launch {
                listState.animateScrollToItem(messageList.size)
              }
            }
          },
          modifier = Modifier
            .background(SaffronPrimary, CircleShape)
            .testTag("send_chat_message_btn")
        ) {
          Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White)
        }
      }
    }
  }
}

@Composable
fun MessageBubble(message: ChatMessage) {
  val alignEnd = message.isMine
  Box(
    modifier = Modifier.fillMaxWidth(),
    contentAlignment = if (alignEnd) Alignment.CenterEnd else Alignment.CenterStart
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 280.dp)
        .background(
          color = if (alignEnd) SaffronPrimary else MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (alignEnd) 16.dp else 4.dp,
            bottomEnd = if (alignEnd) 4.dp else 16.dp
          )
        )
        .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
      Text(
        text = message.text,
        color = if (alignEnd) Color.White else MaterialTheme.colorScheme.onSurface,
        fontSize = 14.sp
      )
      Spacer(modifier = Modifier.height(2.dp))
      Row(
        modifier = Modifier.align(Alignment.End),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = message.timestamp,
          fontSize = 10.sp,
          color = if (alignEnd) Color.White.copy(alpha = 0.7f) else Color.Gray
        )
        if (alignEnd) {
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.Filled.DoneAll,
            contentDescription = "Read",
            tint = Color.White,
            modifier = Modifier.size(12.dp)
          )
        }
      }
    }
  }
}
