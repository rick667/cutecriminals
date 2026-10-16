package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.haptics.HapticFeedbackManager
import com.example.hideout.ArtPiece
import com.example.hideout.ArtThumbnailComposable
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Cena de Revelação de Obra Resgatada:
 * - Exibida após o modal de vitória nas fases de chefão e finais de saga.
 * - Estilo 100% harmonioso com o jogo: tons claros, alegres, festivos e aconchegantes.
 * - Fundo com raios solares dourados giratórios (God Rays), confetes coloridos e estrelas cintilantes.
 */
@Composable
fun ArtworkVictoryCelebrationScene(
    artPiece: ArtPiece,
    convertedCoins: Int = 0,
    haptics: HapticFeedbackManager,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    var timeSeconds by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val startNanos = System.nanoTime()
        while (true) {
            withFrameNanos { now ->
                timeSeconds = (now - startNanos) / 1_000_000_000f
            }
        }
    }

    val artScale = remember { Animatable(0.4f) }
    val bannerScale = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        haptics.playMatchMerge()
        artScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        bannerScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        )
    }

    // Confetes festivos em tons pastéis alegres
    val confettiFlakes = remember {
        val pastelColors = listOf(
            Color(0xFFFFB74D), // Ouro suave
            Color(0xFF81C784), // Menta alegre
            Color(0xFF64B5F6), // Azul céu
            Color(0xFFBA68C8), // Violeta festivo
            Color(0xFFFF8A80), // Coral
            Color(0xFFFFD54F)  // Amarelo solar
        )
        (0 until 38).map { i ->
            Triple(
                (i * 0.026f + (i % 6) * 0.16f) % 1.0f,
                40f + (i % 5) * 20f,
                pastelColors[i % pastelColors.size]
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF9EE), // Creme claro e acolhedor
                        Color(0xFFF7EEDB), // Linho suave
                        Color(0xFFEFE2C8)  // Âmbar suave claro
                    )
                )
            )
            .testTag("artwork_celebration_scene"),
        contentAlignment = Alignment.Center
    ) {
        // Raios de Sol dourados e alegres em rotação suave (God Rays claros)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.45f)
            val maxRadius = hypot(size.width, size.height)
            val rayCount = 14
            val angleStep = 360f / rayCount
            val currentAngle = (timeSeconds * 16f) % 360f

            for (i in 0 until rayCount) {
                val startAngle = (i * angleStep + currentAngle) * (Math.PI / 180.0)
                val endAngle = ((i * angleStep + angleStep * 0.5f) + currentAngle) * (Math.PI / 180.0)
                val path = Path().apply {
                    moveTo(center.x, center.y)
                    lineTo(
                        center.x + (maxRadius * cos(startAngle)).toFloat(),
                        center.y + (maxRadius * sin(startAngle)).toFloat()
                    )
                    lineTo(
                        center.x + (maxRadius * cos(endAngle)).toFloat(),
                        center.y + (maxRadius * sin(endAngle)).toFloat()
                    )
                    close()
                }
                drawPath(path, color = Color(0xFFFFE082).copy(alpha = 0.35f))
            }
        }

        // Confetes caindo
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            for ((xRatio, speed, color) in confettiFlakes) {
                val currentY = (speed * timeSeconds * 1.5f) % (canvasH + 60f)
                val currentX = (xRatio * canvasW) + sin(timeSeconds * 2f + xRatio * 10f) * 16f

                drawCircle(
                    color = color.copy(alpha = 0.85f),
                    radius = 4.5f,
                    center = Offset(currentX, currentY)
                )
            }
        }

        // Card Glorioso Central da Obra (Estilo Cozy / Pergaminho Claro)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .padding(vertical = 16.dp)
        ) {
            // Faixa Superior
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFFFD54F),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFC79822)),
                shadowElevation = 6.dp,
                modifier = Modifier.scale(bannerScale.value)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFF5D4037),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (convertedCoins > 0) "★ OBRA REPETIDA CONVERTIDA! ★" else "★ OBRA-PRIMA RESGATADA! ★",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF4A3728),
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Moldura da Obra
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF8)),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE2D5C3)),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .scale(artScale.value)
                    .fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Thumbnail Ampliado com Pedestal
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(0xFFFFF9C4),
                                        Color(0xFFFFF8E1),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        ArtThumbnailComposable(
                            artPiece = artPiece,
                            modifier = Modifier.size(135.dp),
                            showPedestalForRelics = true
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = artPiece.title,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF3E2723),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = artPiece.author,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8D6E63),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (convertedCoins > 0) Color(0xFFFFF3E0) else Color(0xFFE8F5E9),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (convertedCoins > 0) Color(0xFFFFB74D) else Color(0xFFA5D6A7)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (convertedCoins > 0) Icons.Default.AutoAwesome else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (convertedCoins > 0) Color(0xFFE65100) else Color(0xFF2E7D32),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (convertedCoins > 0)
                                    "Você já possui esta obra! Convertida em +$convertedCoins 🪙"
                                else
                                    "Adicionada com honra ao seu Esconderijo!",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (convertedCoins > 0) Color(0xFFE65100) else Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            JuicyRectangularButton(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                testTag = "collect_artwork_btn"
            ) {
                Text(
                    text = "COLOCAR NO ESCONDERIJO",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color(0xFF1B281E)
                )
            }
        }
    }
}
