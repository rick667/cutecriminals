package com.example.tutorial

/**
 * 10 Etapas Estruturadas do Tutorial Oficial do Jogo:
 *
 * 1. Primeiro contato: tela inicial (refúgio com background 4), interação bloqueada.
 * 2. Após poucos segundos: Marcel surge com diálogo (tela levemente escurecida).
 * 3. Final do diálogo: destaca o botão "JOGAR SAGAS" (apenas ele clicável).
 * 4. Destaque da primeira saga (apenas ela clicável).
 * 5. Destaque da Fase 1 da primeira saga (apenas ela clicável).
 * 6. Ao iniciar a Fase 1: Poddle NÃO aparece em pessoa, mas surge uma cartinha dele avisando para não mexer nos cofres.
 * 7. Fim da Fase 1: recompensa OBRIGATORIAMENTE OURO; Marcel aparece e diz que está tudo bem não achar obra de primeira e dará uma de presente.
 * 8. Marcel dá uma das obras que NÃO estão listadas nas sagas (Os Jogadores de Cartas - Cézanne, sagaId = 0).
 * 9. De volta ao refúgio: Marcel ensina a abrir inventário e colocar a nova obra na parede.
 * 10. Marcel diz que isso é tudo por hora e que já podemos explorar sozinhos. Início do jogo normal!
 */
enum class TutorialStep(val stepNumber: Int) {
    STEP_1_INITIAL_FREEZE(1),
    STEP_2_MARCEL_DIALOGUE(2),
    STEP_3_HIGHLIGHT_JOGAR_SAGAS(3),
    STEP_4_HIGHLIGHT_SAGA_1(4),
    STEP_5_HIGHLIGHT_LEVEL_1(5),
    STEP_6_PODDLE_LETTER(6),
    STEP_7_LEVEL_1_PLAYING(7),
    STEP_7_REWARD_GOLD(7),
    STEP_7_MARCEL_GIFT_DIALOGUE(7),
    STEP_8_GIFT_REVEAL(8),
    STEP_9_GUIDE_OPEN_INVENTORY(9),
    STEP_9_GUIDE_SELECT_GIFT_ART(9),
    STEP_9_GUIDE_CONFIRM_PLACEMENT(9),
    STEP_10_MARCEL_FAREWELL(10),
    STEP_COMPLETED(11);

    val isCompleted: Boolean
        get() = this == STEP_COMPLETED
}
