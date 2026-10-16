package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_save")
data class GameSaveEntity(
    @PrimaryKey val id: Int = 1,
    val currentLevel: Int = 1,
    val unlockedMaxLevel: Int = 1,
    val score: Int = 0,
    val coins: Int = 0,
    val maxDockSlots: Int = 5,
    val dockScrewsSerialized: String = "",
    val unscrewedScrewIdsSerialized: String = "",
    val fallenPlateIdsSerialized: String = "",
    val hangingPlatesSerialized: String = "",
    val comboStreak: Int = 0,
    val comboMultiplier: Float = 1.0f,
    val highestComboStreak: Int = 0,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
