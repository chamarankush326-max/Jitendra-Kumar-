package com.example.ui.screens.recharge

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OperatorType
import com.example.data.model.PlanCategory
import com.example.data.model.RechargePlan
import com.example.data.model.RechargeTransaction
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SocialViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeScreen(viewModel: SocialViewModel) {
  BackHandler {
    viewModel.navigateBack()
  }

  var mobileNumber by remember { mutableStateOf("9876543210") }
  var selectedOperator by remember { mutableStateOf(OperatorType.JIO) }
  var selectedCategory by remember { mutableStateOf(PlanCategory.RECOMMENDED) }
  var activePlanForCheckout by remember { mutableStateOf<RechargePlan?>(null) }
  var checkoutSuccessTx by remember { mutableStateOf<RechargeTransaction?>(null) }

  val plans = viewModel.repository.rechargePlans.filter {
    it.operator == selectedOperator && (selectedCategory == PlanCategory.RECOMMENDED || it.category == selectedCategory)
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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Bolt, contentDescription = null, tint = SaffronPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(viewModel.getString("recharge_title"), fontWeight = FontWeight.Bold)
          }
        },
        actions = {
          TextButton(
            onClick = { viewModel.navigateTo(ScreenDestination.RECHARGE_HISTORY) },
            modifier = Modifier.testTag("recharge_history_btn")
          ) {
            Icon(Icons.Filled.History, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(viewModel.getString("recharge_history"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("recharge_screen"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Compliance Notice Card (Real payments disabled)
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SaffronPrimary.copy(alpha = 0.12f)),
          modifier = Modifier.fillMaxWidth().testTag("sandbox_notice_card")
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Filled.Shield,
              contentDescription = null,
              tint = SaffronPrimary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = viewModel.getString("sandbox_warning"),
              style = MaterialTheme.typography.bodySmall,
              color = SaffronPrimary,
              fontWeight = FontWeight.Medium,
              fontSize = 12.sp
            )
          }
        }
      }

      // 2. Mobile Number Input Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = viewModel.getString("enter_mobile"),
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = mobileNumber,
              onValueChange = { if (it.length <= 10) mobileNumber = it },
              modifier = Modifier.fillMaxWidth().testTag("recharge_mobile_input"),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              shape = RoundedCornerShape(12.dp),
              prefix = { Text("+91 ", fontWeight = FontWeight.Bold) },
              trailingIcon = {
                if (mobileNumber.length == 10) {
                  Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                }
              }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Operator Selector Row
            Text(
              text = viewModel.getString("select_operator"),
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              items(OperatorType.values()) { op ->
                OperatorChip(
                  operator = op,
                  isSelected = selectedOperator == op,
                  onClick = { selectedOperator = op }
                )
              }
            }
          }
        }
      }

      // 3. Plan Category Tabs
      item {
        Text(
          text = "${selectedOperator.displayName} Plans",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(PlanCategory.values()) { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = {
                Text(
                  if (viewModel.currentLanguage.collectAsState().value.code == "hi") cat.hindiTitle else cat.englishTitle,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = SaffronPrimary,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }

      // 4. Plans List
      items(plans, key = { it.id }) { plan ->
        PlanCard(
          plan = plan,
          onSelect = { activePlanForCheckout = plan }
        )
      }
    }
  }

  // Checkout Bottom Sheet
  if (activePlanForCheckout != null) {
    RechargeCheckoutModal(
      plan = activePlanForCheckout!!,
      mobileNumber = mobileNumber,
      viewModel = viewModel,
      onDismiss = { activePlanForCheckout = null },
      onSuccess = { tx ->
        activePlanForCheckout = null
        checkoutSuccessTx = tx
      }
    )
  }

  // Success Receipt Dialog
  if (checkoutSuccessTx != null) {
    RechargeSuccessDialog(
      transaction = checkoutSuccessTx!!,
      onDismiss = { checkoutSuccessTx = null }
    )
  }
}

@Composable
fun OperatorChip(
  operator: OperatorType,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val (color, name) = when (operator) {
    OperatorType.JIO -> Pair(JioBlue, "Jio")
    OperatorType.AIRTEL -> Pair(AirtelRed, "Airtel")
    OperatorType.VI -> Pair(ViYellow, "Vi")
    OperatorType.BSNL -> Pair(BsnlOrange, "BSNL")
  }

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant)
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = if (isSelected) color else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 8.dp)
      .testTag("operator_chip_${operator.name}")
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier.size(10.dp).background(color, CircleShape)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = name,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
fun PlanCard(
  plan: RechargePlan,
  onSelect: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("plan_card_${plan.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column {
          Text(
            text = "₹${plan.price}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = SaffronPrimary
          )
          Text(
            text = "${plan.validityDays} Days Validity",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
          )
        }

        if (plan.isPopular) {
          Badge(
            containerColor = PeacockCyan,
            contentColor = Color.Black
          ) {
            Text("POPULAR", fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(2.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
      Spacer(modifier = Modifier.height(10.dp))

      // Benefits Grid
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
          Text("Data", fontSize = 11.sp, color = Color.Gray)
          Text(plan.dataDescription, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
        Column {
          Text("Calls", fontSize = 11.sp, color = Color.Gray)
          Text(plan.voiceCalls, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
        Column {
          Text("SMS", fontSize = 11.sp, color = Color.Gray)
          Text(plan.sms, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
      }

      if (plan.ottBenefits.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Filled.Tv, contentDescription = null, modifier = Modifier.size(16.dp), tint = PeacockCyan)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = plan.ottBenefits.joinToString(" + "),
            fontSize = 11.sp,
            color = PeacockCyan,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("select_plan_btn_${plan.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
      ) {
        Text("Recharge ₹${plan.price}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeCheckoutModal(
  plan: RechargePlan,
  mobileNumber: String,
  viewModel: SocialViewModel,
  onDismiss: () -> Unit,
  onSuccess: (RechargeTransaction) -> Unit
) {
  var isProcessing by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
        .navigationBarsPadding()
        .testTag("recharge_checkout_modal")
    ) {
      Text(
        text = "Confirm Recharge",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(14.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Mobile Number", fontSize = 12.sp, color = Color.Gray)
            Text("+91 $mobileNumber", fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Operator", fontSize = 12.sp, color = Color.Gray)
            Text(plan.operator.displayName, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Plan Amount", fontSize = 12.sp, color = Color.Gray)
            Text("₹${plan.price}", fontWeight = FontWeight.ExtraBold, color = SaffronPrimary)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Benefits", fontSize = 12.sp, color = Color.Gray)
            Text("${plan.dataDescription} (${plan.validityDays}d)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Sandbox Notice
      Text(
        text = "🔒 Sandbox Mode: Real payments disabled until authorized gateway configured.",
        style = MaterialTheme.typography.labelSmall,
        color = SaffronPrimary,
        fontWeight = FontWeight.SemiBold
      )

      Spacer(modifier = Modifier.height(18.dp))

      if (isProcessing) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          CircularProgressIndicator(color = SaffronPrimary, modifier = Modifier.size(28.dp))
          Spacer(modifier = Modifier.width(12.dp))
          Text("Activating Recharge Pack...", fontWeight = FontWeight.Bold)
        }
      } else {
        Button(
          onClick = {
            isProcessing = true
            coroutineScope.launch {
              delay(1200) // Simulated secure processing
              isProcessing = false
              val tx = viewModel.repository.executeRecharge(mobileNumber, plan)
              onSuccess(tx)
            }
          },
          modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_recharge_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
        ) {
          Text("Proceed to Demo Recharge (₹${plan.price})", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun RechargeSuccessDialog(
  transaction: RechargeTransaction,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Box(
        modifier = Modifier.size(56.dp).background(EmeraldSuccess.copy(alpha = 0.15f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(36.dp))
      }
    },
    title = {
      Text("Recharge Successful!", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text("Tx ID: ${transaction.transactionId}", fontSize = 11.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(6.dp))
        Text("₹${transaction.amount}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = SaffronPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("${transaction.operator.displayName} • ${transaction.mobileNumber}", fontWeight = FontWeight.SemiBold)
        Text(transaction.planDetails, fontSize = 12.sp, color = Color.Gray)
      }
    },
    confirmButton = {
      Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)) {
        Text("Done")
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeHistoryScreen(viewModel: SocialViewModel) {
  BackHandler {
    viewModel.navigateBack()
  }

  val history by viewModel.rechargeHistory.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = { viewModel.navigateBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        title = { Text(viewModel.getString("recharge_history"), fontWeight = FontWeight.Bold) }
      )
    }
  ) { innerPadding ->
    if (history.isEmpty()) {
      Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
        Text("No recharge history found", color = Color.Gray)
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("recharge_history_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(history, key = { it.id }) { item ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .background(SaffronPrimary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Filled.ReceiptLong, contentDescription = null, tint = SaffronPrimary)
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "${item.operator.displayName} • ₹${item.amount}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = item.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Mobile: +91 ${item.mobileNumber} | TxID: ${item.transactionId}",
                  fontSize = 11.sp,
                  color = Color.Gray
                )
                Text(
                  text = "${item.planDetails} • ${item.timestamp}",
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
              }
            }
          }
        }
      }
    }
  }
}
