package com.example.pvp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.FirestoreGameRepository
import com.example.data.model.PvPDefense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Gerenciador central do Modo Quartel / PvP:
 * 1. Controle do contador de pesquisas diárias (5/5) e impedimento de atacar o mesmo oponente 2x no mesmo dia.
 * 2. Gestão das 5 melhorias oficiais de defesa (custo apenas em ouro, manutenção periódica contra desgaste).
 * 3. Criação e sincronização automática da base de defesa e renome no Firestore para todo jogador logado.
 * 4. Matchmaking com radar tático que busca 3 jogadores/bots ordenados do menor para o maior renome.
 */
class PvPDefenseManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("pvp_defense_prefs", Context.MODE_PRIVATE)
    private val firestoreRepo = FirestoreGameRepository(context)

    companion object {
        private const val TAG = "PvPDefenseManager"
        const val MAX_DAILY_SEARCHES = 5
        private const val MAINTENANCE_PERIOD_MS = 24 * 60 * 60 * 1000L // 24 horas para ciclo de manutenção
    }

    // --- CONTADOR DE PESQUISAS DIÁRIAS (5/5) ---

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    private fun checkAndResetDailyQuota() {
        val today = getTodayDateString()
        val lastDate = prefs.getString("daily_search_date", null)
        if (lastDate != today) {
            prefs.edit()
                .putString("daily_search_date", today)
                .putInt("daily_searches_remaining", MAX_DAILY_SEARCHES)
                .putStringSet("attacked_opponents_today", emptySet())
                .apply()
        }
    }

    fun getDailySearchesRemaining(): Int {
        checkAndResetDailyQuota()
        return prefs.getInt("daily_searches_remaining", MAX_DAILY_SEARCHES)
    }

    fun getAttackedOpponentsToday(): Set<String> {
        checkAndResetDailyQuota()
        return prefs.getStringSet("attacked_opponents_today", emptySet()) ?: emptySet()
    }

    fun canAttackOpponent(opponentId: String): Boolean {
        checkAndResetDailyQuota()
        val remaining = getDailySearchesRemaining()
        val attacked = getAttackedOpponentsToday()
        return remaining > 0 && !attacked.contains(opponentId)
    }

    fun registerAttack(opponentId: String) {
        checkAndResetDailyQuota()
        val current = getDailySearchesRemaining()
        val newRemaining = (current - 1).coerceAtLeast(0)
        val attacked = getAttackedOpponentsToday().toMutableSet()
        attacked.add(opponentId)

        prefs.edit()
            .putInt("daily_searches_remaining", newRemaining)
            .putStringSet("attacked_opponents_today", attacked)
            .apply()
    }

    // --- PERFIL DE DEFESA LOCAL & MANUTENÇÃO ---

    fun loadLocalDefenseProfile(): SecurityProfile {
        val complexidade = prefs.getInt("def_complexidade", 1)
        val duracao = prefs.getInt("def_duracao", 1)
        val resistencia = prefs.getInt("def_resistencia", 1)
        val antimedidas = prefs.getInt("def_antimedidas", 0)
        val ocultacao = prefs.getInt("def_ocultacao", 0)
        val lastMaintained = prefs.getLong("def_last_maintained", System.currentTimeMillis())
        var degradedId = prefs.getString("def_degraded_id", null)

        // Verificação de desgaste periódico por falta de manutenção
        val now = System.currentTimeMillis()
        if (degradedId == null && (now - lastMaintained) > MAINTENANCE_PERIOD_MS) {
            // Escolhe uma melhoria ativa para sofrer desgaste se houver
            val activeUpgrades = mutableListOf<String>()
            if (complexidade > 0) activeUpgrades.add("complexidade")
            if (duracao > 0) activeUpgrades.add("duracao")
            if (resistencia > 0) activeUpgrades.add("resistencia")
            if (antimedidas > 0) activeUpgrades.add("antimedidas")
            if (ocultacao > 0) activeUpgrades.add("ocultacao")

            if (activeUpgrades.isNotEmpty()) {
                degradedId = activeUpgrades.random()
                prefs.edit().putString("def_degraded_id", degradedId).apply()
            }
        }

        return SecurityProfile(
            complexidade = complexidade,
            duracao = duracao,
            resistencia = resistencia,
            antimedidas = antimedidas,
            ocultacao = ocultacao,
            lastMaintainedTimestamp = lastMaintained,
            degradedUpgradeId = degradedId
        )
    }

    fun saveLocalDefenseProfile(profile: SecurityProfile) {
        prefs.edit()
            .putInt("def_complexidade", profile.complexidade)
            .putInt("def_duracao", profile.duracao)
            .putInt("def_resistencia", profile.resistencia)
            .putInt("def_antimedidas", profile.antimedidas)
            .putInt("def_ocultacao", profile.ocultacao)
            .putLong("def_last_maintained", profile.lastMaintainedTimestamp)
            .putString("def_degraded_id", profile.degradedUpgradeId)
            .apply()
    }

    fun performMaintenance(): SecurityProfile {
        val current = loadLocalDefenseProfile()
        val repaired = current.copy(
            lastMaintainedTimestamp = System.currentTimeMillis(),
            degradedUpgradeId = null
        )
        saveLocalDefenseProfile(repaired)
        return repaired
    }

    // --- SINCRONIZAÇÃO DA BASE DE DEFESA NO BANCO (FIRESTORE) ---

    /**
     * Garante que assim que um jogador novo loga no jogo, um puzzle de defesa base
     * e seu nível de renome são imediatamente registrados no Firestore.
     */
    suspend fun ensurePlayerDefenseRegistered(
        userId: String,
        displayName: String,
        avatarEmoji: String,
        playerRenown: Int
    ): SecurityProfile = withContext(Dispatchers.IO) {
        if (userId.isBlank()) return@withContext loadLocalDefenseProfile()

        val existing = firestoreRepo.getPvPDefense(userId).getOrNull()
        if (existing == null) {
            // Cria defesa base no Firestore
            val baseDefense = PvPDefense(
                userId = userId,
                displayName = displayName,
                avatarEmoji = avatarEmoji,
                renown = playerRenown,
                complexidade = 1,
                duracao = 1,
                resistencia = 1,
                antimedidas = 0,
                ocultacao = 0,
                lastMaintainedTimestamp = System.currentTimeMillis(),
                targetArtPieceId = "noiteestrelada_art"
            )
            firestoreRepo.savePvPDefense(baseDefense)
            val profile = SecurityProfile(1, 1, 1, 0, 0)
            saveLocalDefenseProfile(profile)
            profile
        } else {
            // Atualiza renome no Firestore se alterado
            if (existing.renown != playerRenown) {
                firestoreRepo.savePvPDefense(existing.copy(renown = playerRenown))
            }
            val profile = SecurityProfile(
                complexidade = existing.complexidade,
                duracao = existing.duracao,
                resistencia = existing.resistencia,
                antimedidas = existing.antimedidas,
                ocultacao = existing.ocultacao,
                lastMaintainedTimestamp = existing.lastMaintainedTimestamp
            )
            saveLocalDefenseProfile(profile)
            profile
        }
    }

    suspend fun syncDefenseProfileToCloud(
        userId: String,
        displayName: String,
        avatarEmoji: String,
        playerRenown: Int,
        profile: SecurityProfile
    ) = withContext(Dispatchers.IO) {
        if (userId.isBlank()) return@withContext
        val pvpDoc = PvPDefense(
            userId = userId,
            displayName = displayName,
            avatarEmoji = avatarEmoji,
            renown = playerRenown,
            complexidade = profile.complexidade,
            duracao = profile.duracao,
            resistencia = profile.resistencia,
            antimedidas = profile.antimedidas,
            ocultacao = profile.ocultacao,
            lastMaintainedTimestamp = profile.lastMaintainedTimestamp,
            targetArtPieceId = "noiteestrelada_art"
        )
        firestoreRepo.savePvPDefense(pvpDoc)
    }

    // --- MATCHMAKING: BUSCA 3 JOGADORES (OU BOTS) ORDENADOS POR RENOME ---

    /**
     * O botão BUSCAR traz 3 jogadores (ou bots se necessário), ordenados do menor para o maior renome.
     * Quanto maior a diferença de renome, mais renome o jogador ganha ao vencer o puzzle.
     */
    suspend fun searchThreeOpponents(
        playerRenown: Int,
        currentUserId: String
    ): List<InvasionOpponent> = withContext(Dispatchers.IO) {
        val candidates = mutableListOf<InvasionOpponent>()

        // 1. Tenta buscar jogadores reais no Firestore
        try {
            if (currentUserId.isNotBlank()) {
                val realUsers = firestoreRepo.fetchMatchmakingOpponents(playerRenown, currentUserId).getOrNull()
                realUsers?.forEach { def ->
                    val secProfile = SecurityProfile(
                        complexidade = def.complexidade,
                        duracao = def.duracao,
                        resistencia = def.resistencia,
                        antimedidas = def.antimedidas,
                        ocultacao = def.ocultacao,
                        lastMaintainedTimestamp = def.lastMaintainedTimestamp
                    )
                    candidates.add(
                        InvasionOpponent(
                            id = def.userId,
                            name = def.displayName,
                            title = "Restaurador Rival",
                            avatarEmoji = def.avatarEmoji.ifBlank { "🛡️" },
                            renown = def.renown,
                            securityProfile = secProfile,
                            targetArtPieceId = def.targetArtPieceId.ifBlank { "noiteestrelada_art" },
                            rewardCoins = 450 + (def.renown / 5),
                            rewardRenown = OpponentCatalog.calculateRenownReward(playerRenown, def.renown),
                            isBot = false
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Falha ao buscar oponentes reais no Firestore: ${e.message}")
        }

        // 2. Se não houver jogadores suficientes, completa com bots proceduralmente calibrados
        val bots = OpponentCatalog.generate3BotsAroundRenown(playerRenown)
        for (bot in bots) {
            if (candidates.size < 3 && candidates.none { it.id == bot.id }) {
                candidates.add(bot)
            }
        }

        // 3. Garante exatamente 3 oponentes distintos ordenados do menor para o maior renome
        val finalThree = candidates.take(3).sortedBy { it.renown }

        return@withContext finalThree
    }

    /**
     * Busca os melhores jogadores globais ordenados por renome diretamente do Firestore.
     */
    suspend fun loadGlobalLeaderboard(currentUserId: String): List<InvasionOpponent> = withContext(Dispatchers.IO) {
        val list = mutableListOf<InvasionOpponent>()
        try {
            val realUsers = firestoreRepo.getTopLeaderboard(20).getOrNull()
            realUsers?.forEach { def ->
                val secProfile = SecurityProfile(
                    complexidade = def.complexidade,
                    duracao = def.duracao,
                    resistencia = def.resistencia,
                    antimedidas = def.antimedidas,
                    ocultacao = def.ocultacao,
                    lastMaintainedTimestamp = def.lastMaintainedTimestamp
                )
                list.add(
                    InvasionOpponent(
                        id = def.userId,
                        name = def.displayName.ifBlank { "Restaurador" },
                        title = "Restaurador de Renome",
                        avatarEmoji = def.avatarEmoji.ifBlank { "🏆" },
                        renown = def.renown,
                        securityProfile = secProfile,
                        targetArtPieceId = def.targetArtPieceId.ifBlank { "noiteestrelada_art" },
                        rewardCoins = 500,
                        rewardRenown = 30,
                        isBot = false
                    )
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Falha ao buscar leaderboard do Firestore: ${e.message}")
        }

        // Se houver poucos jogadores no banco, insere bots líderes padrão para ranking completo
        val defaultBots = listOf(
            InvasionOpponent("bot_top_1", "Mestre Leonardo", "Colecionador Lendário", "🏛️", 2450, SecurityProfile(5,5,5,3,3,0L), "noiteestrelada_art", 1000, 50, true),
            InvasionOpponent("bot_top_2", "Barão da Arte", "Estrategista de Defesa", "👑", 2100, SecurityProfile(4,4,5,2,2,0L), "monalisa_art", 850, 40, true),
            InvasionOpponent("bot_top_3", "Dama Mística", "Restauradora de Elixires", "🔮", 1850, SecurityProfile(4,3,4,2,2,0L), "moca_brinco_art", 750, 35, true),
            InvasionOpponent("bot_top_4", "Guardião Fênix", "Protetor do Acervo", "🦅", 1600, SecurityProfile(3,3,4,1,1,0L), "nacimento_venus_art", 650, 30, true),
            InvasionOpponent("bot_top_5", "Sombra Dourada", "Lorde da Oficina", "⚜️", 1400, SecurityProfile(3,3,3,1,1,0L), "grito_art", 550, 25, true)
        )
        for (bot in defaultBots) {
            if (list.none { it.id == bot.id }) {
                list.add(bot)
            }
        }

        return@withContext list.sortedByDescending { it.renown }
    }

    // --- HISTÓRICO DE DEFESAS ---

    fun loadDefenseHistory(): List<DefenseHistoryRecord> {
        val count = prefs.getInt("def_history_count", -1)
        if (count <= 0) {
            // Histórico padrão inicial para dar vida ao jogo
            return listOf(
                DefenseHistoryRecord(
                    id = "hist_1",
                    attackerName = "Lord Archibald",
                    attackerAvatar = "🧐",
                    attackerRenown = 1340,
                    timeAgo = "Há 1 hora",
                    isDefended = true,
                    artworkStolen = false,
                    stolenArtPieceName = null,
                    renownDelta = 18,
                    coinsDefended = 320
                ),
                DefenseHistoryRecord(
                    id = "hist_2",
                    attackerName = "Baronesa Von Mint",
                    attackerAvatar = "👒",
                    attackerRenown = 1480,
                    timeAgo = "Ontem às 19:30",
                    isDefended = false,
                    artworkStolen = false,
                    stolenArtPieceName = null,
                    renownDelta = -6,
                    coinsDefended = 180
                ),
                DefenseHistoryRecord(
                    id = "hist_3",
                    attackerName = "Duque Mecânico",
                    attackerAvatar = "🎩",
                    attackerRenown = 1590,
                    timeAgo = "Ontem às 11:15",
                    isDefended = true,
                    artworkStolen = false,
                    stolenArtPieceName = null,
                    renownDelta = 24,
                    coinsDefended = 450
                ),
                DefenseHistoryRecord(
                    id = "hist_4",
                    attackerName = "Colecionador das Sombras",
                    attackerAvatar = "🕶️",
                    attackerRenown = 1650,
                    timeAgo = "Há 3 dias",
                    isDefended = false,
                    artworkStolen = true,
                    stolenArtPieceName = "O Pensador",
                    renownDelta = -15,
                    coinsDefended = 0
                )
            )
        }

        val list = mutableListOf<DefenseHistoryRecord>()
        for (i in 0 until count) {
            val id = prefs.getString("def_hist_${i}_id", "hist_$i") ?: "hist_$i"
            val name = prefs.getString("def_hist_${i}_name", "Invasor") ?: "Invasor"
            val avatar = prefs.getString("def_hist_${i}_avatar", "👤") ?: "👤"
            val renown = prefs.getInt("def_hist_${i}_renown", 1000)
            val time = prefs.getString("def_hist_${i}_time", "Recentemente") ?: "Recentemente"
            val defended = prefs.getBoolean("def_hist_${i}_defended", true)
            val stolen = prefs.getBoolean("def_hist_${i}_stolen", false)
            val artName = prefs.getString("def_hist_${i}_artName", null)
            val rDelta = prefs.getInt("def_hist_${i}_rDelta", 0)
            val coins = prefs.getInt("def_hist_${i}_coins", 0)

            list.add(
                DefenseHistoryRecord(
                    id = id,
                    attackerName = name,
                    attackerAvatar = avatar,
                    attackerRenown = renown,
                    timeAgo = time,
                    isDefended = defended,
                    artworkStolen = stolen,
                    stolenArtPieceName = artName,
                    renownDelta = rDelta,
                    coinsDefended = coins
                )
            )
        }
        return list
    }

    fun recordDefenseEvent(record: DefenseHistoryRecord) {
        val current = loadDefenseHistory().toMutableList()
        current.add(0, record)
        val trimmed = current.take(20)

        val editor = prefs.edit()
        editor.putInt("def_history_count", trimmed.size)
        trimmed.forEachIndexed { i, item ->
            editor.putString("def_hist_${i}_id", item.id)
            editor.putString("def_hist_${i}_name", item.attackerName)
            editor.putString("def_hist_${i}_avatar", item.attackerAvatar)
            editor.putInt("def_hist_${i}_renown", item.attackerRenown)
            editor.putString("def_hist_${i}_time", item.timeAgo)
            editor.putBoolean("def_hist_${i}_defended", item.isDefended)
            editor.putBoolean("def_hist_${i}_stolen", item.artworkStolen)
            editor.putString("def_hist_${i}_artName", item.stolenArtPieceName)
            editor.putInt("def_hist_${i}_rDelta", item.renownDelta)
            editor.putInt("def_hist_${i}_coins", item.coinsDefended)
        }
        editor.apply()
    }
}
