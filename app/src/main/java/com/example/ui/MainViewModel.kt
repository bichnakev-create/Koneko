package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.KuroAiService
import com.example.data.ai.KuroShowcaseRecommendation
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.LikedPostEntity
import com.example.data.local.UserPreferencesManager
import com.example.data.local.UserProfile
import com.example.data.model.KonachanPost
import com.example.data.repository.DisplayComment
import com.example.data.repository.WallpaperRepository
import com.example.ui.components.KuroExpression
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    ONBOARDING,
    GALLERY,
    CURATED_SHOWCASE,
    LIKES,
    CHAT,
    PROFILE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val prefsManager = UserPreferencesManager(application)
    val wallpaperRepository = WallpaperRepository(database)

    val userProfile: StateFlow<UserProfile> = prefsManager.userProfile

    private val kuroAi = KuroAiService(
        getApiKey = { userProfile.value.byokApiKey }
    )

    // Navigation
    private val _currentScreen = MutableStateFlow(
        if (prefsManager.userProfile.value.isOnboardingComplete) AppScreen.GALLERY else AppScreen.ONBOARDING
    )
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Gallery State
    private val _galleryPosts = MutableStateFlow<List<KonachanPost>>(emptyList())
    val galleryPosts: StateFlow<List<KonachanPost>> = _galleryPosts.asStateFlow()

    private val _isLoadingGallery = MutableStateFlow(false)
    val isLoadingGallery: StateFlow<Boolean> = _isLoadingGallery.asStateFlow()

    private val _galleryError = MutableStateFlow<String?>(null)
    val galleryError: StateFlow<String?> = _galleryError.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeTagFilter = MutableStateFlow<String?>(null)
    val activeTagFilter: StateFlow<String?> = _activeTagFilter.asStateFlow()

    private var currentPage = 1

    // Detail Screen
    private val _selectedPost = MutableStateFlow<KonachanPost?>(null)
    val selectedPost: StateFlow<KonachanPost?> = _selectedPost.asStateFlow()

    private val _isPostLiked = MutableStateFlow(false)
    val isPostLiked: StateFlow<Boolean> = _isPostLiked.asStateFlow()

    private val _comments = MutableStateFlow<List<DisplayComment>>(emptyList())
    val comments: StateFlow<List<DisplayComment>> = _comments.asStateFlow()

    private val _isLoadingComments = MutableStateFlow(false)
    val isLoadingComments: StateFlow<Boolean> = _isLoadingComments.asStateFlow()

    // Curated Showcase
    private val _curatedPosts = MutableStateFlow<List<KonachanPost>>(emptyList())
    val curatedPosts: StateFlow<List<KonachanPost>> = _curatedPosts.asStateFlow()

    private val _isCurating = MutableStateFlow(false)
    val isCurating: StateFlow<Boolean> = _isCurating.asStateFlow()

    private val _kuroRecommendation = MutableStateFlow<KuroShowcaseRecommendation?>(null)
    val kuroRecommendation: StateFlow<KuroShowcaseRecommendation?> = _kuroRecommendation.asStateFlow()

    private val _topUserTags = MutableStateFlow<List<String>>(emptyList())
    val topUserTags: StateFlow<List<String>> = _topUserTags.asStateFlow()

    // Likes List
    val likedPosts: StateFlow<List<LikedPostEntity>> = wallpaperRepository.getAllLikedPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedCount: StateFlow<Int> = wallpaperRepository.getLikedCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Chat with Kuro
    val chatMessages: StateFlow<List<ChatMessageEntity>> = database.chatMessageDao().getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isKuroThinking = MutableStateFlow(false)
    val isKuroThinking: StateFlow<Boolean> = _isKuroThinking.asStateFlow()

    private val _kuroExpression = MutableStateFlow(KuroExpression.CALM)
    val kuroExpression: StateFlow<KuroExpression> = _kuroExpression.asStateFlow()

    // Onboarding & BYOK
    private val _byokInput = MutableStateFlow(userProfile.value.byokApiKey)
    val byokInput: StateFlow<String> = _byokInput.asStateFlow()

    private val _totalViews = MutableStateFlow(0)
    val totalViews: StateFlow<Int> = _totalViews.asStateFlow()

    init {
        loadGallery(reset = true)
        refreshCuratedShowcase()
        seedWelcomeChatIfNeeded()
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
        if (screen == AppScreen.CURATED_SHOWCASE) {
            refreshCuratedShowcase()
        } else if (screen == AppScreen.PROFILE) {
            refreshStats()
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun applyTagFilter(tag: String?) {
        _activeTagFilter.value = if (_activeTagFilter.value == tag) null else tag
        loadGallery(reset = true)
    }

    fun loadGallery(reset: Boolean = false) {
        if (reset) {
            currentPage = 1
        }
        viewModelScope.launch {
            _isLoadingGallery.value = true
            _galleryError.value = null

            val tagQuery = buildString {
                _activeTagFilter.value?.let { append("$it ") }
                if (_searchQuery.value.isNotBlank()) append(_searchQuery.value.trim())
            }.trim().ifEmpty { null }

            val result = wallpaperRepository.getWallpapers(
                page = currentPage,
                limit = 24,
                tags = tagQuery,
                safeOnly = userProfile.value.isSafeMode
            )

            result.onSuccess { posts ->
                if (reset) {
                    _galleryPosts.value = posts
                } else {
                    _galleryPosts.value = _galleryPosts.value + posts
                }
                currentPage++
            }.onFailure { err ->
                _galleryError.value = err.message ?: "Failed to load wallpapers"
            }
            _isLoadingGallery.value = false
        }
    }

    fun selectPost(post: KonachanPost) {
        _selectedPost.value = post
        viewModelScope.launch {
            _isPostLiked.value = wallpaperRepository.isPostLiked(post.id)
            wallpaperRepository.recordPostView(post)
            loadComments(post.id)
            refreshStats()
        }
    }

    fun closePostDetail() {
        _selectedPost.value = null
    }

    fun toggleLikeSelectedPost() {
        val post = _selectedPost.value ?: return
        viewModelScope.launch {
            val nowLiked = wallpaperRepository.toggleLike(post)
            _isPostLiked.value = nowLiked
            if (nowLiked) {
                prefsManager.incrementAffinity()
                _kuroExpression.value = KuroExpression.HAPPY
            }
            refreshStats()
        }
    }

    fun toggleLikePost(post: KonachanPost) {
        viewModelScope.launch {
            val nowLiked = wallpaperRepository.toggleLike(post)
            if (_selectedPost.value?.id == post.id) {
                _isPostLiked.value = nowLiked
            }
            if (nowLiked) {
                prefsManager.incrementAffinity()
            }
            refreshStats()
        }
    }

    private fun loadComments(postId: Long) {
        viewModelScope.launch {
            _isLoadingComments.value = true
            _comments.value = wallpaperRepository.getCommentsForPost(postId)
            _isLoadingComments.value = false
        }
    }

    fun submitComment(text: String) {
        val post = _selectedPost.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            val author = userProfile.value.username
            val newComment = wallpaperRepository.addComment(post.id, author, text)
            _comments.value = listOf(newComment) + _comments.value
        }
    }

    // Curated Showcase Agent
    fun refreshCuratedShowcase() {
        viewModelScope.launch {
            _isCurating.value = true
            val topTags = wallpaperRepository.getTopUserTags(limit = 6)
            _topUserTags.value = topTags
            val likes = wallpaperRepository.getLikedCountFlow()
            val views = wallpaperRepository.getTotalViews()

            // AI Agent curates recommendation message and tags
            val recommendation = kuroAi.generateCuratedShowcase(
                topTags = topTags,
                likedCount = likedCount.value,
                viewCount = views
            )
            _kuroRecommendation.value = recommendation

            // Fetch wallpapers using curated tags
            val searchTag = recommendation.suggestedTags.firstOrNull() ?: "scenic"
            val postsResult = wallpaperRepository.getWallpapers(
                page = 1,
                limit = 24,
                tags = searchTag,
                safeOnly = userProfile.value.isSafeMode
            )
            postsResult.onSuccess {
                _curatedPosts.value = it
            }
            _isCurating.value = false
        }
    }

    // Kuro Chat
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val userMsg = ChatMessageEntity(text = text, isFromUser = true)
            database.chatMessageDao().insertMessage(userMsg)

            _isKuroThinking.value = true
            _kuroExpression.value = when {
                text.contains("cute", ignoreCase = true) || text.contains("love", ignoreCase = true) -> KuroExpression.BLUSHING
                text.contains("baka", ignoreCase = true) -> KuroExpression.POUTING
                else -> KuroExpression.HAPPY
            }

            val history = database.chatMessageDao().getAllMessagesList()
                .map { it.text to it.isFromUser }

            val reply = kuroAi.chatWithKuro(
                history = history,
                userMessage = text,
                topLikedTags = _topUserTags.value
            )

            val kuroMsg = ChatMessageEntity(text = reply, isFromUser = false)
            database.chatMessageDao().insertMessage(kuroMsg)
            _isKuroThinking.value = false
            prefsManager.incrementAffinity()
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            database.chatMessageDao().clearAll()
            seedWelcomeChatIfNeeded(force = true)
        }
    }

    private fun seedWelcomeChatIfNeeded(force: Boolean = false) {
        viewModelScope.launch {
            val msgs = database.chatMessageDao().getAllMessagesList()
            if (msgs.isEmpty() || force) {
                val greeting = ChatMessageEntity(
                    text = "H-hmph! What are you doing here, baka?! (>_<) I'm Kuro! I'm just here to make sure you don't pick ugly wallpapers... Ask me for recommendations or chat if you want, nya~ (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)",
                    isFromUser = false
                )
                database.chatMessageDao().insertMessage(greeting)
            }
        }
    }

    // Onboarding & BYOK
    fun updateByokInput(key: String) {
        _byokInput.value = key
    }

    fun completeOnboarding(key: String) {
        prefsManager.setByokApiKey(key.trim())
        prefsManager.completeOnboarding()
        _currentScreen.value = AppScreen.GALLERY
    }

    // User Profile
    fun signIn(username: String, avatarId: Int) {
        prefsManager.signIn(username, avatarId)
    }

    fun signOut() {
        prefsManager.signOut()
    }

    fun updateApiKey(key: String) {
        prefsManager.setByokApiKey(key.trim())
    }

    fun toggleSafeMode(enabled: Boolean) {
        prefsManager.updateSafeMode(enabled)
        loadGallery(reset = true)
        refreshCuratedShowcase()
    }

    private fun refreshStats() {
        viewModelScope.launch {
            _topUserTags.value = wallpaperRepository.getTopUserTags(6)
            _totalViews.value = wallpaperRepository.getTotalViews()
        }
    }
}
