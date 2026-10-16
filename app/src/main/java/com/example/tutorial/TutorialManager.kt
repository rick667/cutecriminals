package com.example.tutorial

import android.content.Context
import android.content.SharedPreferences

object TutorialManager {
    private const val PREFS_NAME = "game_tutorial_preferences"
    private const val KEY_TUTORIAL_COMPLETED = "tutorial_completed"
    private const val KEY_CURRENT_STEP = "tutorial_current_step"

    fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isTutorialCompleted(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_TUTORIAL_COMPLETED, false)
    }

    fun setTutorialCompleted(context: Context, completed: Boolean) {
        getPreferences(context).edit()
            .putBoolean(KEY_TUTORIAL_COMPLETED, completed)
            .putString(KEY_CURRENT_STEP, if (completed) TutorialStep.STEP_COMPLETED.name else TutorialStep.STEP_1_INITIAL_FREEZE.name)
            .apply()
    }

    fun getCurrentStep(context: Context): TutorialStep {
        if (isTutorialCompleted(context)) {
            return TutorialStep.STEP_COMPLETED
        }
        val stepName = getPreferences(context).getString(KEY_CURRENT_STEP, TutorialStep.STEP_1_INITIAL_FREEZE.name)
        return try {
            TutorialStep.valueOf(stepName ?: TutorialStep.STEP_1_INITIAL_FREEZE.name)
        } catch (e: Exception) {
            TutorialStep.STEP_1_INITIAL_FREEZE
        }
    }

    fun setStep(context: Context, step: TutorialStep) {
        val completed = (step == TutorialStep.STEP_COMPLETED)
        getPreferences(context).edit()
            .putString(KEY_CURRENT_STEP, step.name)
            .putBoolean(KEY_TUTORIAL_COMPLETED, completed)
            .apply()
    }

    fun resetTutorial(context: Context) {
        getPreferences(context).edit()
            .putBoolean(KEY_TUTORIAL_COMPLETED, false)
            .putString(KEY_CURRENT_STEP, TutorialStep.STEP_1_INITIAL_FREEZE.name)
            .apply()
    }
}
