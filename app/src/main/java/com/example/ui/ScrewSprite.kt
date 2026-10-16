package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.game.ScrewColor
import com.example.game.ScrewPattern

/**
 * Reusable, dynamically tintable X-Head Screw Sprite (Phillips Head).
 */
@Composable
fun ScrewHeadXSprite(
    tintColor: Color,
    modifier: Modifier = Modifier,
    darkShadeColor: Color? = null,
    lightHighlightColor: Color? = null,
    size: Dp = 46.dp,
    rotationDegrees: Float = 0f,
    elevation: Dp = 4.dp,
    showGlossHighlight: Boolean = true
) {
    val darkShade = darkShadeColor ?: tintColor.copy(alpha = 1f)
    val lightHighlight = lightHighlightColor ?: Color.White

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationZ = rotationDegrees
                shadowElevation = elevation.toPx()
                shape = CircleShape
                clip = true
            }
            .background(
                Brush.radialGradient(
                    listOf(
                        lightHighlight,
                        tintColor,
                        darkShade
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Dynamic White Base Vector Sprite (tintable via ColorFilter)
        Image(
            painter = painterResource(id = R.drawable.ic_screw_head_x),
            contentDescription = "Parafuso Cabeça X",
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(tintColor)
        )

        // Soft Glaze Top Highlight
        if (showGlossHighlight) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.5.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f))
            )
        }
    }
}

/**
 * Overload of [ScrewHeadXSprite] taking the game's [ScrewColor] enum directly.
 */
@Composable
fun ScrewHeadXSprite(
    screwColor: ScrewColor,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    rotationDegrees: Float = 0f,
    elevation: Dp = 4.dp,
    showGlossHighlight: Boolean = true
) {
    ScrewHeadXSprite(
        tintColor = screwColor.primaryColor,
        darkShadeColor = screwColor.darkShade,
        lightHighlightColor = screwColor.lightHighlight,
        modifier = modifier,
        size = size,
        rotationDegrees = rotationDegrees,
        elevation = elevation,
        showGlossHighlight = showGlossHighlight
    )
}

/**
 * Returns the accent theme color associated with a [ScrewPattern].
 */
fun getPatternAccentColor(pattern: ScrewPattern): Color {
    return when (pattern) {
        ScrewPattern.STARRY -> Color(0xFFF59E0B)  // Golden Amber
        ScrewPattern.STRIPED -> Color(0xFF0284C7) // Sky Blue Enamel
        ScrewPattern.POLKA -> Color(0xFFE11D48)   // Vibrant Coral Pink
        ScrewPattern.SPIRAL -> Color(0xFF7C3AED)  // Royal Lavender
        ScrewPattern.NONE -> Color.Transparent
    }
}

/**
 * Cozy, tactile pattern overlay rendered in the center of a screw head.
 * Replaces harsh black backgrounds with warm ivory porcelain discs and polished theme accents.
 */
@Composable
fun CozyScrewPatternOverlay(
    pattern: ScrewPattern,
    modifier: Modifier = Modifier,
    diameter: Dp = 24.dp
) {
    if (pattern == ScrewPattern.NONE) return

    val accentColor = getPatternAccentColor(pattern)

    Box(
        modifier = modifier
            .size(diameter)
            .shadow(2.dp, CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFFFFBF2),
                        Color(0xFFF7EEDD)
                    )
                ),
                CircleShape
            )
            .border(1.5.dp, accentColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when (pattern) {
            ScrewPattern.STARRY -> {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Padrão Estrela",
                    tint = accentColor,
                    modifier = Modifier.fillMaxSize(0.72f)
                )
            }
            ScrewPattern.STRIPED -> {
                Canvas(modifier = Modifier.fillMaxSize(0.80f)) {
                    val stroke = 2.8.dp.toPx()
                    val w = size.width
                    val h = size.height
                    // Diagonal Candy Stripes
                    drawLine(accentColor, Offset(w * 0.15f, h * 0.5f), Offset(w * 0.5f, h * 0.85f), stroke, cap = StrokeCap.Round)
                    drawLine(accentColor, Offset(w * 0.15f, h * 0.15f), Offset(w * 0.85f, h * 0.85f), stroke, cap = StrokeCap.Round)
                    drawLine(accentColor, Offset(w * 0.5f, h * 0.15f), Offset(w * 0.85f, h * 0.5f), stroke, cap = StrokeCap.Round)
                }
            }
            ScrewPattern.POLKA -> {
                Canvas(modifier = Modifier.fillMaxSize(0.78f)) {
                    val c = Offset(size.width / 2f, size.height / 2f)
                    val r = 2.2.dp.toPx()
                    val offset = 4.8.dp.toPx()
                    drawCircle(accentColor, r, c)
                    drawCircle(accentColor, r * 0.85f, Offset(c.x - offset, c.y - offset))
                    drawCircle(accentColor, r * 0.85f, Offset(c.x + offset, c.y - offset))
                    drawCircle(accentColor, r * 0.85f, Offset(c.x - offset, c.y + offset))
                    drawCircle(accentColor, r * 0.85f, Offset(c.x + offset, c.y + offset))
                }
            }
            ScrewPattern.SPIRAL -> {
                Canvas(modifier = Modifier.fillMaxSize(0.82f)) {
                    val c = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(accentColor, radius = 2.5.dp.toPx(), center = c, style = Stroke(1.8.dp.toPx()))
                    drawCircle(accentColor.copy(alpha = 0.55f), radius = 5.2.dp.toPx(), center = c, style = Stroke(1.8.dp.toPx()))
                    drawCircle(accentColor, radius = 7.8.dp.toPx(), center = c, style = Stroke(1.5.dp.toPx()))
                }
            }
            else -> {}
        }
    }
}

/**
 * Cozy enamel pin corner badge for high-clarity pattern identification.
 */
@Composable
fun CozyPatternBadge(
    pattern: ScrewPattern,
    modifier: Modifier = Modifier
) {
    if (pattern == ScrewPattern.NONE) return

    val accentColor = getPatternAccentColor(pattern)
    val (label, icon) = when (pattern) {
        ScrewPattern.STARRY -> Pair("★", Icons.Default.Star)
        ScrewPattern.STRIPED -> Pair("///", null)
        ScrewPattern.POLKA -> Pair("••", null)
        ScrewPattern.SPIRAL -> Pair("@", null)
        else -> Pair("", null)
    }

    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFFFF9EE))
                ),
                RoundedCornerShape(8.dp)
            )
            .border(1.2.dp, accentColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 4.dp, vertical = 1.5.dp),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(10.dp)
            )
        } else {
            Text(
                text = label,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
        }
    }
}
