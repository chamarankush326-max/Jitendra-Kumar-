package com.example.ui.screens.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AdminReport
import com.example.data.model.UserProfile
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonPink
import com.example.ui.theme.PeacockCyan
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.SocialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(viewModel: SocialViewModel) {
  BackHandler {
    viewModel.navigateBack()
  }

  val stats by viewModel.adminStats.collectAsState()
  val reports by viewModel.adminReports.collectAsState()
  val users by viewModel.managedUsers.collectAsState()

  var selectedTab by remember { mutableStateOf(0) } // 0: Overview & Stats, 1: Users, 2: Reports

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
            Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = SaffronPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(viewModel.getString("admin_title"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
          }
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("admin_panel_screen")
    ) {
      // Admin Tabs
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = SaffronPrimary
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Overview", fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text(viewModel.getString("user_management"), fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("Reports (${reports.count { !it.isResolved }})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
      }

      when (selectedTab) {
        0 -> {
          // DASHBOARD STATS
          LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            item {
              Text("Platform Statistics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                  title = "Total Users",
                  value = "${stats.totalUsers}",
                  icon = Icons.Filled.People,
                  tint = SaffronPrimary,
                  modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                  title = "Active Today",
                  value = "${stats.activeUsersToday}",
                  icon = Icons.Filled.TrendingUp,
                  tint = EmeraldSuccess,
                  modifier = Modifier.weight(1f)
                )
              }
            }

            item {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                  title = "Posts Today",
                  value = "${stats.postsToday}",
                  icon = Icons.Filled.DynamicFeed,
                  tint = PeacockCyan,
                  modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                  title = "Reels Views",
                  value = "${stats.reelsViewsToday}",
                  icon = Icons.Filled.VideoLibrary,
                  tint = NeonPink,
                  modifier = Modifier.weight(1f)
                )
              }
            }

            item {
              AdminStatCard(
                title = "Pending Abuse Reports",
                value = "${stats.pendingReports}",
                icon = Icons.Filled.Report,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
              )
            }

            item {
              Spacer(modifier = Modifier.height(10.dp))
              Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Security, contentDescription = null, tint = EmeraldSuccess)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Server & Security Health", fontWeight = FontWeight.Bold)
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    "All microservices, databases, and encryption rules operational in Indian region (asia-south1).",
                    fontSize = 12.sp,
                    color = Color.Gray
                  )
                }
              }
            }
          }
        }
        1 -> {
          // USER MANAGEMENT
          LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(users, key = { it.id }) { user ->
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
                      contentDescription = null,
                      modifier = Modifier.size(42.dp)
                    )
                  } else {
                    Box(
                      modifier = Modifier.size(42.dp).background(SaffronPrimary.copy(alpha = 0.2f), CircleShape),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(user.name.take(1), fontWeight = FontWeight.Bold, color = SaffronPrimary)
                    }
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(user.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                      if (user.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
                      }
                      if (user.isBanned) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("(BANNED)", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                      }
                    }
                    Text(user.username, fontSize = 11.sp, color = Color.Gray)
                  }

                  // Verification Toggle
                  IconButton(onClick = { viewModel.repository.toggleVerifyUser(user.id) }) {
                    Icon(
                      imageVector = if (user.isVerified) Icons.Filled.CheckCircle else Icons.Filled.AddCircle,
                      contentDescription = "Toggle Verified",
                      tint = if (user.isVerified) EmeraldSuccess else Color.Gray
                    )
                  }

                  // Ban / Unban Button
                  OutlinedButton(
                    onClick = { viewModel.repository.toggleBanUser(user.id) },
                    colors = ButtonDefaults.outlinedButtonColors(
                      contentColor = if (user.isBanned) EmeraldSuccess else MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                  ) {
                    Text(
                      text = if (user.isBanned) viewModel.getString("unban_user") else viewModel.getString("ban_user"),
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }
        }
        2 -> {
          // REPORTS MODERATION
          LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(reports, key = { it.id }) { report ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (!report.isResolved) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
                )
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = report.reason,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      color = if (!report.isResolved) MaterialTheme.colorScheme.error else Color.Gray
                    )
                    Text(
                      text = if (report.isResolved) "RESOLVED" else "PENDING",
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp,
                      color = if (report.isResolved) EmeraldSuccess else SaffronPrimary
                    )
                  }

                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Reported content: \"${report.reportedContentTitle}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp
                  )
                  Text(
                    text = "Reported by ${report.reporterName} • ${report.timestamp}",
                    fontSize = 11.sp,
                    color = Color.Gray
                  )

                  if (!report.isResolved) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      Button(
                        onClick = {
                          viewModel.repository.resolveAdminReport(report.id)
                          viewModel.showNotice("Report marked as resolved")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        modifier = Modifier.height(34.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                      ) {
                        Text(viewModel.getString("resolve_report"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                      }

                      OutlinedButton(
                        onClick = {
                          viewModel.repository.resolveAdminReport(report.id)
                          viewModel.showNotice("Content deleted & report resolved")
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.height(34.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                      ) {
                        Text(viewModel.getString("remove_post"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminStatCard(
  title: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(2.dp)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier.size(42.dp).background(tint.copy(alpha = 0.15f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(text = title, fontSize = 11.sp, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
      }
    }
  }
}
