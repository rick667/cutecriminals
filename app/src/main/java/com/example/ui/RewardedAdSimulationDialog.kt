package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Diálogo acolhedor e visual para simulação/sandbox de anúncios premiados.
 *
 * Utilizado automaticamente no ambiente de testes (emuladores e depuração)
 * quando os servidores do Google AdMob não retornam anúncios reais por falta
 * de conta Play Store logada ou restrições de IP de emuladores.
 *
 * Garante que o desenvolvedor e o jogador possam testar e receber 100% das
 * recompensas diretamente no emulador do AI Studio sem precisar publicar o app!
 */
@Composable
fun RewardedAdSimulationDialog(
    rewardType: String,
    onRewardCompleted: () -> Unit,
    onDismiss: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var secondsLeft by remember { mutableIntStateOf(3) }

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(100L)
            progress += 0.035f
            secondsLeft = (3f * (1f - progress)).toInt() + 1
        }
        progress = 1f
        secondsLeft = 0
        delay(200L)
        onRewardCompleted()
    }

    val rewardLabel = when (rewardType) {
        "FREE_EXTRA_SLOT" -> "🔓 +1 Espaço Extra na Doca"
        "HINT" -> "💡 +1 Dica do Hint Manager"
        "UNDO" -> "↩️ +1 Desfazer Movimento"
        "ORGANIZE" -> "🔀 +1 Organizar Parafusos"
        "SLOT" -> "📦 +1 Slot de Inventário"
        "DOUBLE_COINS" -> "🪙 Dobrar Ouro da Vitória (2x)"
        "FREE_GOLD" -> "💰 +250 Moedas de Ouro Grátis"
        else -> "🎁 Recompensa Especial do Jogo"
    }

    JuicyDialog(
        type = JuicyModalType.SMALL,
        onDismiss = onDismiss,
        testTag = "rewarded_ad_modal"
    ) {
        Spacer(Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(PastelPowderSkyLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartDisplay,
                contentDescription = null,
                tint = PastelPowderSkyDark,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "🎬 Vídeo Premiado (Teste)",
            color = CozyTextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Ambiente de Testes / Google AdMob Sandbox",
            color = CozyTextSecondary,
            fontSize = 10.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        // Barra de progresso do vídeo simulado
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = PastelPowderSkyDark,
            trackColor = CozySurfaceSubtle
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (secondsLeft > 0) "Concluindo em ${secondsLeft}s..." else "✓ Recompensa Pronta!",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (secondsLeft > 0) CozyTextSecondary else Color(0xFF2E7D32)
        )

        Spacer(Modifier.height(10.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFFF8E1),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = rewardLabel,
                    color = Color(0xFFC78500),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Botão para resgate imediato sem precisar esperar a contagem
        JuicyRectangularButton(
            onClick = { onRewardCompleted() },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            tint = ScrewGold,
            testTag = "claim_ad_reward_button"
        ) {
            Text(
                text = "COLETAR RECOMPENSA AGORA",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = Color.White
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "💡 Você não precisa publicar na Play Store para testar. Seus especiais funcionam 100% no ambiente de testes!",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2E7D32),
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )

        Spacer(Modifier.height(6.dp))

        val diagnosticText by com.example.ads.AdMobRewardedManager.lastDiagnosticMessage.collectAsState()
        Text(
            text = "Status AdMob: $diagnosticText",
            fontSize = 9.sp,
            color = CozyTextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}
