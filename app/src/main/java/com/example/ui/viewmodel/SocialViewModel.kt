package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.AppLanguage
import com.example.data.repository.LanguageManager
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ScreenDestination {
  SPLASH,
  AUTH,
  HOME,
  REELS,
  CREATE_POST,
  CHAT_LIST,
  CHAT_DETAIL,
  NOTIFICATIONS,
  RECHARGE,
  RECHARGE_HISTORY,
  SEARCH,
  PROFILE,
  SETTINGS,
  ADMIN,
  PRIVACY_POLICY
}

class SocialViewModel(application: Application) : AndroidViewModel(application) {

  val repository = SocialRepository(application)

  val isLoggedIn = repository.isLoggedIn
  val currentLanguage = repository.currentLanguage
  val currentUser = repository.currentUser
  val stories = repository.stories
  val posts = repository.posts
  val reels = repository.reels
  val conversations = repository.conversations
  val messages = repository.messages
  val notifications = repository.notifications
  val rechargeHistory = repository.rechargeHistory
  val adminStats = repository.adminStats
  val adminReports = repository.adminReports
  val managedUsers = repository.managedUsers

  // Current active navigation screen
  private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.SPLASH)
  val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

  // Back stack for custom screen navigation
  private val screenStack = mutableListOf<ScreenDestination>()

  // Active chat conversation
  private val _activeConversationId = MutableStateFlow<String?>(null)
  val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()

  // Active post for comments modal
  private val _activeCommentPostId = MutableStateFlow<String?>(null)
  val activeCommentPostId: StateFlow<String?> = _activeCommentPostId.asStateFlow()

  // Selected recharge plan for checkout sheet
  private val _selectedRechargePlan = MutableStateFlow<RechargePlan?>(null)
  val selectedRechargePlan: StateFlow<RechargePlan?> = _selectedRechargePlan.asStateFlow()

  // Post upload progress simulation
  private val _uploadProgress = MutableStateFlow<Float?>(null)
  val uploadProgress: StateFlow<Float?> = _uploadProgress.asStateFlow()

  // Search query
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  // User Data Export Dialog / Sheet text
  private val _exportedDataText = MutableStateFlow<String?>(null)
  val exportedDataText: StateFlow<String?> = _exportedDataText.asStateFlow()

  // Toast / Snackbar message
  private val _uiNotice = MutableStateFlow<String?>(null)
  val uiNotice: StateFlow<String?> = _uiNotice.asStateFlow()

  init {
    // Splash screen animation timer, then route according to login state
    viewModelScope.launch {
      delay(2600) // Allow 3D opening animation to shine
      if (_currentScreen.value == ScreenDestination.SPLASH) {
        if (isLoggedIn.value) {
          _currentScreen.value = ScreenDestination.HOME
        } else {
          _currentScreen.value = ScreenDestination.AUTH
        }
      }
    }
  }

  fun navigateTo(destination: ScreenDestination) {
    if (_currentScreen.value != destination) {
      screenStack.add(_currentScreen.value)
      _currentScreen.value = destination
    }
  }

  fun navigateBack(): Boolean {
    if (screenStack.isNotEmpty()) {
      _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
      return true
    } else if (_currentScreen.value != ScreenDestination.HOME && isLoggedIn.value) {
      _currentScreen.value = ScreenDestination.HOME
      return true
    }
    return false
  }

  fun openChat(conversationId: String) {
    _activeConversationId.value = conversationId
    navigateTo(ScreenDestination.CHAT_DETAIL)
  }

  fun openComments(postId: String) {
    _activeCommentPostId.value = postId
  }

  fun closeComments() {
    _activeCommentPostId.value = null
  }

  fun selectRechargePlan(plan: RechargePlan) {
    _selectedRechargePlan.value = plan
  }

  fun clearSelectedRechargePlan() {
    _selectedRechargePlan.value = null
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun showNotice(msg: String) {
    _uiNotice.value = msg
  }

  fun clearNotice() {
    _uiNotice.value = null
  }

  fun getString(key: String): String {
    return LanguageManager.string(key, currentLanguage.value)
  }

  fun toggleLanguage() {
    repository.toggleLanguage()
  }

  fun setLanguage(language: AppLanguage) {
    repository.setLanguage(language)
  }

  fun login(name: String, phone: String) {
    repository.login(name, phone)
    _currentScreen.value = ScreenDestination.HOME
    screenStack.clear()
    showNotice(getString("login_title"))
  }

  fun logout() {
    repository.logout()
    _currentScreen.value = ScreenDestination.AUTH
    screenStack.clear()
    showNotice(getString("logout"))
  }

  fun createPostWithProgress(content: String, location: String = "Delhi, India") {
    viewModelScope.launch {
      _uploadProgress.value = 0.1f
      delay(300)
      _uploadProgress.value = 0.5f
      delay(400)
      _uploadProgress.value = 0.85f
      delay(300)
      _uploadProgress.value = 1.0f
      delay(200)
      repository.createPost(content, location)
      _uploadProgress.value = null
      navigateBack()
      showNotice(getString("publish_post"))
    }
  }

  fun triggerDataExport() {
    val export = repository.generateUserDataExport()
    _exportedDataText.value = export
  }

  fun closeDataExport() {
    _exportedDataText.value = null
  }
}
