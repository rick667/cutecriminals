package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.game.ScrewGameState
import com.example.game.ScrewGameViewModel
import com.example.pvp.InvasionHubScreen

/**
 * Tela do Quartel (Defesas, PvP e Assaltos).
 * Integra o hub de invasões e gerenciamento de defesas com navegação intuitiva.
 */
@Composable
fun QuartelScreen(
    viewModel: ScrewGameViewModel,
    state: ScrewGameState,
    modifier: Modifier = Modifier
) {
    InvasionHubScreen(
        viewModel = viewModel,
        onBack = {
            // Volta para a aba principal de refúgio se o jogador usar o botão voltar
            viewModel.haptics.playScrewClick()
        },
        modifier = modifier.fillMaxSize()
    )
}
