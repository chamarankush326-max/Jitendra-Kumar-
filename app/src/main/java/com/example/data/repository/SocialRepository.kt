package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class SocialRepository(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("apna_social_prefs", Context.MODE_PRIVATE)

  // Auth State: Persisted so closing or reopening the app never logs out!
  // User only logs out when explicitly tapping Logout in Settings.
  private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", true))
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  // App Language: Default to English, switchable to Hindi
  private val _currentLanguage = MutableStateFlow(
    if (prefs.getString("app_lang", "en") == "hi") AppLanguage.HINDI else AppLanguage.ENGLISH
  )
  val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

  // Current logged in user profile
  private val _currentUser = MutableStateFlow(
    UserProfile(
      id = "user_me",
      name = prefs.getString("user_name", "Ankush Chamar") ?: "Ankush Chamar",
      username = prefs.getString("user_handle", "@ankush_bharat") ?: "@ankush_bharat",
      bio = prefs.getString("user_bio", "Proud Indian 🇮🇳 | Tech Creator & Explorer | Connecting Bharat") ?: "Proud Indian 🇮🇳 | Tech Creator & Explorer | Connecting Bharat",
      avatarResId = R.drawable.avatar_user_one,
      phone = prefs.getString("user_phone", "+91 98765 43210") ?: "+91 98765 43210",
      email = "chamarankush326@gmail.com",
      followersCount = 1420,
      followingCount = 285,
      postsCount = 4,
      isVerified = true,
      joinedDate = "October 2024"
    )
  )
  val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

  // Stories
  private val _stories = MutableStateFlow<List<StoryItem>>(
    listOf(
      StoryItem("s0", "user_me", "Your Story", R.drawable.avatar_user_one, null, "Add to story", "Just now", false),
      StoryItem("s1", "u1", "Priya S.", R.drawable.avatar_user_two, R.drawable.post_scenery, "Sunset Taj", "1h ago", false),
      StoryItem("s2", "u2", "Rohit K.", R.drawable.avatar_user_one, R.drawable.hero_social_banner, "Festive vibe", "3h ago", false),
      StoryItem("s3", "u3", "Aarav Tech", R.drawable.apna_social_icon, null, "New Update", "5h ago", false)
    )
  )
  val stories: StateFlow<List<StoryItem>> = _stories.asStateFlow()

  // Posts Feed
  private val _posts = MutableStateFlow<List<FeedPost>>(
    listOf(
      FeedPost(
        id = "post_1",
        userId = "u1",
        authorName = "Priya Sharma",
        authorHandle = "@priya_sharma",
        authorAvatarRes = R.drawable.avatar_user_two,
        content = "Witnessing the eternal wonder of the Taj Mahal during golden sunset! Nothing beats the rich heritage and colors of Incredible India 🇮🇳✨ #IncredibleIndia #TajMahal #TravelDiaries",
        imageRes = R.drawable.post_scenery,
        likesCount = 842,
        commentsCount = 24,
        sharesCount = 68,
        isLiked = false,
        isSaved = true,
        isFollowed = true,
        timestamp = "2 hours ago",
        location = "Agra, Uttar Pradesh",
        tags = listOf("#IncredibleIndia", "#TajMahal", "#Bharat"),
        comments = listOf(
          Comment("c1", "u2", "Rohit Verma", "@rohit_v", R.drawable.avatar_user_one, "Magical shot Priya! The reflection pool is so clear! 😍", "1h ago", 5),
          Comment("c2", "u4", "Ananya Roy", "@ananya_r", null, "Jai Hind! Proud of our heritage 🇮🇳", "30m ago", 3)
        )
      ),
      FeedPost(
        id = "post_2",
        userId = "user_me",
        authorName = "Ankush Chamar",
        authorHandle = "@ankush_bharat",
        authorAvatarRes = R.drawable.avatar_user_one,
        content = "Welcome everyone to Apna Social! Built for Bharat to connect communities, share reels, chat in our language and even recharge seamlessly. Let me know what you love most! 🚀📱",
        imageRes = R.drawable.hero_social_banner,
        likesCount = 312,
        commentsCount = 18,
        sharesCount = 45,
        isLiked = true,
        isSaved = false,
        isFollowed = false,
        timestamp = "4 hours ago",
        location = "New Delhi, India",
        tags = listOf("#ApnaSocial", "#DigitalBharat", "#Community"),
        comments = listOf(
          Comment("c3", "u1", "Priya Sharma", "@priya_sharma", R.drawable.avatar_user_two, "Loving the 3D logo and Hindi language toggle! Very smooth 👏", "2h ago", 8)
        )
      ),
      FeedPost(
        id = "post_3",
        userId = "u2",
        authorName = "Rohit Verma",
        authorHandle = "@rohit_v",
        authorAvatarRes = R.drawable.avatar_user_one,
        content = "Evening tea and coding modern Android apps. Life is good when technology empowers millions of people across villages and cities alike! ☕💻 #CodeIndia #DevLife",
        imageRes = null,
        likesCount = 195,
        commentsCount = 9,
        sharesCount = 14,
        isLiked = false,
        isSaved = false,
        isFollowed = false,
        timestamp = "6 hours ago",
        location = "Bengaluru, Karnataka",
        tags = listOf("#CodeIndia", "#DevLife", "#StartupBharat"),
        comments = emptyList()
      )
    )
  )
  val posts: StateFlow<List<FeedPost>> = _posts.asStateFlow()

  // Reels
  private val _reels = MutableStateFlow<List<ReelVideo>>(
    listOf(
      ReelVideo(
        id = "reel_1",
        userId = "u1",
        authorName = "Priya Sharma",
        authorHandle = "@priya_sharma",
        authorAvatarRes = R.drawable.avatar_user_two,
        title = "Discovering hidden architectural gems across Rajasthan! 🏰✨ The folk music and vibrant turbans are pure joy.",
        soundTrack = "Rajasthani Folk Beats - Kesariya Mharo",
        likesCount = 3420,
        commentsCount = 142,
        sharesCount = 580,
        isLiked = true,
        tags = listOf("#Rajasthan", "#DesiVibes", "#TravelIndia"),
        backgroundGradient = listOf(0xFF2A0845, 0xFF6441A5)
      ),
      ReelVideo(
        id = "reel_2",
        userId = "u2",
        authorName = "Rohit Verma",
        authorHandle = "@rohit_v",
        authorAvatarRes = R.drawable.avatar_user_one,
        title = "Street food in Chandni Chowk! Paranthe, Jalebi & Rabri taste test 🔥😋",
        soundTrack = "Delhi 6 Vibes - Dilli 6 Remix",
        likesCount = 5890,
        commentsCount = 310,
        sharesCount = 920,
        isLiked = false,
        tags = listOf("#StreetFood", "#DelhiFoodie", "#ChatoriDilli"),
        backgroundGradient = listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121)
      ),
      ReelVideo(
        id = "reel_3",
        userId = "user_me",
        authorName = "Ankush Chamar",
        authorHandle = "@ankush_bharat",
        authorAvatarRes = R.drawable.avatar_user_one,
        title = "3D Graphics & Jetpack Compose in action on Apna Social! Proudly made in India 🇮🇳",
        soundTrack = "Bharat Anthem - Instrumental",
        likesCount = 2100,
        commentsCount = 88,
        sharesCount = 410,
        isLiked = false,
        tags = listOf("#TechBharat", "#ApnaSocial", "#AndroidDev"),
        backgroundGradient = listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364)
      )
    )
  )
  val reels: StateFlow<List<ReelVideo>> = _reels.asStateFlow()

  // Conversations & Chat
  private val _conversations = MutableStateFlow<List<Conversation>>(
    listOf(
      Conversation(
        id = "conv_1",
        partnerId = "u1",
        partnerName = "Priya Sharma",
        partnerHandle = "@priya_sharma",
        partnerAvatarRes = R.drawable.avatar_user_two,
        lastMessage = "Hey! Did you check out the new recharge tab?",
        lastMessageTime = "10:42 AM",
        unreadCount = 1,
        isOnline = true
      ),
      Conversation(
        id = "conv_2",
        partnerId = "u2",
        partnerName = "Rohit Verma",
        partnerHandle = "@rohit_v",
        partnerAvatarRes = R.drawable.avatar_user_one,
        lastMessage = "Yes, the 3D rotating logo looks super cool!",
        lastMessageTime = "Yesterday",
        unreadCount = 0,
        isOnline = false
      ),
      Conversation(
        id = "conv_3",
        partnerId = "u3",
        partnerName = "Apna Social Support",
        partnerHandle = "@apna_care",
        partnerAvatarRes = R.drawable.apna_social_icon,
        lastMessage = "Welcome to Apna Social! Let us know if you need help.",
        lastMessageTime = "2 days ago",
        unreadCount = 0,
        isOnline = true
      )
    )
  )
  val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

  private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(
    mapOf(
      "conv_1" to listOf(
        ChatMessage("m1", "u1", "user_me", "Namaste Ankush! How are you?", "10:30 AM", isMine = false),
        ChatMessage("m2", "user_me", "u1", "Namaste Priya! Everything is running fantastic on Apna Social.", "10:35 AM", isMine = true),
        ChatMessage("m3", "u1", "user_me", "Hey! Did you check out the new recharge tab?", "10:42 AM", isMine = false)
      ),
      "conv_2" to listOf(
        ChatMessage("m4", "user_me", "u2", "Rohit, check the opening 3D emblem!", "Yesterday", isMine = true),
        ChatMessage("m5", "u2", "user_me", "Yes, the 3D rotating logo looks super cool!", "Yesterday", isMine = false)
      )
    )
  )
  val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

  // Notifications
  private val _notifications = MutableStateFlow<List<AppNotification>>(
    listOf(
      AppNotification("n1", NotificationType.LIKE, "Priya Sharma liked your post", "\"Welcome everyone to Apna Social...\"", "10m ago", false, "post_2"),
      AppNotification("n2", NotificationType.COMMENT, "Rohit Verma commented", "\"Magical shot Priya!...\"", "1h ago", false, "post_1"),
      AppNotification("n3", NotificationType.FOLLOW, "Aarav Tech started following you", "Check their profile & posts", "3h ago", true),
      AppNotification("n4", NotificationType.RECHARGE, "Recharge Successful", "₹299 Jio 1.5GB/day pack activated successfully.", "1 day ago", true)
    )
  )
  val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

  // Recharge Plans
  val rechargePlans: List<RechargePlan> = listOf(
    // Jio
    RechargePlan("j1", OperatorType.JIO, PlanCategory.RECOMMENDED, 299, 28, "1.5 GB/day (42 GB Total)", "Unlimited Voice", "100 SMS/day", listOf("JioCinema", "JioTV", "5G Eligible"), true),
    RechargePlan("j2", OperatorType.JIO, PlanCategory.UNLIMITED, 349, 28, "2.0 GB/day (56 GB Total)", "True 5G Unlimited", "100 SMS/day", listOf("JioCinema Premium", "JioCloud"), true),
    RechargePlan("j3", OperatorType.JIO, PlanCategory.DATA_BOOSTER, 61, 0, "6 GB High-Speed Data", "Existing Plan", "N/A", emptyList(), false),
    RechargePlan("j4", OperatorType.JIO, PlanCategory.ANNUAL, 2999, 365, "2.5 GB/day (912.5 GB)", "Unlimited Voice", "100 SMS/day", listOf("Hotstar Mobile 1 Year", "5G Data"), true),

    // Airtel
    RechargePlan("a1", OperatorType.AIRTEL, PlanCategory.RECOMMENDED, 319, 30, "2.0 GB/day Month Validity", "Truly Unlimited", "100 SMS/day", listOf("Wynk Music", "Free Hellotunes", "Airtel Xstream"), true),
    RechargePlan("a2", OperatorType.AIRTEL, PlanCategory.UNLIMITED, 399, 28, "3.0 GB/day (84 GB)", "Unlimited 5G Data", "100 SMS/day", listOf("Disney+ Hotstar 3M", "Apollo 24|7"), true),
    RechargePlan("a3", OperatorType.AIRTEL, PlanCategory.DATA_BOOSTER, 77, 0, "5 GB Data", "Existing Plan", "N/A", emptyList(), false),
    RechargePlan("a4", OperatorType.AIRTEL, PlanCategory.ANNUAL, 3359, 365, "2.5 GB/day", "Unlimited Calls", "100 SMS/day", listOf("Disney+ Hotstar 1 Year", "Apollo 24|7"), true),

    // Vi
    RechargePlan("v1", OperatorType.VI, PlanCategory.RECOMMENDED, 299, 28, "1.5 GB/day + Binge All Night", "Unlimited", "100 SMS/day", listOf("Binge All Night (12am-6am)", "Weekend Rollover"), true),
    RechargePlan("v2", OperatorType.VI, PlanCategory.UNLIMITED, 479, 56, "1.5 GB/day", "Truly Unlimited", "100 SMS/day", listOf("Vi Movies & TV", "Data Delight"), false),

    // BSNL
    RechargePlan("b1", OperatorType.BSNL, PlanCategory.RECOMMENDED, 199, 30, "2.0 GB/day High Speed", "Unlimited Calling", "100 SMS/day", listOf("Lokdhun", "BSNL Tunes"), true),
    RechargePlan("b2", OperatorType.BSNL, PlanCategory.ANNUAL, 1999, 365, "600 GB High Speed", "Unlimited Calls", "100 SMS/day", listOf("BSNL Care"), true)
  )

  // Recharge Transactions History
  private val _rechargeHistory = MutableStateFlow<List<RechargeTransaction>>(
    listOf(
      RechargeTransaction(
        id = "tx_101",
        mobileNumber = "9876543210",
        operator = OperatorType.JIO,
        amount = 299,
        planDetails = "1.5GB/day - 28 Days Validity",
        timestamp = "05 Nov 2026, 02:15 PM",
        status = "SUCCESS",
        transactionId = "APNAJIO78219"
      ),
      RechargeTransaction(
        id = "tx_102",
        mobileNumber = "9823011223",
        operator = OperatorType.AIRTEL,
        amount = 77,
        planDetails = "5GB Data Booster",
        timestamp = "28 Oct 2026, 11:40 AM",
        status = "SUCCESS",
        transactionId = "APNAAIR44102"
      )
    )
  )
  val rechargeHistory: StateFlow<List<RechargeTransaction>> = _rechargeHistory.asStateFlow()

  // Admin Data
  private val _adminStats = MutableStateFlow(AdminStats())
  val adminStats: StateFlow<AdminStats> = _adminStats.asStateFlow()

  private val _adminReports = MutableStateFlow<List<AdminReport>>(
    listOf(
      AdminReport("rep_1", "Sunil Sharma", "u99", "Suspicious spam link in comments", "Spam & Advertising", "1 hour ago", false),
      AdminReport("rep_2", "Kavita Devi", "u42", "Abusive language in comments", "Harassment & Bullying", "3 hours ago", false),
      AdminReport("rep_3", "Amit Kumar", "u12", "Copyright infringement claim", "Intellectual Property", "1 day ago", true)
    )
  )
  val adminReports: StateFlow<List<AdminReport>> = _adminReports.asStateFlow()

  private val _managedUsers = MutableStateFlow<List<UserProfile>>(
    listOf(
      UserProfile("u1", "Priya Sharma", "@priya_sharma", "Travel Blogger", R.drawable.avatar_user_two, "", "+91 98111 22334", "priya@gmail.com", 12500, 310, 84, isVerified = true, isBanned = false),
      UserProfile("u2", "Rohit Verma", "@rohit_v", "Software Engineer & Foodie", R.drawable.avatar_user_one, "", "+91 98222 33445", "rohit@gmail.com", 4500, 480, 29, isVerified = false, isBanned = false),
      UserProfile("u99", "Spam Bot 3000", "@cheap_deals_xyz", "Earn money fast click link", null, "", "+91 99999 00000", "bot@scam.net", 2, 4000, 95, isVerified = false, isBanned = true)
    )
  )
  val managedUsers: StateFlow<List<UserProfile>> = _managedUsers.asStateFlow()

  // Actions

  fun setLanguage(language: AppLanguage) {
    _currentLanguage.value = language
    prefs.edit().putString("app_lang", language.code).apply()
  }

  fun toggleLanguage() {
    val next = if (_currentLanguage.value == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
    setLanguage(next)
  }

  fun login(name: String = "Ankush Chamar", phone: String = "+91 98765 43210") {
    _isLoggedIn.value = true
    prefs.edit().putBoolean("is_logged_in", true).apply()
    _currentUser.value = _currentUser.value.copy(name = name, phone = phone)
  }

  fun logout() {
    _isLoggedIn.value = false
    prefs.edit().putBoolean("is_logged_in", false).apply()
  }

  fun updateProfile(name: String, bio: String, handle: String) {
    _currentUser.value = _currentUser.value.copy(name = name, bio = bio, username = handle)
    prefs.edit()
      .putString("user_name", name)
      .putString("user_bio", bio)
      .putString("user_handle", handle)
      .apply()
  }

  fun toggleLikePost(postId: String) {
    _posts.value = _posts.value.map { post ->
      if (post.id == postId) {
        val newLiked = !post.isLiked
        val newCount = if (newLiked) post.likesCount + 1 else post.likesCount - 1
        post.copy(isLiked = newLiked, likesCount = maxOf(0, newCount))
      } else post
    }
  }

  fun toggleSavePost(postId: String) {
    _posts.value = _posts.value.map { post ->
      if (post.id == postId) post.copy(isSaved = !post.isSaved) else post
    }
  }

  fun toggleFollowUser(userId: String) {
    _posts.value = _posts.value.map { post ->
      if (post.userId == userId) post.copy(isFollowed = !post.isFollowed) else post
    }
    _managedUsers.value = _managedUsers.value.map { user ->
      if (user.id == userId) user.copy(isFollowedByMe = !user.isFollowedByMe) else user
    }
  }

  fun addComment(postId: String, commentText: String) {
    if (commentText.isBlank()) return
    val user = _currentUser.value
    val newComment = Comment(
      id = "c_${System.currentTimeMillis()}",
      userId = user.id,
      userName = user.name,
      userHandle = user.username,
      userAvatarRes = user.avatarResId,
      text = commentText.trim(),
      timestamp = "Just now"
    )

    _posts.value = _posts.value.map { post ->
      if (post.id == postId) {
        post.copy(
          commentsCount = post.commentsCount + 1,
          comments = listOf(newComment) + post.comments
        )
      } else post
    }
  }

  fun createPost(content: String, location: String = "India", imageRes: Int? = R.drawable.hero_social_banner) {
    val user = _currentUser.value
    val newPost = FeedPost(
      id = "post_${System.currentTimeMillis()}",
      userId = user.id,
      authorName = user.name,
      authorHandle = user.username,
      authorAvatarRes = user.avatarResId,
      content = content,
      imageRes = imageRes,
      likesCount = 0,
      commentsCount = 0,
      sharesCount = 0,
      isLiked = false,
      isSaved = false,
      isFollowed = false,
      timestamp = "Just now",
      location = location,
      tags = listOf("#ApnaSocial", "#Desi"),
      comments = emptyList()
    )
    _posts.value = listOf(newPost) + _posts.value
    _currentUser.value = user.copy(postsCount = user.postsCount + 1)
  }

  fun deletePost(postId: String) {
    _posts.value = _posts.value.filter { it.id != postId }
  }

  fun editPost(postId: String, newContent: String) {
    _posts.value = _posts.value.map { post ->
      if (post.id == postId) post.copy(content = newContent) else post
    }
  }

  fun toggleLikeReel(reelId: String) {
    _reels.value = _reels.value.map { reel ->
      if (reel.id == reelId) {
        val newLiked = !reel.isLiked
        val count = if (newLiked) reel.likesCount + 1 else reel.likesCount - 1
        reel.copy(isLiked = newLiked, likesCount = maxOf(0, count))
      } else reel
    }
  }

  fun addReel(title: String, sound: String = "Original Soundtrack") {
    val user = _currentUser.value
    val newReel = ReelVideo(
      id = "reel_${System.currentTimeMillis()}",
      userId = user.id,
      authorName = user.name,
      authorHandle = user.username,
      authorAvatarRes = user.avatarResId,
      title = title,
      soundTrack = sound,
      likesCount = 1,
      commentsCount = 0,
      sharesCount = 0,
      isLiked = false,
      tags = listOf("#MyReel", "#ApnaSocial"),
      backgroundGradient = listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121)
    )
    _reels.value = listOf(newReel) + _reels.value
  }

  fun sendMessage(conversationId: String, text: String) {
    if (text.isBlank()) return
    val user = _currentUser.value
    val newMsg = ChatMessage(
      id = "m_${System.currentTimeMillis()}",
      senderId = user.id,
      receiverId = "partner",
      text = text.trim(),
      timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
      isMine = true
    )

    val currentList = _messages.value[conversationId] ?: emptyList()
    val updatedMap = _messages.value.toMutableMap()
    updatedMap[conversationId] = currentList + newMsg
    _messages.value = updatedMap

    // update conversation last message
    _conversations.value = _conversations.value.map { conv ->
      if (conv.id == conversationId) {
        conv.copy(lastMessage = text, lastMessageTime = "Just now")
      } else conv
    }
  }

  fun executeRecharge(mobile: String, plan: RechargePlan): RechargeTransaction {
    val tx = RechargeTransaction(
      id = "tx_${System.currentTimeMillis()}",
      mobileNumber = mobile,
      operator = plan.operator,
      amount = plan.price,
      planDetails = "${plan.dataDescription} - ${plan.validityDays} Days",
      timestamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
      status = "SUCCESS",
      transactionId = "APNA${plan.operator.name}${System.currentTimeMillis().toString().takeLast(6)}"
    )
    _rechargeHistory.value = listOf(tx) + _rechargeHistory.value

    // add notification
    val notif = AppNotification(
      id = "notif_${System.currentTimeMillis()}",
      type = NotificationType.RECHARGE,
      title = "Recharge Successful - ₹${plan.price}",
      description = "${plan.operator.displayName} recharge of ₹${plan.price} for $mobile succeeded.",
      timeAgo = "Just now",
      isRead = false
    )
    _notifications.value = listOf(notif) + _notifications.value

    return tx
  }

  // Admin Actions
  fun toggleBanUser(userId: String) {
    _managedUsers.value = _managedUsers.value.map { user ->
      if (user.id == userId) user.copy(isBanned = !user.isBanned) else user
    }
  }

  fun toggleVerifyUser(userId: String) {
    _managedUsers.value = _managedUsers.value.map { user ->
      if (user.id == userId) user.copy(isVerified = !user.isVerified) else user
    }
  }

  fun resolveAdminReport(reportId: String) {
    _adminReports.value = _adminReports.value.map { rep ->
      if (rep.id == reportId) rep.copy(isResolved = true) else rep
    }
    _adminStats.value = _adminStats.value.copy(
      pendingReports = maxOf(0, _adminStats.value.pendingReports - 1)
    )
  }

  fun markNotificationsRead() {
    _notifications.value = _notifications.value.map { it.copy(isRead = true) }
  }

  // Export / Download User Data (uservesh)
  fun generateUserDataExport(): String {
    val u = _currentUser.value
    val myPosts = _posts.value.filter { it.userId == u.id }
    val myRecharges = _rechargeHistory.value

    val sb = StringBuilder()
    sb.appendLine("=================================================")
    sb.appendLine("        APNA SOCIAL - USER DATA EXPORT          ")
    sb.appendLine("=================================================")
    sb.appendLine("Export Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
    sb.appendLine()
    sb.appendLine("--- USER PROFILE ---")
    sb.appendLine("Name: ${u.name}")
    sb.appendLine("Username: ${u.username}")
    sb.appendLine("Bio: ${u.bio}")
    sb.appendLine("Phone: ${u.phone}")
    sb.appendLine("Email: ${u.email}")
    sb.appendLine("Followers: ${u.followersCount} | Following: ${u.followingCount}")
    sb.appendLine("Verified Account: ${u.isVerified}")
    sb.appendLine("Member Since: ${u.joinedDate}")
    sb.appendLine()
    sb.appendLine("--- PUBLISHED POSTS (${myPosts.size}) ---")
    myPosts.forEachIndexed { idx, post ->
      sb.appendLine("${idx + 1}. [${post.timestamp}] ${post.content}")
      sb.appendLine("   Location: ${post.location} | Likes: ${post.likesCount} | Comments: ${post.commentsCount}")
    }
    sb.appendLine()
    sb.appendLine("--- RECHARGE TRANSACTIONS (${myRecharges.size}) ---")
    myRecharges.forEachIndexed { idx, tx ->
      sb.appendLine("${idx + 1}. TxID: ${tx.transactionId} | Amount: ₹${tx.amount} | Mobile: ${tx.mobileNumber} | Status: ${tx.status}")
      sb.appendLine("   Date: ${tx.timestamp} | Details: ${tx.planDetails}")
    }
    sb.appendLine()
    sb.appendLine("=================================================")
    sb.appendLine("Data strictly encrypted and compliant with Privacy Policy.")
    sb.appendLine("=================================================")
    return sb.toString()
  }
}
