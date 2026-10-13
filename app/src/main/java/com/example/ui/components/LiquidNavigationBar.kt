package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LiquidDarkCardElevated
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.NekoPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LiquidNavigationBar(
    currentScreen: AppScreen,
    likedCount: Int,
    onSelectScreen: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlassBackground(
                    shape = RoundedCornerShape(26.dp),
                    backgroundColor = LiquidDarkCardElevated.copy(alpha = 0.95f),
                    borderColor = LiquidGlassBorder
                )
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.PhotoLibrary,
                label = "Gallery",
                isSelected = currentScreen == AppScreen.GALLERY,
                onClick = { onSelectScreen(AppScreen.GALLERY) },
                testTag = "nav_gallery"
            )
            NavItem(
                icon = Icons.Default.AutoAwesome,
                label = "Showcase",
                isSelected = currentScreen == AppScreen.CURATED_SHOWCASE,
                onClick = { onSelectScreen(AppScreen.CURATED_SHOWCASE) },
                testTag = "nav_showcase"
            )
            NavItem(
                icon = Icons.Default.ChatBubble,
                label = "Kuro AI",
                isSelected = currentScreen == AppScreen.CHAT,
                onClick = { onSelectScreen(AppScreen.CHAT) },
                testTag = "nav_chat"
            )
            NavItem(
                icon = Icons.Default.Favorite,
                label = "Likes",
                isSelected = currentScreen == AppScreen.LIKES,
                badgeCount = if (likedCount > 0) likedCount else null,
                onClick = { onSelectScreen(AppScreen.LIKES) },
                testTag = "nav_likes"
            )
            NavItem(
                icon = Icons.Default.Person,
                label = "Profile",
                isSelected = currentScreen == AppScreen.PROFILE,
                onClick = { onSelectScreen(AppScreen.PROFILE) },
                testTag = "nav_profile"
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    badgeCount: Int? = null,
    onClick: () -> Unit,
    testTag: String
) {
    val activeColor by animateColorAsState(
        targetValue = if (isSelected) NeonViolet else TextSecondary,
        label = "nav_icon_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = NeonViolet),
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        BadgedBox(
            badge = {
                if (badgeCount != null) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(NekoPink)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (badgeCount > 99) "99+" else "$badgeCount",
                            color = ObsidianVoid,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .then(
                        if (isSelected) {
                            Modifier
                                .clip(CircleShape)
                                .background(NeonViolet.copy(alpha = 0.2f))
                                .border(1.dp, NeonViolet.copy(alpha = 0.6f), CircleShape)
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = activeColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = if (isSelected) TextPrimary else TextSecondary,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
