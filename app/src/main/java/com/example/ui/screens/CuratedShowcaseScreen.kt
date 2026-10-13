package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.KuroShowcaseRecommendation
import com.example.data.model.KonachanPost
import com.example.ui.components.KuroExpression
import com.example.ui.components.KuroNekoAvatar
import com.example.ui.components.LiquidTagChip
import com.example.ui.components.liquidGlassBackground
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LiquidDarkCard
import com.example.ui.theme.LiquidDarkCardElevated
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CuratedShowcaseScreen(
    recommendation: KuroShowcaseRecommendation?,
    posts: List<KonachanPost>,
    isCurating: Boolean,
    topTags: List<String>,
    onRefresh: () -> Unit,
    onTagSelect: (String) -> Unit,
    onPostClick: (KonachanPost) -> Unit,
    onToggleLike: (KonachanPost) -> Unit,
    isPostLiked: (Long) -> Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianVoid)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = NeonViolet,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Kuro's Curated Showcase",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Tailored to your aesthetic taste",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(LiquidDarkCard)
                    .border(1.dp, LiquidGlassBorder, CircleShape)
                    .testTag("curated_refresh_button")
            ) {
                if (isCurating) {
                    CircularProgressIndicator(
                        color = NeonViolet,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Re-curate",
                        tint = NeonViolet,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Lazy Grid containing the Hero Card + curated posts
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Hero Curation Banner
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassBackground(
                            shape = RoundedCornerShape(20.dp),
                            backgroundColor = LiquidDarkCardElevated,
                            borderColor = NeonViolet.copy(alpha = 0.5f)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            KuroNekoAvatar(
                                size = 48.dp,
                                expression = KuroExpression.BLUSHING
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Kuro (Curator Mode)",
                                    color = NeonViolet,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (topTags.isNotEmpty()) "Observed tastes: ${topTags.take(3).joinToString(", ")}" else "Analyzing your favorite tags...",
                                    color = ElectricCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val greeting = recommendation?.greeting
                            ?: "H-hmph! It's not like I searched the booru specially for you or anything, baka! (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄) ...Here are the prettiest wallpapers I picked out!"

                        Text(
                            text = "\"$greeting\"",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.Normal
                        )

                        if (!recommendation?.suggestedTags.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Kuro's Recommended Tags to explore:",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                recommendation?.suggestedTags?.take(3)?.forEach { tag ->
                                    LiquidTagChip(
                                        tag = tag,
                                        isSelected = true,
                                        onClick = { onTagSelect(tag) },
                                        modifier = Modifier.testTag("curated_tag_$tag")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Grid items
            if (isCurating && posts.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NeonViolet)
                    }
                }
            } else {
                items(posts, key = { it.id }) { post ->
                    WallpaperCard(
                        post = post,
                        isLiked = isPostLiked(post.id),
                        onClick = { onPostClick(post) },
                        onToggleLike = { onToggleLike(post) },
                        modifier = Modifier.testTag("curated_post_${post.id}")
                    )
                }
            }
        }
    }
}
