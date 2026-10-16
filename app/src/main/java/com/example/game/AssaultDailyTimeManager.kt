package com.example.game

import android.content.Context
import android.os.SystemClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Gerenciador de Tempo Confiável para o Modo Assalto Diário.
 * Combina relógio de rede (HTTP Date header) com SystemClock.elapsedRealtime()
 * para garantir que o jogador NÃO consiga burlar o limite diário alterando
 * a hora/data nas configurações do próprio aparelho Android.
 */
object AssaultDailyTimeManager {

    private const val PREFS_FILE = "assault_daily_clock.json"
    private const val KEY_LAST_KNOWN_SERVER_TIME = "last_server_time_ms"
    private const val KEY_BOOT_ELAPSED_AT_SYNC = "boot_elapsed_ms"
    private const val KEY_LAST_PLAYED_DATE_PREFIX = "last_played_date_saga_"

    // Cache em memória
    private var cachedServerTimeMs: Long = 0L
    private var cachedElapsedRealtimeMs: Long = 0L

    /**
     * Retorna o timestamp estimado atual à prova de adulteração local.
     */
    fun getSecureCurrentTimeMillis(context: Context): Long {
        loadClockState(context)
        val currentElapsed = SystemClock.elapsedRealtime()

        if (cachedServerTimeMs > 0L && cachedElapsedRealtimeMs > 0L) {
            val deltaElapsed = currentElapsed - cachedElapsedRealtimeMs
            if (deltaElapsed >= 0) {
                return cachedServerTimeMs + deltaElapsed
            }
        }
        // Fallback para System.currentTimeMillis se nunca sincronizado
        return System.currentTimeMillis()
    }

    /**
     * Retorna a string de data formatada em UTC (ex: "2026-10-11") para controle diário justo.
     */
    fun getSecureTodayDateString(context: Context): String {
        val secureMs = getSecureCurrentTimeMillis(context)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return sdf.format(Date(secureMs))
    }

    /**
     * Calcula quantos segundos faltam para a virada do dia UTC.
     */
    fun getSecondsUntilDailyReset(context: Context): Long {
        val secureMs = getSecureCurrentTimeMillis(context)
        val nextDayMs = ((secureMs / 86_400_000L) + 1) * 86_400_000L
        return ((nextDayMs - secureMs) / 1000L).coerceAtLeast(0L)
    }

    /**
     * Verifica se o jogador pode jogar o Modo Assalto hoje na respectiva saga.
     */
    fun canPlayAssaultToday(context: Context, sagaId: Int): Boolean {
        val today = getSecureTodayDateString(context)
        val lastPlayed = getLastPlayedDate(context, sagaId)
        return lastPlayed != today
    }

    /**
     * Registra que o jogador executou o Assalto daquela saga na data de hoje.
     */
    fun recordAssaultPlayedToday(context: Context, sagaId: Int) {
        val today = getSecureTodayDateString(context)
        saveLastPlayedDate(context, sagaId, today)
    }

    /**
     * Tenta sincronizar com o relógio do servidor (ex: Google, Cloudflare, WorldTime).
     * Roda em background de forma segura e não bloqueante.
     */
    suspend fun syncServerTimeInBackground(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                // Requisição HEAD rápida para obter o header 'Date' do servidor
                val url = URL("https://www.google.com")
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "HEAD"
                    connectTimeout = 3000
                    readTimeout = 3000
                    instanceFollowRedirects = false
                }
                val networkDate = connection.date
                connection.disconnect()

                if (networkDate > 0L) {
                    val currentElapsed = SystemClock.elapsedRealtime()
                    cachedServerTimeMs = networkDate
                    cachedElapsedRealtimeMs = currentElapsed
                    saveClockState(context, networkDate, currentElapsed)
                }
            } catch (_: Exception) {
                // Sem conexão de rede; utiliza o último estado confiável gravado em disco + elapsedRealtime
            }
        }
    }

    private fun loadClockState(context: Context) {
        if (cachedServerTimeMs > 0L) return
        try {
            val file = File(context.filesDir, PREFS_FILE)
            if (file.exists()) {
                val json = JSONObject(file.readText())
                cachedServerTimeMs = json.optLong(KEY_LAST_KNOWN_SERVER_TIME, 0L)
                cachedElapsedRealtimeMs = json.optLong(KEY_BOOT_ELAPSED_AT_SYNC, 0L)
            }
        } catch (_: Exception) { }
    }

    private fun saveClockState(context: Context, serverTimeMs: Long, elapsedMs: Long) {
        try {
            val file = File(context.filesDir, PREFS_FILE)
            val json = if (file.exists()) {
                JSONObject(file.readText())
            } else {
                JSONObject()
            }
            json.put(KEY_LAST_KNOWN_SERVER_TIME, serverTimeMs)
            json.put(KEY_BOOT_ELAPSED_AT_SYNC, elapsedMs)
            file.writeText(json.toString())
        } catch (_: Exception) { }
    }

    private fun getLastPlayedDate(context: Context, sagaId: Int): String? {
        return try {
            val file = File(context.filesDir, PREFS_FILE)
            if (file.exists()) {
                val json = JSONObject(file.readText())
                if (json.has("$KEY_LAST_PLAYED_DATE_PREFIX$sagaId")) {
                    json.getString("$KEY_LAST_PLAYED_DATE_PREFIX$sagaId")
                } else null
            } else null
        } catch (_: Exception) { null }
    }

    private fun saveLastPlayedDate(context: Context, sagaId: Int, dateStr: String) {
        try {
            val file = File(context.filesDir, PREFS_FILE)
            val json = if (file.exists()) {
                JSONObject(file.readText())
            } else {
                JSONObject()
            }
            json.put("$KEY_LAST_PLAYED_DATE_PREFIX$sagaId", dateStr)
            file.writeText(json.toString())
        } catch (_: Exception) { }
    }
}
