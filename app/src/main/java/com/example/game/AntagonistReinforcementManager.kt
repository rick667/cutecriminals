package com.example.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

/**
 * Representa os dados da mecânica de "Reforço do Antagonista / Emboscada":
 * Ao atingir um certo percentual de progresso (ex: 50% ou 60%), o antagonista adiciona
 * camadas extras de placas e parafusos que entram pelas laterais da tela com tremor de impacto!
 */
data class AntagonistReinforcementConfig(
    val triggerThresholdFraction: Float, // ex: 0.50f (50%)
    val extraPlates: List<Plate>,
    val extraScrews: List<Screw>,
    val antagonistMessage: String = "⚠️ O Antagonista interveio com placas adicionais!"
)

object AntagonistReinforcementManager {

    /**
     * Determina se a fase possui a mecânica de reforço do antagonista.
     * Introduzida no final da primeira saga (Fase 18, 19, 20) e recorrente em fases intermediárias
     * e avançadas das sagas subsequentes.
     */
    fun getReinforcementConfigForLevel(levelNumber: Int): AntagonistReinforcementConfig? {
        // Apresentada nas fases finais da Saga 1 (18, 19, 20)
        // E em fases selecionadas das sagas seguintes (ex: a cada 6 fases ou fases de chefão/semi-chefão)
        val hasMechanic = when {
            levelNumber == 22 -> true // Fase 22: DEDICADA à introdução do Reforço do Antagonista!
            levelNumber > 22 && (levelNumber % 20 == 0 || (levelNumber > 30 && levelNumber % 15 == 0)) -> true
            else -> false
        }

        if (!hasMechanic) return null

        val triggerFraction = when {
            levelNumber % 20 == 0 -> 0.50f // Fases finais de saga disparam na metade exata (50%)
            levelNumber % 2 == 0 -> 0.45f
            else -> 0.55f
        }

        val (extraPlates, extraScrews) = generateReinforcementBatch(levelNumber)

        val message = when {
            levelNumber <= 20 -> "🚨 O Rival interveio! Reforços laterais deslizando no tabuleiro!"
            levelNumber % 20 == 0 -> "👑 EMBOSCADA DO CHEFÃO: Novas placas e parafusos trancados!"
            else -> "⚠️ ALERTA: O Antagonista reforçou o puzzle pelas laterais!"
        }

        return AntagonistReinforcementConfig(
            triggerThresholdFraction = triggerFraction,
            extraPlates = extraPlates,
            extraScrews = extraScrews,
            antagonistMessage = message
        )
    }

    /**
     * Gera 2 placas extras (uma entrando pela esquerda e outra pela direita)
     * com 6 parafusos no total (2 trios perfeitos, garantindo solvabilidade de doca limpa).
     */
    fun generateReinforcementBatch(levelNumber: Int): Pair<List<Plate>, List<Screw>> {
        val seed = levelNumber * 777L
        val rng = java.util.Random(seed)

        val colorPool = listOf(
            ScrewColor.RED, ScrewColor.BLUE, ScrewColor.YELLOW,
            ScrewColor.GREEN, ScrewColor.PURPLE, ScrewColor.ORANGE
        ).shuffled(rng)

        val colorA = colorPool[0]
        val colorB = colorPool[1]

        // 2 Placas:
        // Placa 1: Entra pela esquerda (-1f slideDirection)
        // Placa 2: Entra pela direita (+1f slideDirection)
        val plateLeftId = "antag_plate_L_$levelNumber"
        val plateRightId = "antag_plate_R_$levelNumber"

        val plateLeftBounds = Rect(0.12f, 0.22f, 0.46f, 0.50f)
        val plateRightBounds = Rect(0.54f, 0.38f, 0.88f, 0.66f)

        // 6 parafusos balanceados em 2 trios (3 de colorA e 3 de colorB)
        // 3 no lado esquerdo, 3 no lado direito
        val screwL1 = Screw(
            id = "antag_s_L1_$levelNumber",
            color = colorA,
            normalizedPos = Offset(0.20f, 0.28f),
            layerIndex = 3,
            holdingPlateIds = listOf(plateLeftId),
            slideInFromDirection = -1f // Esquerda
        )
        val screwL2 = Screw(
            id = "antag_s_L2_$levelNumber",
            color = colorA,
            normalizedPos = Offset(0.38f, 0.28f),
            layerIndex = 3,
            holdingPlateIds = listOf(plateLeftId),
            slideInFromDirection = -1f
        )
        val screwL3 = Screw(
            id = "antag_s_L3_$levelNumber",
            color = colorB,
            normalizedPos = Offset(0.29f, 0.44f),
            layerIndex = 3,
            holdingPlateIds = listOf(plateLeftId),
            slideInFromDirection = -1f
        )

        val screwR1 = Screw(
            id = "antag_s_R1_$levelNumber",
            color = colorB,
            normalizedPos = Offset(0.62f, 0.44f),
            layerIndex = 3,
            holdingPlateIds = listOf(plateRightId),
            slideInFromDirection = 1f // Direita
        )
        val screwR2 = Screw(
            id = "antag_s_R2_$levelNumber",
            color = colorB,
            normalizedPos = Offset(0.80f, 0.44f),
            layerIndex = 3,
            holdingPlateIds = listOf(plateRightId),
            slideInFromDirection = 1f
        )
        val screwR3 = Screw(
            id = "antag_s_R3_$levelNumber",
            color = colorA,
            normalizedPos = Offset(0.71f, 0.60f),
            layerIndex = 3,
            holdingPlateIds = listOf(plateRightId),
            slideInFromDirection = 1f
        )

        val plateLeft = Plate(
            id = plateLeftId,
            name = "Reforço Rival Noroeste",
            color = PlateWarmClay,
            layerIndex = 3,
            bounds = plateLeftBounds,
            screwHoleIds = listOf(screwL1.id, screwL2.id, screwL3.id),
            slideInFromDirection = -1f // Entra pela esquerda
        )

        val plateRight = Plate(
            id = plateRightId,
            name = "Reforço Rival Sudeste",
            color = PlateDustyLilac,
            layerIndex = 3,
            bounds = plateRightBounds,
            screwHoleIds = listOf(screwR1.id, screwR2.id, screwR3.id),
            slideInFromDirection = 1f // Entra pela direita
        )

        return Pair(
            listOf(plateLeft, plateRight),
            listOf(screwL1, screwL2, screwL3, screwR1, screwR2, screwR3)
        )
    }
}
