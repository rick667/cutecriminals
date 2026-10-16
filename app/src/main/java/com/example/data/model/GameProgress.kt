package com.example.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class GameProgress(
    val userId: String = "",
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
    val stars: Int = 0,
    @ServerTimestamp val lastUpdatedTimestamp: Date? = null
)
