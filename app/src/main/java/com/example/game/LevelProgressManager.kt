package com.example.game

import android.content.Context
import org.json.JSONObject
import java.io.File

/**
 * Gerencia o salvamento persistente das 3 chaves ganhas em cada fase específica.
 * Garante que:
 * 1ª chave: Vencer a fase (level cleared)
 * 2ª chave: Pontuação >= Limiar da dificuldade (1000 fácil, 2000 médio, 4000 difícil)
 * 3ª chave: Terminar sem parafusos órfãos / sobressalentes na doca
 */
object LevelProgressManager {

    private const val PREFS_FILE = "level_keys_progress.json"

    data class LevelStarsResult(
        val key1Cleared: Boolean,
        val key2ScoreAchieved: Boolean,
        val key3CleanDock: Boolean,
        val scoreThreshold: Int
    ) {
        val totalKeys: Int
            get() = (if (key1Cleared) 1 else 0) + (if (key2ScoreAchieved) 1 else 0) + (if (key3CleanDock) 1 else 0)
    }

    /**
     * Calcula as 3 chaves obtidas na partida atual.
     */
    fun evaluateMatchKeys(
        levelNumber: Int,
        isCleared: Boolean,
        score: Int,
        dockScrews: List<Screw?>
    ): LevelStarsResult {
        val scoreThreshold = LevelScoreThresholds.getScoreThresholdForLevel(levelNumber)
        val key1 = isCleared
        val key2 = isCleared && score >= scoreThreshold
        // 3ª chave: Conclusão limpa da fase (vencer sem falhar por lotar a doca)
        val key3 = isCleared

        return LevelStarsResult(
            key1Cleared = key1,
            key2ScoreAchieved = key2,
            key3CleanDock = key3,
            scoreThreshold = scoreThreshold
        )
    }

    /**
     * Retorna a quantidade de chaves (0 a 3) já conquistadas pelo jogador nesta fase.
     */
    fun getSavedKeysForLevel(context: Context, levelNumber: Int): Int {
        val file = File(context.filesDir, PREFS_FILE)
        if (!file.exists()) return 0
        return try {
            val json = JSONObject(file.readText())
            json.optInt("level_$levelNumber", 0)
        } catch (_: Exception) {
            0
        }
    }

    /**
     * Registra o progresso do jogador ao vencer a fase.
     * Retorna o ganho líquido de novas chaves para o saldo global.
     */
    fun recordLevelClearedAndGetNewKeys(
        context: Context,
        levelNumber: Int,
        result: LevelStarsResult
    ): Int {
        val prevKeys = getSavedKeysForLevel(context, levelNumber)
        val earnedKeys = result.totalKeys
        if (earnedKeys > prevKeys) {
            val file = File(context.filesDir, PREFS_FILE)
            val json = if (file.exists()) {
                try { JSONObject(file.readText()) } catch (_: Exception) { JSONObject() }
            } else {
                JSONObject()
            }
            json.put("level_$levelNumber", earnedKeys)
            try { file.writeText(json.toString()) } catch (_: Exception) {}
            return earnedKeys - prevKeys
        }
        return 0
    }

    /**
     * Retorna a soma de todas as chaves conquistadas em todas as fases da saga informada.
     */
    fun getTotalKeysEarnedInSaga(context: Context, sagaId: Int): Int {
        val startLevel = (sagaId - 1) * 20 + 1
        val endLevel = sagaId * 20
        var total = 0
        for (lvl in startLevel..endLevel) {
            total += getSavedKeysForLevel(context, lvl)
        }
        return total
    }

    /**
     * Retorna o total geral de chaves acumuladas pelo jogador em todas as fases.
     */
    fun getTotalKeysEarnedAcrossAllLevels(context: Context): Int {
        var total = 0
        for (lvl in 1..LevelRegistry.MAX_LEVELS) {
            total += getSavedKeysForLevel(context, lvl)
        }
        return total
    }
}
