package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.KonachanPost
import com.example.data.repository.DisplayComment
import com.example.ui.components.LiquidTagChip
import com.example.ui.components.liquidGlassBackground
import com.example.ui.components.rememberShimmerBrush
import com.example.ui.theme.AbyssalDark
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LiquidDarkCard
import com.example.ui.theme.LiquidDarkCardElevated
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.LiquidRose
import com.example.ui.theme.NekoPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PostDetailScreen(
    post: KonachanPost,
    isLiked: Boolean,
    comments: List<DisplayComment>,
    isLoadingComments: Boolean,
    currentUser: String,
    onBack: () -> Unit,
    onToggleLike: () -> Unit,
    onAddComment: (String) -> Unit,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var commentInput by remember { mutableStateOf("") }
    var isFullscreenOpen by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val tagsScrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianVoid)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Wallpaper Header Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(post.displaySampleUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Wallpaper by ${post.author ?: "Artist"}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(rememberShimmerBrush())
                )

                // Top navigation overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ObsidianVoid.copy(alpha = 0.7f))
                            .border(1.dp, LiquidGlassBorder, CircleShape)
                            .testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }

                    IconButton(
                        onClick = { isFullscreenOpen = true },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ObsidianVoid.copy(alpha = 0.7f))
                            .border(1.dp, LiquidGlassBorder, CircleShape)
                            .testTag("detail_fullscreen_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = ElectricCyan
                        )
                    }
                }

                // Gradient blend
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, ObsidianVoid)
                            )
                        )
                )
            }

            // Post Details & Meta
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = post.author ?: "Unknown Artist",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Konachan Post #${post.id} • ${post.resolutionLabel}",
                            fontSize = 12.sp,
                            color = ElectricCyan,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Like Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isLiked) NekoPink.copy(alpha = 0.2f) else LiquidDarkCard)
                            .border(1.dp, if (isLiked) NekoPink else LiquidGlassBorder, RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true, color = NekoPink),
                                onClick = onToggleLike
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("detail_like_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isLiked) "Unlike" else "Like",
                            tint = if (isLiked) NekoPink else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isLiked) "Liked" else "Like",
                            color = if (isLiked) NekoPink else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tags Horizontal List
                Text(
                    text = "Tags (${post.tagList.size})",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(tagsScrollState),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    post.tagList.forEach { tag ->
                        LiquidTagChip(
                            tag = tag,
                            isSelected = false,
                            onClick = { onTagClick(tag) },
                            modifier = Modifier.testTag("detail_tag_$tag")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Comments Section Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = null,
                        tint = NeonViolet,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Comments (${comments.size})",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Add Comment Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassBackground(
                            backgroundColor = LiquidDarkCard,
                            borderColor = LiquidGlassBorder
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentInput,
                        onValueChange = { commentInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("comment_input"),
                        placeholder = {
                            Text("Write a comment as $currentUser...", color = TextSecondary, fontSize = 13.sp)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    IconButton(
                        onClick = {
                            if (commentInput.isNotBlank()) {
                                onAddComment(commentInput.trim())
                                commentInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NeonViolet)
                            .testTag("submit_comment_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Comment",
                            tint = ObsidianVoid,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Comments List
                if (isLoadingComments && comments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NeonViolet, modifier = Modifier.size(24.dp))
                    }
                } else if (comments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlassBackground(
                                backgroundColor = LiquidDarkCard.copy(alpha = 0.5f),
                                borderColor = LiquidGlassBorder.copy(alpha = 0.5f)
                            )
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No comments yet. Be the first to leave your thoughts!",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        comments.forEach { comment ->
                            CommentItem(comment = comment)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Fullscreen Wallpaper Viewer Dialog
        if (isFullscreenOpen) {
            Dialog(
                onDismissRequest = { isFullscreenOpen = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(post.displayFullUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Fullscreen view",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                    IconButton(
                        onClick = { isFullscreenOpen = false },
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(16.dp)
                            .align(Alignment.TopEnd)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ObsidianVoid.copy(alpha = 0.8f))
                            .border(1.dp, LiquidGlassBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentItem(comment: DisplayComment) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlassBackground(
                shape = RoundedCornerShape(14.dp),
                backgroundColor = if (comment.isLocal) LiquidDarkCardElevated else LiquidDarkCard,
                borderColor = if (comment.isLocal) NeonViolet.copy(alpha = 0.4f) else LiquidGlassBorder
            )
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.author,
                        color = if (comment.isLocal) NeonViolet else ElectricCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (comment.isLocal) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonViolet.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("You", color = NeonViolet, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = comment.timestampFormatted,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.text,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}
