package com.example

import com.example.game.AssaultModifier
import com.example.game.QuickPlayDifficulty
import com.example.game.QuickPlayManager
import com.example.game.SagaLevelGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickPlayManagerTest {

    @Test
    fun getUnlockedMechanics_progressionThresholds() {
        // Abaixo do nível 6: nenhuma mecânica
        val m1 = QuickPlayManager.getUnlockedMechanics(1)
        assertTrue(m1.isEmpty())

        // Nível 6: desbloqueia PATTERN
        val m6 = QuickPlayManager.getUnlockedMechanics(6)
        assertTrue(m6.contains(SagaLevelGenerator.SpecialMechanicType.PATTERN))
        assertEquals(1, m6.size)

        // Nível 10: desbloqueia COUNTDOWN
        val m10 = QuickPlayManager.getUnlockedMechanics(10)
        assertTrue(m10.contains(SagaLevelGenerator.SpecialMechanicType.COUNTDOWN))

        // Nível 18: desbloqueia ICE
        val m18 = QuickPlayManager.getUnlockedMechanics(18)
        assertTrue(m18.contains(SagaLevelGenerator.SpecialMechanicType.ICE))

        // Nível 50: todas as 7 mecânicas
        val m50 = QuickPlayManager.getUnlockedMechanics(50)
        assertEquals(7, m50.size)
    }

    @Test
    fun generatePuzzle_easyDifficulty_structureAndSolvability() {
        val puzzle = QuickPlayManager.generatePuzzle(QuickPlayDifficulty.EASY, unlockedMaxLevel = 10, seed = 42L)
        assertNotNull(puzzle)
        // 6 trios = 18 parafusos
        assertEquals(18, puzzle.screws.size)
        // Sem modificadores de fase no modo fácil
        assertTrue(puzzle.activeModifiers.isEmpty())
        assertEquals("Jogo Rápido • Fácil", puzzle.metadata.title)
        // Múltiplo exato de 3 garantido
        assertEquals(0, puzzle.screws.size % 3)
    }

    @Test
    fun generatePuzzle_normalDifficulty_hasMechanics() {
        val puzzle = QuickPlayManager.generatePuzzle(QuickPlayDifficulty.NORMAL, unlockedMaxLevel = 20, seed = 123L)
        assertNotNull(puzzle)
        // 8 trios = 24 parafusos
        assertEquals(24, puzzle.screws.size)
        assertTrue(puzzle.activeModifiers.isEmpty())
        assertEquals("Jogo Rápido • Normal", puzzle.metadata.title)
    }

    @Test
    fun generatePuzzle_hardDifficulty_hasMultipleMechanicsAndModifiers() {
        val puzzle = QuickPlayManager.generatePuzzle(QuickPlayDifficulty.HARD, unlockedMaxLevel = 50, seed = 999L)
        assertNotNull(puzzle)
        // 10 trios = 30 parafusos
        assertEquals(30, puzzle.screws.size)
        // Modo difícil sempre tem pelo menos 1 modificador estilo assalto
        assertFalse(puzzle.activeModifiers.isEmpty())
        assertEquals("Jogo Rápido • Difícil", puzzle.metadata.title)
    }
}
