package com.example.data.repository

enum class AppLanguage(val code: String, val title: String, val flag: String) {
  ENGLISH("en", "English", "🇬🇧"),
  HINDI("hi", "हिन्दी", "🇮🇳")
}

object LanguageManager {
  private val translations = mapOf(
    // App Title & Tagline
    "app_name" to mapOf("en" to "Apna Social", "hi" to "अपना सोशल"),
    "app_tagline" to mapOf("en" to "Bharat's Own Social Community", "hi" to "भारत का अपना सोशल नेटवर्क"),

    // Navigation
    "nav_home" to mapOf("en" to "Home", "hi" to "होम"),
    "nav_reels" to mapOf("en" to "Reels", "hi" to "रील्स"),
    "nav_create" to mapOf("en" to "Create", "hi" to "बनाएं"),
    "nav_chat" to mapOf("en" to "Chat", "hi" to "चैट"),
    "nav_profile" to mapOf("en" to "Profile", "hi" to "प्रोफ़ाइल"),
    "nav_recharge" to mapOf("en" to "Recharge", "hi" to "रिचार्ज"),
    "nav_admin" to mapOf("en" to "Admin", "hi" to "एडमिन"),
    "nav_notifications" to mapOf("en" to "Alerts", "hi" to "सूचनाएं"),

    // Home & Feed
    "tab_for_you" to mapOf("en" to "For You", "hi" to "आपके लिए"),
    "tab_following" to mapOf("en" to "Following", "hi" to "फॉलोइंग"),
    "tab_trending" to mapOf("en" to "Trending", "hi" to "ट्रेंडिंग"),
    "like" to mapOf("en" to "Like", "hi" to "पसंद"),
    "liked" to mapOf("en" to "Liked", "hi" to "पसंद किया"),
    "comment" to mapOf("en" to "Comment", "hi" to "टिप्पणी"),
    "share" to mapOf("en" to "Share", "hi" to "साझा करें"),
    "save" to mapOf("en" to "Save", "hi" to "सेव करें"),
    "saved" to mapOf("en" to "Saved", "hi" to "सेव किया"),
    "follow" to mapOf("en" to "Follow", "hi" to "फॉलो"),
    "following" to mapOf("en" to "Following", "hi" to "फॉलो कर रहे हैं"),
    "add_story" to mapOf("en" to "Your Story", "hi" to "आपकी स्टोरी"),
    "post_something" to mapOf("en" to "What's on your mind?", "hi" to "आपके मन में क्या है?"),
    "comments_header" to mapOf("en" to "Comments", "hi" to "टिप्पणियां"),
    "write_comment" to mapOf("en" to "Add a comment...", "hi" to "टिप्पणी लिखें..."),
    "send" to mapOf("en" to "Send", "hi" to "भेजें"),

    // Reels
    "reels_title" to mapOf("en" to "Apna Reels", "hi" to "अपना रील्स"),
    "upload_reel" to mapOf("en" to "Create Reel", "hi" to "रील बनाएं"),
    "audio_original" to mapOf("en" to "Original Audio", "hi" to "मूल ऑडियो"),

    // Create Post
    "create_post_title" to mapOf("en" to "Create Post", "hi" to "नई पोस्ट बनाएं"),
    "post_caption_hint" to mapOf("en" to "Write caption, #hashtags...", "hi" to "कैप्शन और #हैशटैग लिखें..."),
    "add_photo" to mapOf("en" to "Photo / Video", "hi" to "फोटो / वीडियो"),
    "tag_location" to mapOf("en" to "Add Location", "hi" to "स्थान जोड़ें"),
    "publish_post" to mapOf("en" to "Post Now", "hi" to "पोस्ट करें"),
    "uploading_progress" to mapOf("en" to "Uploading post...", "hi" to "पोस्ट अपलोड हो रही है..."),

    // Chat
    "chat_title" to mapOf("en" to "Messages", "hi" to "संदेश"),
    "chat_search_hint" to mapOf("en" to "Search friends...", "hi" to "मित्रों को खोजें..."),
    "chat_input_hint" to mapOf("en" to "Type a message...", "hi" to "संदेश लिखें..."),
    "online" to mapOf("en" to "Online", "hi" to "ऑनलाइन"),

    // Search
    "search_title" to mapOf("en" to "Explore Bharat", "hi" to "भारत एक्सप्लोर करें"),
    "search_hint" to mapOf("en" to "Search users, posts, #tags...", "hi" to "उपयोगकर्ता, पोस्ट या टैग खोजें..."),
    "trending_tags" to mapOf("en" to "Trending Topics", "hi" to "ट्रेंडिंग विषय"),

    // Profile
    "edit_profile" to mapOf("en" to "Edit Profile", "hi" to "प्रोफ़ाइल संपादित करें"),
    "followers" to mapOf("en" to "Followers", "hi" to "फॉलोअर्स"),
    "posts" to mapOf("en" to "Posts", "hi" to "पोस्ट्स"),
    "save_profile" to mapOf("en" to "Save Changes", "hi" to "बदलाव सेव करें"),

    // Recharge
    "recharge_title" to mapOf("en" to "Mobile Recharge", "hi" to "मोबाइल रिचार्ज"),
    "enter_mobile" to mapOf("en" to "Mobile Number", "hi" to "मोबाइल नंबर"),
    "select_operator" to mapOf("en" to "Select Operator", "hi" to "ऑपरेटर चुनें"),
    "view_plans" to mapOf("en" to "Recharge Plans", "hi" to "रिचार्ज प्लान"),
    "recharge_now" to mapOf("en" to "Recharge Now", "hi" to "अभी रिचार्ज करें"),
    "recharge_history" to mapOf("en" to "Recharge History", "hi" to "रिचार्ज इतिहास"),
    "sandbox_warning" to mapOf(
      "en" to "Sandbox Mode: Real payments disabled until authorized gateway configured.",
      "hi" to "डेमो मोड: अधिकृत भुगतान गेटवे कॉन्फ़िगर होने तक वास्तविक भुगतान बंद है।"
    ),
    "recharge_success" to mapOf("en" to "Recharge Successful! Pack Activated.", "hi" to "रिचार्ज सफल! पैक सक्रिय हुआ।"),

    // Admin
    "admin_title" to mapOf("en" to "Admin Moderation Panel", "hi" to "एडमिन मॉडरेशन पैनल"),
    "user_management" to mapOf("en" to "User Accounts", "hi" to "उपयोगकर्ता खाते"),
    "reported_posts" to mapOf("en" to "Reported Content", "hi" to "रिपोर्ट की गई सामग्री"),
    "ban_user" to mapOf("en" to "Ban User", "hi" to "बैन करें"),
    "unban_user" to mapOf("en" to "Unban", "hi" to "अनबैन करें"),
    "remove_post" to mapOf("en" to "Delete Content", "hi" to "सामग्री हटाएं"),
    "resolve_report" to mapOf("en" to "Mark Resolved", "hi" to "हल चिह्नित करें"),

    // Settings
    "settings_title" to mapOf("en" to "Settings", "hi" to "सेटिंग्स"),
    "account_settings" to mapOf("en" to "Account & Security", "hi" to "अकाउंट और सुरक्षा"),
    "privacy_settings" to mapOf("en" to "Privacy", "hi" to "गोपनीयता"),
    "download_data" to mapOf("en" to "Download My Data (Uservesh)", "hi" to "अपना डेटा डाउनलोड करें"),
    "download_data_desc" to mapOf(
      "en" to "Export all your profile info, posts, and activity as a backup file.",
      "hi" to "अपनी प्रोफ़ाइल जानकारी, पोस्ट और गतिविधियों का बैकअप डाउनलोड करें।"
    ),
    "change_password" to mapOf("en" to "Change Password", "hi" to "पासवर्ड बदलें"),
    "logout" to mapOf("en" to "Log Out", "hi" to "लॉग आउट करें"),
    "logout_confirm" to mapOf(
      "en" to "Are you sure you want to log out? Your session is saved securely.",
      "hi" to "क्या आप लॉग आउट करना चाहते हैं? आपका सत्र सुरक्षित रूप से सहेजा गया है।"
    ),
    "delete_account" to mapOf("en" to "Delete Account", "hi" to "खाता हटाएं"),
    "privacy_policy" to mapOf("en" to "Privacy Policy & Terms", "hi" to "गोपनीयता नीति और शर्तें"),

    // Auth
    "login_title" to mapOf("en" to "Welcome to Apna Social", "hi" to "अपना सोशल में स्वागत है"),
    "login_sub" to mapOf("en" to "Connect with friends across Bharat", "hi" to "पूरे भारत के दोस्तों से जुड़ें"),
    "phone_login" to mapOf("en" to "Login with Mobile OTP", "hi" to "मोबाइल ओटीपी से लॉगिन करें"),
    "google_login" to mapOf("en" to "Continue with Google", "hi" to "Google से लॉगिन करें"),
    "get_otp" to mapOf("en" to "Get OTP", "hi" to "ओटीपी पाएं"),
    "enter_otp" to mapOf("en" to "Enter 6-digit OTP", "hi" to "6-अंकों का ओटीपी दर्ज करें"),
    "verify_continue" to mapOf("en" to "Verify & Continue", "hi" to "सत्यापित करें और आगे बढ़ें"),
    "demo_otp_hint" to mapOf("en" to "Demo OTP: 123456", "hi" to "डेमो ओटीपी: 123456")
  )

  fun string(key: String, language: AppLanguage): String {
    return translations[key]?.get(language.code) ?: translations[key]?.get("en") ?: key
  }
}
