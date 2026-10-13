package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AbyssalDark
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LiquidDarkCard
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.LiquidRose
import com.example.ui.theme.NekoBlush
import com.example.ui.theme.NekoPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Modifier helper for translucent liquid glass look with glossy subtle border
 */
fun Modifier.liquidGlassBackground(
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = LiquidDarkCard.copy(alpha = 0.85f),
    borderColor: Color = LiquidGlassBorder
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .border(1.dp, borderColor, shape)

/**
 * Shimmer effect brush for image loading placeholders
 */
@Composable
fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )

    return Brush.linearGradient(
        colors = listOf(
            LiquidDarkCard.copy(alpha = 0.6f),
            NeonViolet.copy(alpha = 0.25f),
            LiquidDarkCard.copy(alpha = 0.6f)
        ),
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )
}

/**
 * Liquid Tag Chip
 */
@Composable
fun LiquidTagChip(
    tag: String,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) {
        Brush.horizontalGradient(listOf(DeepViolet, NeonViolet))
    } else {
        Brush.horizontalGradient(listOf(LiquidDarkCard, LiquidDarkCard))
    }
    val borderCol = if (isSelected) ElectricCyan else LiquidGlassBorder
    val textCol = if (isSelected) ObsidianVoid else TextSecondary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(12.dp))
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = NeonViolet),
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "#$tag",
            color = textCol,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

/**
 * Interactive animated Like Button
 */
@Composable
fun LiquidLikeButton(
    isLiked: Boolean,
    onToggle: () -> Unit,
    score: Int? = null,
    modifier: Modifier = Modifier
) {
    val heartColor = if (isLiked) NekoPink else TextSecondary
    val bgColor = if (isLiked) NekoPink.copy(alpha = 0.2f) else AbyssalDark.copy(alpha = 0.75f)

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, if (isLiked) NekoPink.copy(alpha = 0.6f) else LiquidGlassBorder, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = NekoPink),
                onClick = onToggle
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isLiked) "Unlike" else "Like",
            tint = heartColor,
            modifier = Modifier.size(18.dp)
        )
        if (score != null) {
            Text(
                text = " $score",
                color = if (isLiked) NekoPink else TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}

/**
 * Kuro Neko Avatar vector graphic drawn dynamically with expressions
 */
@Composable
fun KuroNekoAvatar(
    size: Dp = 48.dp,
    expression: KuroExpression = KuroExpression.HAPPY,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        DeepViolet.copy(alpha = 0.8f),
                        ObsidianVoid
                    )
                )
            )
            .border(1.5.dp, NeonViolet, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.78f)) {
            val w = this.size.width
            val h = this.size.height

            // Ears
            val leftEar = Path().apply {
                moveTo(w * 0.20f, h * 0.35f)
                lineTo(w * 0.12f, h * 0.08f)
                lineTo(w * 0.42f, h * 0.22f)
                close()
            }
            drawPath(leftEar, color = NeonViolet)

            val leftInner = Path().apply {
                moveTo(w * 0.22f, h * 0.30f)
                lineTo(w * 0.18f, h * 0.14f)
                lineTo(w * 0.36f, h * 0.23f)
                close()
            }
            drawPath(leftInner, color = NekoBlush)

            val rightEar = Path().apply {
                moveTo(w * 0.80f, h * 0.35f)
                lineTo(w * 0.88f, h * 0.08f)
                lineTo(w * 0.58f, h * 0.22f)
                close()
            }
            drawPath(rightEar, color = NeonViolet)

            val rightInner = Path().apply {
                moveTo(w * 0.78f, h * 0.30f)
                lineTo(w * 0.82f, h * 0.14f)
                lineTo(w * 0.64f, h * 0.23f)
                close()
            }
            drawPath(rightInner, color = NekoBlush)

            // Face base
            drawCircle(
                color = Color(0xFF1E1838),
                radius = w * 0.38f,
                center = Offset(w * 0.5f, h * 0.55f)
            )

            // Eyes / Expression
            when (expression) {
                KuroExpression.BLUSHING -> {
                    // Closed flustered eyes > <
                    drawLine(ElectricCyan, Offset(w * 0.30f, h * 0.48f), Offset(w * 0.42f, h * 0.52f), strokeWidth = 3f)
                    drawLine(ElectricCyan, Offset(w * 0.30f, h * 0.56f), Offset(w * 0.42f, h * 0.52f), strokeWidth = 3f)
                    drawLine(ElectricCyan, Offset(w * 0.70f, h * 0.48f), Offset(w * 0.58f, h * 0.52f), strokeWidth = 3f)
                    drawLine(ElectricCyan, Offset(w * 0.70f, h * 0.56f), Offset(w * 0.58f, h * 0.52f), strokeWidth = 3f)
                    // Blush
                    drawCircle(NekoPink.copy(alpha = 0.6f), radius = w * 0.07f, center = Offset(w * 0.28f, h * 0.62f))
                    drawCircle(NekoPink.copy(alpha = 0.6f), radius = w * 0.07f, center = Offset(w * 0.72f, h * 0.62f))
                }
                KuroExpression.POUTING -> {
                    // Half-closed haughty eyes
                    drawLine(ElectricCyan, Offset(w * 0.32f, h * 0.48f), Offset(w * 0.44f, h * 0.50f), strokeWidth = 3.5f)
                    drawCircle(ElectricCyan, radius = w * 0.04f, center = Offset(w * 0.38f, h * 0.53f))

                    drawLine(ElectricCyan, Offset(w * 0.68f, h * 0.48f), Offset(w * 0.56f, h * 0.50f), strokeWidth = 3.5f)
                    drawCircle(ElectricCyan, radius = w * 0.04f, center = Offset(w * 0.62f, h * 0.53f))
                }
                KuroExpression.HAPPY, KuroExpression.CALM -> {
                    // Sparkling anime eyes
                    drawCircle(ElectricCyan, radius = w * 0.07f, center = Offset(w * 0.36f, h * 0.50f))
                    drawCircle(Color.White, radius = w * 0.025f, center = Offset(w * 0.38f, h * 0.48f))

                    drawCircle(ElectricCyan, radius = w * 0.07f, center = Offset(w * 0.64f, h * 0.50f))
                    drawCircle(Color.White, radius = w * 0.025f, center = Offset(w * 0.66f, h * 0.48f))
                }
            }

            // Nose & Neko mouth :3
            drawCircle(NekoPink, radius = w * 0.025f, center = Offset(w * 0.5f, h * 0.62f))

            val mouth = Path().apply {
                moveTo(w * 0.44f, h * 0.68f)
                quadraticTo(w * 0.47f, h * 0.72f, w * 0.50f, h * 0.69f)
                quadraticTo(w * 0.53f, h * 0.72f, w * 0.56f, h * 0.68f)
            }
            drawPath(
                mouth,
                color = NekoBlush,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
            )
        }
    }
}

enum class KuroExpression {
    CALM,
    HAPPY,
    BLUSHING,
    POUTING
}
