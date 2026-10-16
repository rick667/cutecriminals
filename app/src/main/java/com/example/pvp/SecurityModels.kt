package com.example.pvp

import com.example.hideout.ArtPiece
import com.example.hideout.HideoutCatalog

/**
 * Definição descritiva de uma das 5 melhorias oficiais de defesa do Quartel:
 * 1. Complexidade: aumenta a quantidade de mecânicas únicas no puzzle de defesa
 * 2. Duração: aumenta a quantidade de camadas que o puzzle vai ter
 * 3. Resistência: aumenta a frequência do bônus de reforço de placas e parafusos extras (estilo fase 22)
 * 4. Antimedidas: impossibilita o uso de especiais (cada nível desativa um dos especiais)
 * 5. Ocultação: diminui a chance de vir uma obra caso o invasor vença o puzzle
 */
data class OfficialDefenseUpgradeDef(
    val id: String,
    val name: String,
    val description: String,
    val iconEmoji: String,
    val maxLevel: Int = 5
) {
    fun getCostCoins(currentLevel: Int): Int {
        return when (currentLevel) {
            0 -> 500
            1 -> 1500
            2 -> 4500
            3 -> 12000
            4 -> 30000
            else -> 0
        }
    }
}

object DefenseUpgradesCatalog {
    val COMPLEXIDADE = OfficialDefenseUpgradeDef(
        id = "complexidade",
        name = "Complexidade",
        description = "Aumenta a quantidade de mecânicas únicas no puzzle de defesa (parafusos contagem regressiva, travas duplas e cofres criptografados).",
        iconEmoji = "⚙️"
    )

    val DURACAO = OfficialDefenseUpgradeDef(
        id = "duracao",
        name = "Duração",
        description = "Aumenta a quantidade de camadas de blindagem e obstáculos que o invasor precisa superar.",
        iconEmoji = "🧱"
    )

    val RESISTENCIA = OfficialDefenseUpgradeDef(
        id = "resistencia",
        name = "Resistência",
        description = "Aumenta a frequência com que bônus de placas e parafusos extras de reforço entram no tabuleiro (estilo fase 22).",
        iconEmoji = "🛡️"
    )

    val ANTIMEDIDAS = OfficialDefenseUpgradeDef(
        id = "antimedidas",
        name = "Antimedidas",
        description = "Bloqueia o uso de especiais pelo invasor. Cada nível desativa uma ferramenta tática (Slots, Desfazer, Chave, Bomba).",
        iconEmoji = "🚫"
    )

    val OCULTACAO = OfficialDefenseUpgradeDef(
        id = "ocultacao",
        name = "Ocultação",
        description = "Diminui expressivamente a chance do invasor saquear uma obra de arte rara do seu cofre caso consiga vencer o puzzle.",
        iconEmoji = "🎭"
    )

    val ALL_UPGRADES = listOf(COMPLEXIDADE, DURACAO, RESISTENCIA, ANTIMEDIDAS, OCULTACAO)
}

/**
 * Perfil de Segurança Oficial do Jogador / Quartel.
 * Todas as melhorias custam APENAS OURO e exigem manutenção periódica contra desgaste.
 */
data class SecurityProfile(
    val complexidade: Int = 1,
    val duracao: Int = 1,
    val resistencia: Int = 1,
    val antimedidas: Int = 0,
    val ocultacao: Int = 0,
    val lastMaintainedTimestamp: Long = System.currentTimeMillis(),
    val degradedUpgradeId: String? = null,
    // Propriedades legadas mantidas para retrocompatibilidade
    val extraLocksLevel: Int = 0,
    val fastAlarmLevel: Int = 0,
    val decoyScrewsLevel: Int = 0,
    val runeCipherLevel: Int = 0
) {
    /**
     * Nível efetivo levando em conta eventual desgaste por falta de manutenção periódica.
     */
    fun getEffectiveLevel(upgradeId: String): Int {
        val baseLvl = when (upgradeId) {
            "complexidade" -> complexidade
            "duracao" -> duracao
            "resistencia" -> resistencia
            "antimedidas" -> antimedidas
            "ocultacao" -> ocultacao
            else -> 0
        }
        return if (degradedUpgradeId == upgradeId) {
            (baseLvl - 1).coerceAtLeast(0)
        } else {
            baseLvl
        }
    }

    val totalDefenseRating: Int
        get() = (getEffectiveLevel("complexidade") * 35) +
                (getEffectiveLevel("duracao") * 40) +
                (getEffectiveLevel("resistencia") * 30) +
                (getEffectiveLevel("antimedidas") * 45) +
                (getEffectiveLevel("ocultacao") * 25)

    fun getAlarmSeconds(): Int {
        val base = 120
        val reduction = getEffectiveLevel("antimedidas") * 10
        return (base - reduction).coerceAtLeast(60)
    }

    /**
     * Retorna se alguma ferramenta especial do jogador invasor está desativada por antimedidas:
     * - Nível 1: Desativa Slot Extra
     * - Nível 2: Desativa Desfazer
     * - Nível 3: Desativa Chave de Fenda
     * - Nível 4: Desativa Bomba
     * - Nível 5: Desativa todos os itens e assistências
     */
    fun isBoosterDisabled(boosterType: String): Boolean {
        val antiLvl = getEffectiveLevel("antimedidas")
        return when (boosterType) {
            "EXTRA_SLOT" -> antiLvl >= 1
            "UNDO" -> antiLvl >= 2
            "SCREWDRIVER" -> antiLvl >= 3
            "BOMB" -> antiLvl >= 4
            else -> antiLvl >= 5
        }
    }
}

/**
 * Modelo de Oponente para Invasões e Matchmaking.
 */
data class InvasionOpponent(
    val id: String,
    val name: String,
    val title: String,
    val avatarEmoji: String,
    val renown: Int,
    val securityProfile: SecurityProfile,
    val targetArtPieceId: String,
    val rewardCoins: Int = 450,
    val rewardRenown: Int = 25,
    val isBot: Boolean = false
)

data class SafeReward(
    val isArtPiece: Boolean,
    val artPiece: ArtPiece?,
    val coins: Int,
    val title: String,
    val subtitle: String
)

data class InvasionGachaState(
    val opponent: InvasionOpponent,
    val safes: List<SafeReward>,
    val chosenSafeIndex: Int? = null,
    val isRevealed: Boolean = false
)

object OpponentCatalog {

    /**
     * Calcula o ganho de renome com base na diferença entre jogador e defensor.
     * Quanto maior o renome do adversário em relação ao jogador, maior a recompensa.
     */
    fun calculateRenownReward(playerRenown: Int, opponentRenown: Int): Int {
        val diff = opponentRenown - playerRenown
        return (22 + (diff / 10)).coerceIn(8, 65)
    }

    /**
     * Gera bots calibrados ao redor do renome do jogador, ordenados do mais baixo ao mais alto.
     */
    fun generate3BotsAroundRenown(playerRenown: Int): List<InvasionOpponent> {
        val artList = HideoutCatalog.getDefaultCatalog()
        val art1 = artList.getOrNull(0)?.id ?: "noiteestrelada_art"
        val art2 = artList.getOrNull(1)?.id ?: "opensador_escultura"
        val art3 = artList.getOrNull(2)?.id ?: "amulherdobrincoperola_art"

        // 1. Alvo Mais Baixo
        val lowRenown = (playerRenown - 75).coerceAtLeast(100)
        val oppLow = InvasionOpponent(
            id = "bot_apprentice_${lowRenown}",
            name = "Lord Archibald",
            title = "O Antiquário Solitário",
            avatarEmoji = "🧐",
            renown = lowRenown,
            securityProfile = SecurityProfile(
                complexidade = 1,
                duracao = 1,
                resistencia = 0,
                antimedidas = 0,
                ocultacao = 0
            ),
            targetArtPieceId = art1,
            rewardCoins = 320,
            rewardRenown = calculateRenownReward(playerRenown, lowRenown),
            isBot = true
        )

        // 2. Alvo Equilibrado
        val midRenown = playerRenown + 15
        val oppMid = InvasionOpponent(
            id = "bot_baroness_${midRenown}",
            name = "Baronesa Von Mint",
            title = "Colecionadora de Mármores",
            avatarEmoji = "👒",
            renown = midRenown,
            securityProfile = SecurityProfile(
                complexidade = 2,
                duracao = 2,
                resistencia = 1,
                antimedidas = 1,
                ocultacao = 1
            ),
            targetArtPieceId = art2,
            rewardCoins = 550,
            rewardRenown = calculateRenownReward(playerRenown, midRenown),
            isBot = true
        )

        // 3. Alvo Desafiador (Maior Renome)
        val highRenown = playerRenown + 120
        val oppHigh = InvasionOpponent(
            id = "bot_clockwork_${highRenown}",
            name = "Duque Mecânico de Viena",
            title = "Mestre dos Cofres & Engrenagens",
            avatarEmoji = "🎩",
            renown = highRenown,
            securityProfile = SecurityProfile(
                complexidade = 3,
                duracao = 3,
                resistencia = 2,
                antimedidas = 2,
                ocultacao = 2
            ),
            targetArtPieceId = art3,
            rewardCoins = 900,
            rewardRenown = calculateRenownReward(playerRenown, highRenown),
            isBot = true
        )

        return listOf(oppLow, oppMid, oppHigh)
    }

    /**
     * Compatibilidade retroativa com código existente.
     */
    fun getAvailableOpponents(playerLevel: Int): List<InvasionOpponent> {
        return generate3BotsAroundRenown(1000 + (playerLevel * 20))
    }
}

/**
 * Registro de histórico de defesas do jogador:
 * Exibe ataques recebidos, se o invasor venceu ou falhou,
 * e principalmente se conseguiu ou não roubar uma obra de arte do jogador.
 */
data class DefenseHistoryRecord(
    val id: String,
    val attackerName: String,
    val attackerAvatar: String,
    val attackerRenown: Int,
    val timeAgo: String,
    val isDefended: Boolean, // true se o invasor falhou e o alarme disparou
    val artworkStolen: Boolean, // true se o atacante conseguiu roubar uma obra do cofre
    val stolenArtPieceName: String?,
    val renownDelta: Int, // ex: +18 ou -12
    val coinsDefended: Int
)
