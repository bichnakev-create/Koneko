package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.UserProfile
import com.example.ui.components.KuroExpression
import com.example.ui.components.KuroNekoAvatar
import com.example.ui.components.LiquidTagChip
import com.example.ui.components.liquidGlassBackground
import com.example.ui.theme.AbyssalDark
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LiquidDarkCard
import com.example.ui.theme.LiquidDarkCardElevated
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.LiquidRose
import com.example.ui.theme.NekoPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.StarAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProfileSignInScreen(
    userProfile: UserProfile,
    likedCount: Int,
    totalViews: Int,
    topTags: List<String>,
    onSignIn: (username: String, avatarId: Int) -> Unit,
    onSignOut: () -> Unit,
    onUpdateApiKey: (String) -> Unit,
    onToggleSafeMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSignInDialog by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val affinityTitles = listOf(
        "Level 1: Guarded Neko (>_<)",
        "Level 2: Curious Tsundere",
        "Level 3: Softening Heart (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)",
        "Level 4: Secretly Doting Neko",
        "Level 5: Devoted Waifu Companion nya~"
    )
    val currentAffinityTitle = affinityTitles.getOrElse(userProfile.affinityLevel - 1) { affinityTitles.last() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianVoid)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = NeonViolet,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Account & Aesthetic Profile",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (userProfile.isSignedIn) "Signed in as ${userProfile.username}" else "Browsing as Guest",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // User Identity Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassBackground(
                        backgroundColor = LiquidDarkCardElevated,
                        borderColor = NeonViolet.copy(alpha = 0.5f)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(DeepViolet.copy(alpha = 0.3f))
                                .border(1.5.dp, NeonViolet, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.username.take(1).uppercase(),
                                color = NeonViolet,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = userProfile.username,
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (userProfile.isSignedIn) NeonViolet else TextSecondary)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (userProfile.isSignedIn) "Authenticated User" else "Guest Mode",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    if (userProfile.isSignedIn) {
                        IconButton(
                            onClick = onSignOut,
                            modifier = Modifier.testTag("sign_out_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Sign Out",
                                tint = LiquidRose
                            )
                        }
                    } else {
                        Button(
                            onClick = { showSignInDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("profile_signin_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Login,
                                    contentDescription = null,
                                    tint = ObsidianVoid,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Sign In",
                                    color = ObsidianVoid,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Kuro Affinity Meter
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassBackground(
                        backgroundColor = LiquidDarkCard,
                        borderColor = LiquidGlassBorder
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            KuroNekoAvatar(size = 32.dp, expression = KuroExpression.HAPPY)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Kuro Affinity Meter",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Tier ${userProfile.affinityLevel}/5",
                            color = StarAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentAffinityTitle,
                        color = NeonViolet,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(AbyssalDark)
                    ) {
                        val fraction = (userProfile.affinityLevel.toFloat() / 5f).coerceIn(0.1f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(NeonViolet)
                        )
                    }
                }
            }

            // Activity Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    icon = Icons.Default.Favorite,
                    iconTint = NekoPink,
                    title = "Wallpapers Liked",
                    value = "$likedCount",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Visibility,
                    iconTint = ElectricCyan,
                    title = "Art Explored",
                    value = "$totalViews",
                    modifier = Modifier.weight(1f)
                )
            }

            // Learned Aesthetic Taste Tags
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassBackground(
                        backgroundColor = LiquidDarkCard,
                        borderColor = LiquidGlassBorder
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NeonViolet,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI Learned Taste Tags",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kuro prioritizes these tags when you return to the Showcase feed",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (topTags.isEmpty()) {
                        Text(
                            text = "No interaction history yet. Browse and like art to build your taste profile!",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            topTags.take(4).forEach { tag ->
                                LiquidTagChip(tag = tag, isSelected = true)
                            }
                        }
                    }
                }
            }

            // Settings & BYOK
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassBackground(
                        backgroundColor = LiquidDarkCard,
                        borderColor = LiquidGlassBorder
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Settings & AI Configuration",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // BYOK API Key setting
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showApiKeyDialog = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Gemini API Key (BYOK)",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (userProfile.byokApiKey.isNotBlank()) "Configured (${userProfile.byokApiKey.take(6)}...)" else "Default / Offline Mode",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = NeonViolet,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Safe Mode Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = NeonViolet,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Safe Mode Filter (rating:s)",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Filter booru posts to safe ratings only",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = userProfile.isSafeMode,
                            onCheckedChange = onToggleSafeMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianVoid,
                                checkedTrackColor = NeonViolet,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = AbyssalDark
                            ),
                            modifier = Modifier.testTag("safe_mode_switch")
                        )
                    }
                }
            }
        }

        // Sign In Dialog
        if (showSignInDialog) {
            SignInDialog(
                currentUsername = userProfile.username,
                onDismiss = { showSignInDialog = false },
                onConfirm = { username, avatarId ->
                    onSignIn(username, avatarId)
                    showSignInDialog = false
                }
            )
        }

        // BYOK Edit Dialog
        if (showApiKeyDialog) {
            ApiKeyEditDialog(
                currentKey = userProfile.byokApiKey,
                onDismiss = { showApiKeyDialog = false },
                onSave = { key ->
                    onUpdateApiKey(key)
                    showApiKeyDialog = false
                }
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .liquidGlassBackground(
                backgroundColor = LiquidDarkCard,
                borderColor = LiquidGlassBorder
            )
            .padding(14.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun SignInDialog(
    currentUsername: String,
    onDismiss: () -> Unit,
    onConfirm: (username: String, avatarId: Int) -> Unit
) {
    var username by remember { mutableStateOf(if (currentUsername == "Senpai") "" else currentUsername) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlassBackground(
                    backgroundColor = LiquidDarkCardElevated,
                    borderColor = NeonViolet
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Sign In / Set Profile",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Enter a username to sign your comments and track personal wallpaper stats with Kuro.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("e.g. AnimeOtaku99", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonViolet,
                        unfocusedBorderColor = LiquidGlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signin_username_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = AbyssalDark)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val finalName = username.trim().ifEmpty { "Senpai" }
                            onConfirm(finalName, 1)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                        modifier = Modifier.testTag("signin_confirm_button")
                    ) {
                        Text("Save & Sign In", color = ObsidianVoid, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ApiKeyEditDialog(
    currentKey: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var keyText by remember { mutableStateOf(currentKey) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlassBackground(
                    backgroundColor = LiquidDarkCardElevated,
                    borderColor = ElectricCyan
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Update Gemini API Key",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Provide your personal Google Gemini API key (BYOK). Clear this field to fall back to the built-in system key / offline mode.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = keyText,
                    onValueChange = { keyText = it },
                    placeholder = { Text("AIzaSy...", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = LiquidGlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_api_key_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = AbyssalDark)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(keyText.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        modifier = Modifier.testTag("save_api_key_button")
                    ) {
                        Text("Save Key", color = ObsidianVoid, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
