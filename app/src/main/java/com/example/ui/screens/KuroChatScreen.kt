package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity
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
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private val QUICK_PROMPTS = listOf(
    "Recommend a wallpaper for me!",
    "What do you think of my taste?",
    "You're really cute, Kuro!",
    "Call me baka, nya~",
    "Find me scenic starry skies!"
)

@Composable
fun KuroChatScreen(
    messages: List<ChatMessageEntity>,
    isThinking: Boolean,
    expression: KuroExpression,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scrollStateQuick = rememberScrollState()

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianVoid)
            .statusBarsPadding()
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                KuroNekoAvatar(
                    size = 44.dp,
                    expression = expression
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Kuro-chan",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonViolet.copy(alpha = 0.25f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "Soft Tsundere",
                                color = NeonViolet,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = if (isThinking) "Typing a flustered reply... (>_<)" else "Online • Watching your booru taste",
                        color = ElectricCyan,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onClearChat,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(LiquidDarkCard)
                    .border(1.dp, LiquidGlassBorder, CircleShape)
                    .testTag("clear_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear Chat",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(message = msg)
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .liquidGlassBackground(
                                    shape = RoundedCornerShape(18.dp),
                                    backgroundColor = LiquidDarkCard,
                                    borderColor = LiquidGlassBorder
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    color = NeonViolet,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Kuro is thinking...",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Suggestion Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollStateQuick)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            QUICK_PROMPTS.forEach { prompt ->
                LiquidTagChip(
                    tag = prompt,
                    isSelected = false,
                    onClick = { onSendMessage(prompt) },
                    modifier = Modifier.testTag("prompt_chip_${prompt.take(6)}")
                )
            }
        }

        // Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 96.dp, top = 4.dp)
                .liquidGlassBackground(
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = LiquidDarkCardElevated,
                    borderColor = LiquidGlassBorder
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input"),
                placeholder = {
                    Text("Chat with Kuro nya...", color = TextSecondary, fontSize = 13.sp)
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
                    if (textInput.isNotBlank()) {
                        onSendMessage(textInput.trim())
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(NeonViolet)
                    .testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = ObsidianVoid,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessageEntity) {
    val isUser = message.isFromUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            KuroNekoAvatar(size = 32.dp, expression = KuroExpression.HAPPY)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .then(
                    if (isUser) {
                        Modifier
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(DeepViolet, NeonViolet)
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    } else {
                        Modifier
                            .liquidGlassBackground(
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
                                backgroundColor = LiquidDarkCard,
                                borderColor = ElectricCyan.copy(alpha = 0.4f)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    }
                )
        ) {
            Text(
                text = message.text,
                color = if (isUser) ObsidianVoid else TextPrimary,
                fontSize = 13.5.sp,
                lineHeight = 19.sp,
                fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}
