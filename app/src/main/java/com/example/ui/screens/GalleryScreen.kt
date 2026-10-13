package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.KonachanPost
import com.example.ui.components.KuroExpression
import com.example.ui.components.KuroNekoAvatar
import com.example.ui.components.LiquidLikeButton
import com.example.ui.components.LiquidTagChip
import com.example.ui.components.liquidGlassBackground
import com.example.ui.components.rememberShimmerBrush
import com.example.ui.theme.AbyssalDark
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LiquidDarkCard
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private val POPULAR_TAGS = listOf(
    "scenic",
    "catgirl",
    "clouds",
    "sunset",
    "cyberpunk",
    "original",
    "night_sky",
    "animal_ears",
    "landscape",
    "solo"
)

@Composable
fun GalleryScreen(
    posts: List<KonachanPost>,
    isLoading: Boolean,
    errorMessage: String?,
    searchQuery: String,
    activeTag: String?,
    onSearchChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onTagSelect: (String?) -> Unit,
    onPostClick: (KonachanPost) -> Unit,
    onToggleLike: (KonachanPost) -> Unit,
    onLoadMore: () -> Unit,
    isPostLiked: (Long) -> Boolean,
    modifier: Modifier = Modifier
) {
    val scrollStateTags = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianVoid)
            .statusBarsPadding()
    ) {
        // Liquid Header & Search
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KuroNekoAvatar(size = 36.dp, expression = KuroExpression.CALM)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "KonaNeko Gallery",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Konachan Booru Feed",
                            color = ElectricCyan,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = onSearchSubmit,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(LiquidDarkCard)
                        .border(1.dp, LiquidGlassBorder, CircleShape)
                        .testTag("refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = NeonViolet,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gallery_search_input"),
                placeholder = {
                    Text("Search tags (e.g. scenic, catgirl)...", color = TextSecondary, fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = NeonViolet,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            onSearchChange("")
                            onSearchSubmit()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonViolet,
                    unfocusedBorderColor = LiquidGlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = LiquidDarkCard,
                    unfocusedContainerColor = LiquidDarkCard
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Horizontal Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollStateTags),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LiquidTagChip(
                    tag = "all",
                    isSelected = activeTag == null,
                    onClick = { onTagSelect(null) },
                    modifier = Modifier.testTag("tag_all")
                )
                for (tag in POPULAR_TAGS) {
                    LiquidTagChip(
                        tag = tag,
                        isSelected = activeTag == tag,
                        onClick = { onTagSelect(tag) },
                        modifier = Modifier.testTag("tag_$tag")
                    )
                }
            }
        }

        // Gallery Grid
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (posts.isEmpty() && isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonViolet)
                }
            } else if (posts.isEmpty() && errorMessage != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    KuroNekoAvatar(size = 72.dp, expression = KuroExpression.POUTING)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Hmph! Failed to fetch wallpapers, baka!",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onSearchSubmit,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Retry", color = ObsidianVoid, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 96.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(posts, key = { it.id }) { post ->
                        WallpaperCard(
                            post = post,
                            isLiked = isPostLiked(post.id),
                            onClick = { onPostClick(post) },
                            onToggleLike = { onToggleLike(post) },
                            modifier = Modifier.testTag("post_card_${post.id}")
                        )
                    }

                    // Load More footer item
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = NeonViolet, modifier = Modifier.size(28.dp))
                            } else {
                                Button(
                                    onClick = onLoadMore,
                                    colors = ButtonDefaults.buttonColors(containerColor = LiquidDarkCard),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .border(1.dp, LiquidGlassBorder, RoundedCornerShape(14.dp))
                                        .testTag("load_more_button")
                                ) {
                                    Text(
                                        text = "Load More Wallpapers",
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
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
fun WallpaperCard(
    post: KonachanPost,
    isLiked: Boolean,
    onClick: () -> Unit,
    onToggleLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shimmer = rememberShimmerBrush()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .liquidGlassBackground(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = LiquidDarkCard,
                borderColor = LiquidGlassBorder
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = NeonViolet),
                onClick = onClick
            )
    ) {
        // Image
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(post.displayPreviewUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Wallpaper by ${post.author ?: "Artist"}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(shimmer)
        )

        // Dark gradient scrim at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, AbyssalDark.copy(alpha = 0.95f))
                    )
                )
        )

        // Top Badges: Resolution
        Box(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianVoid.copy(alpha = 0.75f))
                .border(0.8.dp, LiquidGlassBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Text(
                text = post.resolutionLabel,
                color = ElectricCyan,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Bottom Info: Author & Like button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = post.author ?: "Unknown",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            )

            LiquidLikeButton(
                isLiked = isLiked,
                onToggle = onToggleLike,
                score = if (post.score > 0) post.score else null,
                modifier = Modifier.testTag("like_button_${post.id}")
            )
        }
    }
}
