package com.example.game

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

/**
 * Modificadores para o Modo Assalto (Boss Rush / Heist).
 * Cada obra obtida na saga adiciona um modificador cumulativo (de 1 a 5).
 */
enum class AssaultModifier(
    val title: String,
    val subtitle: String,
    val description: String,
    val iconEmoji: String,
    val badgeColor: Color
) {
    TIME_RUSH(
        title = "Corrida Contra o Tempo",
        subtitle = "Tempo de Alarme Ativo",
        description = "O alarme da segurança foi disparado! Você tem tempo limite para desparafusar tudo.",
        iconEmoji = "⏱️",
        badgeColor = Color(0xFFE53935)
    ),
    SHAKE_TREMOR(
        title = "Tremor Sísmico",
        subtitle = "Câmera Instável",
        description = "A estrutura do cofre treme a cada parafuso retirado, exigindo foco total.",
        iconEmoji = "⚡",
        badgeColor = Color(0xFFF57C00)
    ),
    REDUCED_SLOT(
        title = "Espaço Confinado",
        subtitle = "Doca Limitada",
        description = "As dimensões do cofre bloqueiam 1 slot da doca, reduzindo a margem de erro.",
        iconEmoji = "🔒",
        badgeColor = Color(0xFF8E24AA)
    ),
    CONCEALED_SCREWS(
        title = "Névoa de Dissimulação",
        subtitle = "Cores Camufladas",
        description = "Gás de alarme misterioso camufla parafusos do fundo até você liberar a visão.",
        iconEmoji = "🌫️",
        badgeColor = Color(0xFF00897B)
    ),
    SPEED_COMBO_DEMAND(
        title = "Fuga Acelerada",
        subtitle = "Exige Combos Rápidos",
        description = "A adrenalina do assalto exige ritmo veloz: mantenha o medidor sem zerar para triunfar.",
        iconEmoji = "🔥",
        badgeColor = Color(0xFFD81B60)
    );

    companion object {
        fun getModifiersForCollectedCount(collectedCount: Int): List<AssaultModifier> {
            val count = collectedCount.coerceIn(1, 5)
            return values().take(count)
        }
    }
}

data class AssaultSagaStatus(
    val sagaId: Int,
    val isSagaCompleted: Boolean,
    val collectedObrasCount: Int,
    val canPlayToday: Boolean,
    val secondsUntilDailyReset: Long,
    val activeModifiers: List<AssaultModifier>
)
