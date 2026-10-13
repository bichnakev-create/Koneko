package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KuroExpression
import com.example.ui.components.KuroNekoAvatar
import com.example.ui.components.liquidGlassBackground
import com.example.ui.theme.AbyssalDark
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LiquidDarkCard
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.LiquidRose
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    initialApiKey: String,
    onComplete: (apiKey: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var apiKeyText by remember { mutableStateOf(initialApiKey) }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        ObsidianVoid,
                        AbyssalDark,
                        LiquidDarkCard
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Kuro Avatar Hero
            KuroNekoAvatar(
                size = 96.dp,
                expression = KuroExpression.BLUSHING,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "KonaNeko",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Liquid Dark Anime Wallpaper Sanctuary",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = ElectricCyan,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Kuro's Welcome Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassBackground(
                        backgroundColor = LiquidDarkCard.copy(alpha = 0.9f),
                        borderColor = NeonViolet.copy(alpha = 0.5f)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonViolet)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kuro (AI Companion)",
                            color = NeonViolet,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"H-hmph! What are you staring at, baka?! (>_<) I'm Kuro! I track the wallpapers you like and curate fresh showcases whenever you return... Don't make me wait forever, nya!\"",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Feature Highlights
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureRow(
                    icon = Icons.Default.PhotoLibrary,
                    title = "Konachan Booru Gallery",
                    subtitle = "Explore thousands of HD anime wallpapers, tags & comments"
                )
                FeatureRow(
                    icon = Icons.Default.AutoAwesome,
                    title = "Smart AI Taste Tracking",
                    subtitle = "Kuro learns what tags you view & like, then curates custom sets"
                )
                FeatureRow(
                    icon = Icons.Default.ChatBubble,
                    title = "Chat with Soft Tsundere Waifu",
                    subtitle = "Ask Kuro for recommendations, banter & critique your taste"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BYOK Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassBackground(
                        backgroundColor = AbyssalDark.copy(alpha = 0.95f),
                        borderColor = LiquidGlassBorder
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "BYOK",
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bring Your Own Key (BYOK)",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enter your Google Gemini API key to power Kuro's live conversational mind. You can also skip or leave blank to use the built-in system key / offline mode.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = apiKeyText,
                        onValueChange = { apiKeyText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("byok_key_input"),
                        placeholder = {
                            Text("AIzaSy...", color = TextSecondary.copy(alpha = 0.5f), fontSize = 13.sp)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonViolet,
                            unfocusedBorderColor = LiquidGlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = LiquidDarkCard,
                            unfocusedContainerColor = LiquidDarkCard
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enter Button
            Button(
                onClick = { onComplete(apiKeyText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("onboarding_enter_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonViolet
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ObsidianVoid,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enter Gallery & Meet Kuro",
                        color = ObsidianVoid,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlassBackground(
                backgroundColor = LiquidDarkCard.copy(alpha = 0.7f),
                borderColor = LiquidGlassBorder.copy(alpha = 0.6f)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(DeepViolet.copy(alpha = 0.25f))
                .border(1.dp, NeonViolet.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonViolet,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
