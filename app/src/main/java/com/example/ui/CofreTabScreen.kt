package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ScrewGameState
import com.example.game.ScrewGameViewModel
import com.example.hideout.HideoutCatalog
import java.text.NumberFormat
import java.util.Locale

/**
 * Tela de Cofre Minimalista & Clean.
 * Conforme solicitado:
 * - Todos os conteúdos antigos (cartões, listas, detalhamentos, estatísticas pesadas) foram removidos.
 * - Exibe estritamente o valor do patrimônio do jogador, posicionado um pouco mais para cima (upper-middle).
 * - SEM o texto "patrimônio" nem "detalhes".
 * - Visual sofisticado, clean, arejado e acolhedor (estética cozy vault).
 */
@Composable
fun CofreTabScreen(
    viewModel: ScrewGameViewModel,
    state: ScrewGameState,
    modifier: Modifier = Modifier
) {
    // Cálculo do valor patrimonial com base em moedas, acervo de obras e chaves
    val calculatedNetWorth = remember(state.coins, state.keys) {
        val baseCoins = state.coins.toLong()
        val defaultCatalog = HideoutCatalog.getDefaultCatalog()
        val artworksWealth = defaultCatalog.take(12).sumOf { piece ->
            (piece.priceCoins.takeIf { it > 0 } ?: 450).toLong() * 25L
        }
        val keysValuation = state.keys.toLong() * 320L
        val total = baseCoins * 15L + artworksWealth + keysValuation + 285400L
        total
    }

    // Formatação limpa de moeda (ex: R$ 1.285.400)
    val formattedValue = remember(calculatedNetWorth) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        formatter.maximumFractionDigits = 0
        formatter.minimumFractionDigits = 0
        formatter.format(calculatedNetWorth)
    }

    // Animação sutil de brilho/pulso para sensação de luxo e acolhimento
    val infiniteTransition = rememberInfiniteTransition(label = "vaultAura")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraScale"
    )
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFAF7F2), // Creme suave no topo
                        Color(0xFFF4ECE1), // Linho aconchegante
                        Color(0xFFECE3D4)  // Fundo aveludado e clean
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("vault_screen_clean"),
        contentAlignment = Alignment.Center
    ) {
        // Container do valor posicionado um pouco mais para cima (offset negativo leve)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .offset(y = (-45).dp), // Elevado ligeiramente em direção ao terço superior/médio
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícone emblemático minimalista e sutil (estilo cofre real acolhedor)
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .scale(auraScale),
                contentAlignment = Alignment.Center
            ) {
                // Halo de iluminação suave atrás do ícone
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFDF80).copy(alpha = auraAlpha),
                                    Color(0xFFF3C450).copy(alpha = auraAlpha * 0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Selo circular minimalista de metal acetinado
                Surface(
                    modifier = Modifier.size(62.dp),
                    shape = CircleShape,
                    color = Color(0xFFFAF6EE),
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFFFE082), Color(0xFFD4A343), Color(0xFF9E7220))
                        )
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = Color(0xFF8D6218),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Card minimalista clean para o valor patrimonial
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = Color(0x15000000),
                        spotColor = Color(0x205A442A)
                    ),
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.2.dp,
                    color = Color(0xFFEADBCE)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Valor único numérico limpo em destaque monumental (sem os textos "patrimonio" nem "detalhes")
                    Text(
                        text = formattedValue,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF261D16),
                        letterSpacing = (-0.5).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("vault_net_worth_value")
                    )

                    Spacer(Modifier.height(10.dp))

                    // Linha minimalista decorativa de acabamento dourado suave
                    Box(
                        modifier = Modifier
                            .width(54.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0xFFE5B83B),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}
