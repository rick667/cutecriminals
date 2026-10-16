package com.example.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class ScrewColor(
    val title: String,
    val primaryColor: Color,
    val darkShade: Color,
    val lightHighlight: Color,
    val crossColor: Color = Color.White
) {
    RED("Morango Doce", Color(0xFFE84A5F), Color(0xFF9E1F30), Color(0xFFFF8595), Color(0xFFFFF0F2)),
    BLUE("Azul Céu", Color(0xFF4A89DC), Color(0xFF285494), Color(0xFF85B5FF), Color(0xFFF0F5FF)),
    GREEN("Menta Esmeralda", Color(0xFF38B27A), Color(0xFF1E6E49), Color(0xFF75E3B0), Color(0xFFF0FFF8)),
    YELLOW("Âmbar Dourado", Color(0xFFF5B025), Color(0xFFA67107), Color(0xFFFFD573), Color(0xFFFFFDF0)),
    PURPLE("Lavanda Imperial", Color(0xFF9660D4), Color(0xFF5D3191), Color(0xFFC79CFF), Color(0xFFF8F0FF)),
    ORANGE("Tangerina Viva", Color(0xFFF36F38), Color(0xFFA83F11), Color(0xFFFF9F75), Color(0xFFFFF4EE)),
    TEAL("Turquesa Menta", Color(0xFF1ABC9C), Color(0xFF117A65), Color(0xFF64E8CF), Color(0xFFE8F8F5)),
    PINK("Rosa Flamingo", Color(0xFFEC407A), Color(0xFFAD1457), Color(0xFFFF80AB), Color(0xFFFCE4EC));
}

enum class ScrewSkinType(
    val displayName: String,
    val subtitle: String,
    val priceCoins: Int,
    val isUnlockedByDefault: Boolean = false
) {
    CLASSIC_METAL("Botão Pastel Cozy", "Biselado aveludado e visual acolhedor", 0, true),
    PRISM_GLASS("Porcelana & Quartzo", "Peças de cerâmica esmaltada pastel", 150),
    WOOL_PIN("Feltro & Lã Artesanal", "Pinos de costura aconchegantes com toque artesanal", 300)
}

enum class PlateSkinType(
    val displayName: String,
    val subtitle: String,
    val priceCoins: Int,
    val isUnlockedByDefault: Boolean = false
) {
    PASTEL_ACRYLIC("Acrílico Macio Pastel", "Placas suaves com acabamento acetinado", 0, true),
    POLISHED_WOOD("Madeira Clara Nórdica", "Carvalho suave e pinho escandinavo claro", 200),
    ARCTIC_ICE("Cerâmica Fina", "Placas de louça esmaltada em tons pastéis", 350)
}

enum class ScrewPattern(val displayName: String) {
    NONE("Liso"),
    STRIPED("Listrado"),
    STARRY("Estrelado"),
    POLKA("Pontilhado"),
    SPIRAL("Espiral")
}

data class MatchToken(
    val color: ScrewColor,
    val pattern: ScrewPattern
)

data class Screw(
    val id: String,
    val color: ScrewColor,
    val normalizedPos: Offset, // Relative coordinates inside game board (0.0 .. 1.0)
    val layerIndex: Int,       // 0 = bottom, 1 = mid, 2 = top layer
    val isUnscrewed: Boolean = false,
    val isOccluded: Boolean = false,
    val holdingPlateIds: List<String> = emptyList(), // Which plates this screw is pinning down
    val pattern: ScrewPattern = ScrewPattern.NONE,
    val isRainbow: Boolean = false,
    val dualColor: ScrewColor? = null,
    val countdownMatchesRequired: Int = 0,
    val countdownRemaining: Int = countdownMatchesRequired,
    val slideInFromDirection: Float = 0f, // 0f = normal, -1f = desliza da esquerda, +1f = desliza da direita
    val iceLayersRemaining: Int = 0, // 0 = sem gelo, > 0 = congelado (precisa de quedas de placa ou match 3 para quebrar o gelo)
    val isChameleonGear: Boolean = false, // Parafuso de Engrenagem Rotativa (alterna de cor a cada movimento no tabuleiro)
    val gearAltColor: ScrewColor? = null,
    val pressureDetonatorTurnsRemaining: Int = 0, // > 0 = Parafuso de Pressão / Detonador (precisa ser retirado antes de estourar)
    val sentToDock: Boolean = false // Marcado quando foi enviado para a doca (para não renderizar clone no tabuleiro)
) {
    fun matchingTokens(): List<MatchToken> {
        if (isRainbow) return emptyList()
        val colors = if (dualColor != null) listOf(color, dualColor) else listOf(color)
        return colors.map { c -> MatchToken(c, pattern) }
    }
}

fun canMatchTriplet(s1: Screw, s2: Screw, s3: Screw): MatchToken? {
    val triplet = listOf(s1, s2, s3)
    val nonRainbow = triplet.filter { !it.isRainbow }
    if (nonRainbow.isEmpty()) {
        return MatchToken(ScrewColor.YELLOW, ScrewPattern.NONE)
    }
    val firstTokens = nonRainbow.first().matchingTokens()
    for (candidate in firstTokens) {
        val allMatch = nonRainbow.drop(1).all { other ->
            other.matchingTokens().any { it == candidate }
        }
        if (allMatch) return candidate
    }
    return null
}

data class Plate(
    val id: String,
    val name: String,
    val color: Color,
    val layerIndex: Int,
    val bounds: Rect,         // Normalized bounds covering screws underneath
    val screwHoleIds: List<String>, // Screws that lock this plate in place
    val isFallen: Boolean = false,
    val isHanging: Boolean = false,
    val currentRotationDegrees: Float = 0f,
    val pivotNormalizedOffset: Offset? = null,
    val requiredDockCombination: List<ScrewColor>? = null,
    val isCombinationUnlocked: Boolean = false,
    val slideInFromDirection: Float = 0f // 0f = normal, -1f = desliza da esquerda, +1f = desliza da direita
)

data class DockSlotHoverMatchInfo(
    val isActivated: Boolean = false,
    val isTargetEmptySlot: Boolean = false,
    val matchingColor: ScrewColor? = null,
    val matchProgress: Int = 0, // 1 (inicia grupo), 2 (forma par), 3 (completa trio)
    val willCompleteTriplet: Boolean = false,
    val proximityFactor: Float = 0f // 0.0 .. 1.0
)

object DockMatchingGuidanceEngine {
    fun doesScrewMatch(s1: Screw, s2: Screw): Boolean {
        if (s1.isRainbow || s2.isRainbow) return true
        if (s1.color == s2.color) return true
        if (s1.dualColor != null && (s1.dualColor == s2.color || s1.dualColor == s2.dualColor)) return true
        if (s2.dualColor != null && s2.dualColor == s1.color) return true
        return false
    }

    fun evaluate(
        slotIndex: Int,
        dockScrews: List<Screw?>,
        hoveringScrew: Screw?,
        hoveringPos: Offset?,
        slotOffsetInRoot: Offset?,
        maxProximityPx: Float = 520f
    ): DockSlotHoverMatchInfo {
        if (hoveringScrew == null || hoveringPos == null || slotOffsetInRoot == null) {
            return DockSlotHoverMatchInfo()
        }

        val slotCenter = slotOffsetInRoot + Offset(55f, 55f)
        val dist = (hoveringPos - slotCenter).getDistance()
        val verticalDist = slotCenter.y - hoveringPos.y
        val isNearDockRow = verticalDist in -80f..560f && kotlin.math.abs(hoveringPos.x - slotCenter.x) < 700f

        val baseProximity = (1f - (dist / maxProximityPx)).coerceIn(0f, 1f)
        val proximity = if (isNearDockRow) {
            maxOf(baseProximity, ((560f - verticalDist) / 560f).coerceIn(0.25f, 1f))
        } else {
            baseProximity
        }

        if (proximity <= 0.05f) {
            return DockSlotHoverMatchInfo()
        }

        val dockScrew = dockScrews.getOrNull(slotIndex)
        val targetEmptySlot = dockScrews.indexOfFirst { it == null }

        val existingMatches = dockScrews.filterNotNull().count { s ->
            doesScrewMatch(s, hoveringScrew)
        }
        val willComplete = (existingMatches >= 2)
        val nextProgress = (existingMatches + 1).coerceAtMost(3)

        if (dockScrew != null) {
            val isColorMatch = doesScrewMatch(dockScrew, hoveringScrew)
            return if (isColorMatch) {
                DockSlotHoverMatchInfo(
                    isActivated = true,
                    isTargetEmptySlot = false,
                    matchingColor = hoveringScrew.color,
                    matchProgress = nextProgress,
                    willCompleteTriplet = willComplete,
                    proximityFactor = proximity
                )
            } else {
                DockSlotHoverMatchInfo(isActivated = false)
            }
        }

        if (slotIndex == targetEmptySlot) {
            return DockSlotHoverMatchInfo(
                isActivated = true,
                isTargetEmptySlot = true,
                matchingColor = hoveringScrew.color,
                matchProgress = nextProgress,
                willCompleteTriplet = willComplete,
                proximityFactor = proximity
            )
        }

        return DockSlotHoverMatchInfo(isActivated = false)
    }
}

data class MoveHistory(
    val screwId: String,
    val previousOccludedScrews: List<String>,
    val fallenPlates: List<String>,
    val dockSlotIndex: Int,
    val matchedScrewsCleared: List<Screw> = emptyList(),
    val matchedDockIndices: List<Int> = emptyList()
)

enum class GameDifficulty {
    STANDARD_5_SLOTS,
    EXPANDED_7_SLOTS
}

enum class GameScreen {
    SPLASH,
    APP_LOADING,
    MAIN_MENU,
    GAMEPLAY,
    HIDEOUT,
    INVASION_HUB,
    SAGA_SELECTION,
    LEVEL_LOADING
}

data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val rewardCoins: Int,
    val isClaimed: Boolean = false
)

data class DailyRewardDay(
    val day: Int,
    val rewardCoins: Int,
    val rewardDescription: String,
    val isClaimed: Boolean,
    val isCurrentDay: Boolean
)

data class LevelProgressInfo(
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val isUnlocked: Boolean,
    val isCurrent: Boolean,
    val keys: Int,
    val bestScore: Int,
    val totalScrews: Int,
    val totalPlates: Int
)
