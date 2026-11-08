package com.example.data.model

data class UserProfile(
  val id: String,
  val name: String,
  val username: String, // e.g. @rohit_kumar
  val bio: String,
  val avatarResId: Int? = null,
  val avatarUrl: String = "",
  val phone: String = "+91 98765 43210",
  val email: String = "user@apnasocial.in",
  val followersCount: Int = 1240,
  val followingCount: Int = 312,
  val postsCount: Int = 18,
  val isVerified: Boolean = false,
  val isFollowedByMe: Boolean = false,
  val isBanned: Boolean = false,
  val joinedDate: String = "Jan 2024",
  val isPrivate: Boolean = false
)

data class Comment(
  val id: String,
  val userId: String,
  val userName: String,
  val userHandle: String,
  val userAvatarRes: Int? = null,
  val text: String,
  val timestamp: String,
  val likesCount: Int = 0
)

data class FeedPost(
  val id: String,
  val userId: String,
  val authorName: String,
  val authorHandle: String,
  val authorAvatarRes: Int? = null,
  val content: String,
  val imageRes: Int? = null,
  val imageUrl: String = "",
  val likesCount: Int = 42,
  val commentsCount: Int = 6,
  val sharesCount: Int = 12,
  val isLiked: Boolean = false,
  val isSaved: Boolean = false,
  val isFollowed: Boolean = false,
  val timestamp: String = "Just now",
  val location: String = "New Delhi, India",
  val tags: List<String> = emptyList(),
  val comments: List<Comment> = emptyList()
)

data class StoryItem(
  val id: String,
  val userId: String,
  val userName: String,
  val userAvatarRes: Int? = null,
  val imageRes: Int? = null,
  val title: String = "",
  val timestamp: String = "2h ago",
  val isViewed: Boolean = false
)

data class ReelVideo(
  val id: String,
  val userId: String,
  val authorName: String,
  val authorHandle: String,
  val authorAvatarRes: Int? = null,
  val title: String,
  val soundTrack: String,
  val likesCount: Int = 1520,
  val commentsCount: Int = 89,
  val sharesCount: Int = 240,
  val isLiked: Boolean = false,
  val isSaved: Boolean = false,
  val isFollowing: Boolean = false,
  val tags: List<String> = listOf("#ApnaReels", "#DesiVibes", "#Trending"),
  val backgroundGradient: List<Long> = listOf(0xFF2A0845, 0xFF6441A5)
)

data class ChatMessage(
  val id: String,
  val senderId: String,
  val receiverId: String,
  val text: String,
  val timestamp: String,
  val isMine: Boolean,
  val isRead: Boolean = true
)

data class Conversation(
  val id: String,
  val partnerId: String,
  val partnerName: String,
  val partnerHandle: String,
  val partnerAvatarRes: Int? = null,
  val lastMessage: String,
  val lastMessageTime: String,
  val unreadCount: Int = 0,
  val isOnline: Boolean = false
)

enum class NotificationType {
  LIKE, COMMENT, FOLLOW, RECHARGE, SYSTEM
}

data class AppNotification(
  val id: String,
  val type: NotificationType,
  val title: String,
  val description: String,
  val timeAgo: String,
  val isRead: Boolean = false,
  val targetId: String = ""
)

enum class OperatorType(val displayName: String, val circleDefault: String) {
  JIO("Jio 5G", "Delhi NCR"),
  AIRTEL("Airtel 5G Plus", "Delhi NCR"),
  VI("Vodafone Idea (Vi)", "Delhi NCR"),
  BSNL("BSNL 4G", "Delhi NCR")
}

enum class PlanCategory(val englishTitle: String, val hindiTitle: String) {
  RECOMMENDED("Recommended", "अनुशंसित"),
  UNLIMITED("Unlimited Calls", "अनलिमिटेड"),
  DATA_BOOSTER("Data Packs", "डेटा पैक"),
  ANNUAL("Annual Packs", "सालाना पैक")
}

data class RechargePlan(
  val id: String,
  val operator: OperatorType,
  val category: PlanCategory,
  val price: Int, // ₹
  val validityDays: Int,
  val dataDescription: String,
  val voiceCalls: String,
  val sms: String,
  val ottBenefits: List<String> = emptyList(),
  val isPopular: Boolean = false
)

data class RechargeTransaction(
  val id: String,
  val mobileNumber: String,
  val operator: OperatorType,
  val amount: Int,
  val planDetails: String,
  val timestamp: String,
  val status: String = "SUCCESS",
  val transactionId: String
)

data class AdminReport(
  val id: String,
  val reporterName: String,
  val reportedUserId: String,
  val reportedContentTitle: String,
  val reason: String,
  val timestamp: String,
  val isResolved: Boolean = false
)

data class AdminStats(
  val totalUsers: Int = 18450,
  val activeUsersToday: Int = 5420,
  val postsToday: Int = 1290,
  val reelsViewsToday: Int = 48720,
  val pendingReports: Int = 3
)
