package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.LiquidNavigationBar
import com.example.ui.screens.CuratedShowcaseScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.KuroChatScreen
import com.example.ui.screens.LikesScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PostDetailScreen
import com.example.ui.screens.ProfileSignInScreen
import com.example.ui.theme.KonaNekoTheme
import com.example.ui.theme.ObsidianVoid

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KonaNekoTheme {
                KonaNekoApp()
            }
        }
    }
}

@Composable
fun KonaNekoApp(
    viewModel: MainViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val selectedPost by viewModel.selectedPost.collectAsState()

    val galleryPosts by viewModel.galleryPosts.collectAsState()
    val isLoadingGallery by viewModel.isLoadingGallery.collectAsState()
    val galleryError by viewModel.galleryError.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeTag by viewModel.activeTagFilter.collectAsState()

    val curatedPosts by viewModel.curatedPosts.collectAsState()
    val isCurating by viewModel.isCurating.collectAsState()
    val kuroRecommendation by viewModel.kuroRecommendation.collectAsState()
    val topUserTags by viewModel.topUserTags.collectAsState()

    val likedPosts by viewModel.likedPosts.collectAsState()
    val likedCount by viewModel.likedCount.collectAsState()

    val chatMessages by viewModel.chatMessages.collectAsState()
    val isKuroThinking by viewModel.isKuroThinking.collectAsState()
    val kuroExpression by viewModel.kuroExpression.collectAsState()

    val isDetailLiked by viewModel.isPostLiked.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val isLoadingComments by viewModel.isLoadingComments.collectAsState()
    val totalViews by viewModel.totalViews.collectAsState()

    // Handle back button
    BackHandler(enabled = selectedPost != null || (currentScreen != AppScreen.GALLERY && currentScreen != AppScreen.ONBOARDING)) {
        if (selectedPost != null) {
            viewModel.closePostDetail()
        } else if (currentScreen != AppScreen.GALLERY) {
            viewModel.navigateTo(AppScreen.GALLERY)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianVoid)
    ) {
        if (selectedPost != null) {
            PostDetailScreen(
                post = selectedPost!!,
                isLiked = isDetailLiked,
                comments = comments,
                isLoadingComments = isLoadingComments,
                currentUser = userProfile.username,
                onBack = { viewModel.closePostDetail() },
                onToggleLike = { viewModel.toggleLikeSelectedPost() },
                onAddComment = { viewModel.submitComment(it) },
                onTagClick = { tag ->
                    viewModel.closePostDetail()
                    viewModel.applyTagFilter(tag)
                    viewModel.navigateTo(AppScreen.GALLERY)
                }
            )
        } else if (currentScreen == AppScreen.ONBOARDING) {
            OnboardingScreen(
                initialApiKey = userProfile.byokApiKey,
                onComplete = { apiKey ->
                    viewModel.completeOnboarding(apiKey)
                }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = ObsidianVoid,
                bottomBar = {
                    LiquidNavigationBar(
                        currentScreen = currentScreen,
                        likedCount = likedCount,
                        onSelectScreen = { viewModel.navigateTo(it) }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    when (currentScreen) {
                        AppScreen.GALLERY -> {
                            GalleryScreen(
                                posts = galleryPosts,
                                isLoading = isLoadingGallery,
                                errorMessage = galleryError,
                                searchQuery = searchQuery,
                                activeTag = activeTag,
                                onSearchChange = { viewModel.updateSearchQuery(it) },
                                onSearchSubmit = { viewModel.loadGallery(reset = true) },
                                onTagSelect = { viewModel.applyTagFilter(it) },
                                onPostClick = { viewModel.selectPost(it) },
                                onToggleLike = { viewModel.toggleLikePost(it) },
                                onLoadMore = { viewModel.loadGallery(reset = false) },
                                isPostLiked = { postId ->
                                    likedPosts.any { it.id == postId }
                                }
                            )
                        }

                        AppScreen.CURATED_SHOWCASE -> {
                            CuratedShowcaseScreen(
                                recommendation = kuroRecommendation,
                                posts = curatedPosts,
                                isCurating = isCurating,
                                topTags = topUserTags,
                                onRefresh = { viewModel.refreshCuratedShowcase() },
                                onTagSelect = { tag ->
                                    viewModel.applyTagFilter(tag)
                                    viewModel.navigateTo(AppScreen.GALLERY)
                                },
                                onPostClick = { viewModel.selectPost(it) },
                                onToggleLike = { viewModel.toggleLikePost(it) },
                                isPostLiked = { postId ->
                                    likedPosts.any { it.id == postId }
                                }
                            )
                        }

                        AppScreen.CHAT -> {
                            KuroChatScreen(
                                messages = chatMessages,
                                isThinking = isKuroThinking,
                                expression = kuroExpression,
                                onSendMessage = { viewModel.sendChatMessage(it) },
                                onClearChat = { viewModel.clearChat() }
                            )
                        }

                        AppScreen.LIKES -> {
                            LikesScreen(
                                likedPosts = likedPosts,
                                onPostClick = { viewModel.selectPost(it) },
                                onUnlike = { viewModel.toggleLikePost(it) }
                            )
                        }

                        AppScreen.PROFILE -> {
                            ProfileSignInScreen(
                                userProfile = userProfile,
                                likedCount = likedCount,
                                totalViews = totalViews,
                                topTags = topUserTags,
                                onSignIn = { username, avatarId ->
                                    viewModel.signIn(username, avatarId)
                                },
                                onSignOut = { viewModel.signOut() },
                                onUpdateApiKey = { viewModel.updateApiKey(it) },
                                onToggleSafeMode = { viewModel.toggleSafeMode(it) }
                            )
                        }

                        AppScreen.ONBOARDING -> {
                            // Handled at root level
                        }
                    }
                }
            }
        }
    }
}
